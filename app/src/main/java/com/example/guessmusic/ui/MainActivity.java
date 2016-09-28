package com.example.guessmusic.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.guessmusic.R;
import com.example.guessmusic.data.Const;
import com.example.guessmusic.model.IAlertDialogButtonListener;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.Song;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.myui.MyGridView;
import com.example.guessmusic.util.MyLog;
import com.example.guessmusic.util.MyPlayer;
import com.example.guessmusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements IWordButtonClickListener {
	
	public final static String TAG = "MainActivity";
	
	/** 答案状态-- 正确  */
	public final static int STATUS_ANSWER_RIGHT = 1;
	
	/** 答案状态-- 错误  */
	public final static int STATUS_ANSWER_WRONG = 2;
	
	/** 答案状态-- 不完整  */
	public final static int STATUS_ANSWER_LACK = 3;
	
	// 闪烁次数
	public final static int SPASH_TIMES = 6;
	
	public final static int ID_DIALOG_DELETE_WORD = 1;
	
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	
	public final static int ID_DIALOG_LACK_COINS = 3;
	
	// 唱片相关动画
	private Animation mPanAnim;
	// 它代表动画运动速度 线性速度
	private LinearInterpolator mPanLin;

	// 拨杆往左动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	// 拨杆往右动画
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	// 唱片控件
	private ImageView mViewPan;
	
	// 拨杆控件
	private ImageView mViewPanBar;

	// 当前关索引(过关界面中的)
	private TextView mCurrentStagePassView;

	// 当前关索引(游戏界面中的)
	private TextView mCurrentStageView;
	
	//当前歌曲名称
	private TextView mCurrentSongNamePassView;
	
	// Play 按键事件
	private ImageButton mBtnPlayStart;
	
	// 过关界面
	private View mPassView;
	
	// 当前动画是否正在运行
	private boolean mIsRunning = false;

	// （待选择）文字框容器
	private ArrayList<WordButton> mAllWords;

	// （已选择）文字框容器
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	
	// 当前的歌曲
	private Song mCurrentSong;
	
	// 当前关的索引
	private int mCurrentStageIndex = -1;
	
	// 当前金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	// 金币View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 读取数据
		int[] datas = Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		
		// 初始化控件
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		
		mViewCurrentCoins = (TextView)findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		
		//注册监听
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// 初始化盘片动画
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		// 设置速度
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		// 设置动画监听
		mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 当圆盘转动结束的时候，启动拨杆退出动画
				mViewPanBar.startAnimation(mBarOutAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
		});

		// 初始化拨杆左移动画
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		// 动画结束后保持其状态（默认为false） 动画停留
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 当拨杆左移之后，启动唱片转动动画
				mViewPan.startAnimation(mPanAnim);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
		});

		// 初始化拨杆右移动画
		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 整套动画播放完毕，将拨杆显示出来，同时状态标为非动画状态
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
		});
		
		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handlePlayButton();
			}
		});
		
		// 初始化游戏数据
		initCurrentStageData();
		
		// 处理删除按键事件
		handleDeleteWord();
		
		// 处理提示按键事件
		handleTipAnswer();

	}
	
	@Override
	public void onWordButtonClick(WordButton wordButton) {
//		Toast.makeText(this, wordButton.mIndex + "", Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// 每点击一次就去检查答案中、是否正确
		// 获得答案状态
		int checkResult = checkTheAnswer();
		
		// 检查答案
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// 过关并获得相应的奖励
//			Toast.makeText(this, "STATUS_ANSWER_RIGHT", Toast.LENGTH_SHORT).show();
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// 闪烁文字并提示用户（错误提示）
			sparkTheWords();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// 设置文字颜色为白色(Normal)
			// 常规状态将文字设置回白颜色
			// 不然会导致答案错误后已选择文字框里的文字一直是红色的
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}
	
	/**
	 * 处理过关界面及事件
	 */
	private void handlePassEvent() {
		// 显示过关界面
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		// 过关的时候显示过关界面
		mPassView.setVisibility(View.VISIBLE);
		
		// 奖励金币
		handleCoins(Const.PASS_AWARD_COINS);
		
		// 停止未完成的动画
		mViewPan.clearAnimation();
		
		// 停止正在播放的音乐(如过关时需要停止音乐)
		MyPlayer.stopTheSong(MainActivity.this);
		
		// 播放音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);

		// 设置当前关数字(索引)的显示
		mCurrentStagePassView = (TextView) findViewById
				(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		
		// 显示歌曲的名称
		mCurrentSongNamePassView = (TextView) findViewById
				(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		
		// 下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 如果当前已经通关
				if (judgeAppPassed()) {
					// 进入到通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
					return; // 否则仍然会调用initCurrentStageData()，会导致数组越界
				} else {
					// 开始新一关
					// 隐藏过关界面
					mPassView.setVisibility(View.GONE);

					// 加载下一关卡数据
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * 判断是否通关，是返回true
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
 	}

	/**
	 * 清除答案 用于撤销已选文字
	 *
	 * 如果已选文字框中有文字，通过点击该文字，使已选文字框中的文字消失，待选文字框中该文字变得重新可见
	 *
	 * @param wordButton 对应已选框的那个
	 *
	 */
	private void clearTheAnswer(WordButton wordButton) {
		// 设置已选框文字不可见
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisible = false;
		
		// 设置待选框可见性
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}
	
	/**
	 * 设置答案
	 *
	 * 当点击下面的小框后，相应的要去上面的已选框上进行设置修改：
	 *
	 * @param wordButton
	 *
	 */
	private void setSelectWord(WordButton wordButton) {
		// 看一下已选文字框里面的每一个小框有没有东西
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果当前小框没有文字，这些都是一些属性的设置啦，并没有改变过view的内容
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框内容及可见性
				mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisible = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				// 记录索引，为的是以后再点击的时候，要将文字还回去
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
				
				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");
				
				// 设置待选框可见性，这里才是真的去改变view了呢
				setButtonVisiable(wordButton, View.INVISIBLE);

				// 设置了文字之后就break，不然会导致重复设置文字
				break;
				
			}
		}
	}
	
	/**
	 * 设置待选文字框是否可见，根据它内部的属性去设置它真实的显示状态。
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisible = (visibility == View.VISIBLE) ? true : false;
		
		MyLog.d(TAG, button.mIsVisible + "");
		
	}
	
	/**
	 * 处理圆盘中间的播放按钮，就是开始播放音乐
	 */
	private void handlePlayButton() {
		// 防止空指针
		if (mViewPanBar != null) {
			// 非运行状态才可以运行。当拨片右移回来以后，才能进行下一次处理
			// 防止多次点击按钮
			if (!mIsRunning) {
				mIsRunning = true;
				// 开始拨杆进入动画
				mViewPanBar.startAnimation(mBarInAnim);
				// 将按钮隐藏
				mBtnPlayStart.setVisibility(View.INVISIBLE);
				
				// 播放音乐
				MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
			}
		}
	}
	
	@Override
	public void onPause() {
		// 保存游戏数据
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);

		// 当当前应用程序退出的时候，停止动画
		mViewPan.clearAnimation();
		
		// 暂停音乐(比如切换到后台时音乐应该停止)
		MyPlayer.stopTheSong(MainActivity.this);
		
		super.onPause();
	}

	/**
	 * 获取当前关的song对象
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		// SONG_INFO是二维数组
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		
		return song;
	}
	
	/**
	 * 加载当前关的数据(每一关都会调用）
	 */
	private void initCurrentStageData() {
		
		// 读取当前关的歌曲信息
		// mCurrentStageIndex初始化为-1
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		
		// 初始化已选择的文字框
		mBtnSelectWords = initWordSelect();
		
		LayoutParams params = new LayoutParams(140, 140);
		
		// 清空原来的答案
		// 否则进入下一关之后上一关的答案文字仍然存在
		mViewWordsContainer.removeAllViews();
		
		// 增加新的答案框
		// 动态的为已选择文字框添加对应个数的小框，将已选择文字框添加到Layout mViewWordsContainer中
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
		}
		
		// 显示当前关的索引
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		
		// 获得数据
		mAllWords = initAllWord();
		
		// 更新数据------MyGridView
		mMyGridView.updateData(mAllWords);
		
		// 一开始就播放音乐（打开应用就开始播放音乐）
		handlePlayButton();
	}
	
	/**
	 * 初始化待选文字框
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		// 获得所有待选文字
		String[] words = generateWords();
		
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			
			WordButton button = new WordButton();
			
			button.mWordString = words[i];
			
			data.add(button);
			
		}
		
		return data;
		
	}
	
	/**
	 * 初始化已选择文字框，就是创建每个小框，然后把它们变成列表
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		//TODO
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			// 都要转化为一个view才能添加到布局当中
			View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
			
			final WordButton holder = new WordButton();

			// mViewButton是WordButton中要被显示在页面中的部分
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			// 已选文字框和待选文字框的背景颜色是不一样的
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			// 现在还不可见，这只是个标识，并不是让mViewButton真的不可见了
			holder.mIsVisible = false;
			
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			// 小框被点击时表示里面的文字要被清除，然后下面的对应的框要显示出来
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});
			
			data.add(holder);
			
		}
		
		return data;
		
	}
	
	/**
	 * 生成所有的待选文字
	 * @return
	 */
	private String[] generateWords() {
		
		Random random = new Random();
		
		String[] words = new String[MyGridView.COUNTS_WORDS];
		
		// 存入歌名，将歌名始终放在前面的位置。
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		
		// 获取随机文字并存入数组
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// 洗牌算法
		// 它这个关于汉字顺序的打乱，因为当初在创建这些小框的时候，
		// 也就是在MyGridView的getView中就已经设置了它们的index值，所以这里由它们随便打乱啦
		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，直到最后一个元素
		// 这样能够确保每个元素在每个位置的概率都是1/n
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		
		return words;
	}
	
	// http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	// 由于一级汉字从 16 区起始，汉字区的“高位字节”的范围是 0xB0 - 0xF7，“低位字节”的范围是 0xA1 - 0xFE
	/**
	 * 生成随机汉字
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		// 汉字的高位
		int hightPos;
		// 汉字的低位
		int lowPos;
		
		Random random = new Random();

		// 176=oxB0 39是从01到39，不需要到87啦，不然会有一些生僻字产生
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		// 两个字节，高位和低位，然后转化为相应的字节
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos).byteValue());
		b[1] = (Integer.valueOf(lowPos).byteValue());
		
		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 返回的是char类型
		return str.charAt(0);
		
	}
	
	/**
	 * 检查答案，这里都是通过WordButton的属性进行检测的
	 * @return 状态
	 */
	private int checkTheAnswer() {
		// 先检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// 如果有空的，说明答案还不完整
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		// 答案完整，继续检查正确性
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		
		return (sb.toString().equals(mCurrentSong.getSongName())) ? 
				STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * 文字闪烁
	 *
	 * 答案错误时闪烁文字（交替显示红色和白色文字）
	 *
	 */
	private void sparkTheWords() {
		// 定时器相关
		TimerTask task = new TimerTask() {
			boolean mChange = false;

			// 闪烁次数
			int mSpardTimers = 0;
			
			public void run() {
				// 这里在UI线程中修改UI
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTimers > SPASH_TIMES) {
							// 如果不return的话，就会按照下面设定的时间不断的执行
							return;
						}
						
						// 执行闪烁逻辑：交替显示红色和白色文字
						// view是通过mViewButton来修改的
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton.setTextColor(
									mChange ? Color.RED : Color.WHITE);
						}
						
						mChange = !mChange;
					}
				});
			}
			
		};
		
		Timer timer = new Timer();

		// 三个参数：任务动作，什么时候开始，持续时间
		// 就是说150ms执行一次啦
		timer.schedule(task, 1, 150);
	}
	
	/**
	 * 自动选择一个答案(提示一个正确答案字)
	 */
	private void tipAnswer() {
		// 当前是否找到了答案
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 根据当前的答案框条件选择对应的文字并填入
				// 相当于程序点了一下这个按钮
				onWordButtonClick(findIsAnswerWord(i));
				
				tipWord = true;
				
				// 减少金币数量
				if (!handleCoins(-getTipCoins())) {
					// 金币数量不够，显示提示对话框
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				
				break;
			}
		}
		
		// 没有找到可以填充的位置(没有位置可以填充文字了
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}
	
	/**
	 * 删除一个文字(错误答案)
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，显示提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return; // 就不执行下面的逻辑了
		}
		
		// 将这个索引对应的WordButton设置为不可见
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * (随机)找到一个不是答案的文件, 并且当前是可见的
	 * 
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;
		
		while(true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			
			buf = mAllWords.get(index);
			
			if (buf.mIsVisible && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
	}
	
	/**
	 * 找到一个答案文字
	 * @param index 当前需要填入答案框的索引(需要找哪一个位置的答案)
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			// mCurrentSong.getNameCharacters()[index] 当前已选择文字框index上的正确文字
			if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		
		return null;
	}
	
	/**
	 * 判断某个文字是否为答案
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;
		
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (word.mWordString.equals
					("" + mCurrentSong.getNameCharacters()[i])) {
				result = true;
				
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 增加或者减少指定数量的金币
	 * @param data
	 * @return true 增加/减少成功, false 失败
	 * 减少的时候data传负值就行
	 */
	private boolean handleCoins(int data) {
		// 判断当前总的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		} else {
			// 金币不够
			return false;
		}
	}
	
	/**
	 * 读取配置文件Config.xml中的数据(删除答案所需要的金币数)
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	
	/**
	 * 读取配置文件Config.xml中的数据(提示答案所需要的金币数)
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});
	}
	
	/**
	 * 处理提示按键事件
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
//				tipAnswer();
			}
		});
	}
	
	// 自定义AlertDialog事件响应
	// 删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// 执行事件
					deleteOneWord();
				}
			};
	
	// 答案提示
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// 执行事件
					tipAnswer();
				}
			};
	
	// 金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// 执行事件
				}
			};
	
	/**
	 * 根据id值显示相应的对话框
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins() + 
					"个金币去掉一个错误答案？ ", mBtnOkDeleteWordListener);
			break;
			
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉" + getTipCoins() + 
					"个金币获得一个文字提示？ ", mBtnOkTipAnswerListener);
			break;
			
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不足，去商店补充？ ", 
					mBtnOkLackCoinsListener);
			break;
		}
	}
	
}
