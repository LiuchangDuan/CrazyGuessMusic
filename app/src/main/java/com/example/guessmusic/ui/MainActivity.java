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
	
	/** ��״̬-- ��ȷ  */
	public final static int STATUS_ANSWER_RIGHT = 1;
	
	/** ��״̬-- ����  */
	public final static int STATUS_ANSWER_WRONG = 2;
	
	/** ��״̬-- ������  */
	public final static int STATUS_ANSWER_LACK = 3;
	
	// ��˸����
	public final static int SPASH_TIMES = 6;
	
	public final static int ID_DIALOG_DELETE_WORD = 1;
	
	public final static int ID_DIALOG_TIP_ANSWER = 2;
	
	public final static int ID_DIALOG_LACK_COINS = 3;
	
	// ��Ƭ��ض���
	private Animation mPanAnim;
	// ���������˶��ٶ� �����ٶ�
	private LinearInterpolator mPanLin;

	// �������󶯻�
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	// �������Ҷ���
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	// ��Ƭ�ؼ�
	private ImageView mViewPan;
	
	// ���˿ؼ�
	private ImageView mViewPanBar;

	// ��ǰ������(���ؽ����е�)
	private TextView mCurrentStagePassView;

	// ��ǰ������(��Ϸ�����е�)
	private TextView mCurrentStageView;
	
	//��ǰ��������
	private TextView mCurrentSongNamePassView;
	
	// Play �����¼�
	private ImageButton mBtnPlayStart;
	
	// ���ؽ���
	private View mPassView;
	
	// ��ǰ�����Ƿ���������
	private boolean mIsRunning = false;

	// ����ѡ�����ֿ�����
	private ArrayList<WordButton> mAllWords;

	// ����ѡ�����ֿ�����
	private ArrayList<WordButton> mBtnSelectWords;
	
	private MyGridView mMyGridView;
	
	// ��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;
	
	// ��ǰ�ĸ���
	private Song mCurrentSong;
	
	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;
	
	// ��ǰ��ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;
	
	// ���View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ��ȡ����
		int[] datas = Util.loadData(this);
		mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
		
		// ��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);
		
		mMyGridView = (MyGridView) findViewById(R.id.gridView);
		
		mViewCurrentCoins = (TextView)findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");
		
		//ע�����
		mMyGridView.registOnWordButtonClick(this);
		
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// ��ʼ����Ƭ����
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		// �����ٶ�
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		// ���ö�������
		mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// ��Բ��ת��������ʱ�����������˳�����
				mViewPanBar.startAnimation(mBarOutAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
		});

		// ��ʼ���������ƶ���
		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		// ���������󱣳���״̬��Ĭ��Ϊfalse�� ����ͣ��
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// ����������֮��������Ƭת������
				mViewPan.startAnimation(mPanAnim);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
		});

		// ��ʼ���������ƶ���
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
				// ���׶���������ϣ���������ʾ������ͬʱ״̬��Ϊ�Ƕ���״̬
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
		
		// ��ʼ����Ϸ����
		initCurrentStageData();
		
		// ����ɾ�������¼�
		handleDeleteWord();
		
		// ������ʾ�����¼�
		handleTipAnswer();

	}
	
	@Override
	public void onWordButtonClick(WordButton wordButton) {
//		Toast.makeText(this, wordButton.mIndex + "", Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// ÿ���һ�ξ�ȥ�����С��Ƿ���ȷ
		// ��ô�״̬
		int checkResult = checkTheAnswer();
		
		// ����
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// ���ز������Ӧ�Ľ���
//			Toast.makeText(this, "STATUS_ANSWER_RIGHT", Toast.LENGTH_SHORT).show();
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// ��˸���ֲ���ʾ�û���������ʾ��
			sparkTheWords();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// ����������ɫΪ��ɫ(Normal)
			// ����״̬���������ûذ���ɫ
			// ��Ȼ�ᵼ�´𰸴������ѡ�����ֿ��������һֱ�Ǻ�ɫ��
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}
	
	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		// ��ʾ���ؽ���
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		// ���ص�ʱ����ʾ���ؽ���
		mPassView.setVisibility(View.VISIBLE);
		
		// �������
		handleCoins(Const.PASS_AWARD_COINS);
		
		// ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();
		
		// ֹͣ���ڲ��ŵ�����(�����ʱ��Ҫֹͣ����)
		MyPlayer.stopTheSong(MainActivity.this);
		
		// ������Ч
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);

		// ���õ�ǰ������(����)����ʾ
		mCurrentStagePassView = (TextView) findViewById
				(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		
		// ��ʾ����������
		mCurrentSongNamePassView = (TextView) findViewById
				(R.id.text_current_song_name_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		
		// ��һ�ذ�������
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �����ǰ�Ѿ�ͨ��
				if (judgeAppPassed()) {
					// ���뵽ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
					return; // ������Ȼ�����initCurrentStageData()���ᵼ������Խ��
				} else {
					// ��ʼ��һ��
					// ���ع��ؽ���
					mPassView.setVisibility(View.GONE);

					// ������һ�ؿ�����
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ�أ��Ƿ���true
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
 	}

	/**
	 * ����� ���ڳ�����ѡ����
	 *
	 * �����ѡ���ֿ��������֣�ͨ����������֣�ʹ��ѡ���ֿ��е�������ʧ����ѡ���ֿ��и����ֱ�����¿ɼ�
	 *
	 * @param wordButton ��Ӧ��ѡ����Ǹ�
	 *
	 */
	private void clearTheAnswer(WordButton wordButton) {
		// ������ѡ�����ֲ��ɼ�
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisible = false;
		
		// ���ô�ѡ��ɼ���
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}
	
	/**
	 * ���ô�
	 *
	 * ����������С�����Ӧ��Ҫȥ�������ѡ���Ͻ��������޸ģ�
	 *
	 * @param wordButton
	 *
	 */
	private void setSelectWord(WordButton wordButton) {
		// ��һ����ѡ���ֿ������ÿһ��С����û�ж���
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// �����ǰС��û�����֣���Щ����һЩ���Ե�����������û�иı��view������
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ô����ֿ����ݼ��ɼ���
				mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisible = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				// ��¼������Ϊ�����Ժ��ٵ����ʱ��Ҫ�����ֻ���ȥ
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
				
				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");
				
				// ���ô�ѡ��ɼ��ԣ�����������ȥ�ı�view����
				setButtonVisiable(wordButton, View.INVISIBLE);

				// ����������֮���break����Ȼ�ᵼ���ظ���������
				break;
				
			}
		}
	}
	
	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ����������ڲ�������ȥ��������ʵ����ʾ״̬��
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisible = (visibility == View.VISIBLE) ? true : false;
		
		MyLog.d(TAG, button.mIsVisible + "");
		
	}
	
	/**
	 * ����Բ���м�Ĳ��Ű�ť�����ǿ�ʼ��������
	 */
	private void handlePlayButton() {
		// ��ֹ��ָ��
		if (mViewPanBar != null) {
			// ������״̬�ſ������С�����Ƭ���ƻ����Ժ󣬲��ܽ�����һ�δ���
			// ��ֹ��ε����ť
			if (!mIsRunning) {
				mIsRunning = true;
				// ��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				// ����ť����
				mBtnPlayStart.setVisibility(View.INVISIBLE);
				
				// ��������
				MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
			}
		}
	}
	
	@Override
	public void onPause() {
		// ������Ϸ����
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);

		// ����ǰӦ�ó����˳���ʱ��ֹͣ����
		mViewPan.clearAnimation();
		
		// ��ͣ����(�����л�����̨ʱ����Ӧ��ֹͣ)
		MyPlayer.stopTheSong(MainActivity.this);
		
		super.onPause();
	}

	/**
	 * ��ȡ��ǰ�ص�song����
	 */
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		// SONG_INFO�Ƕ�ά����
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);
		
		return song;
	}
	
	/**
	 * ���ص�ǰ�ص�����(ÿһ�ض�����ã�
	 */
	private void initCurrentStageData() {
		
		// ��ȡ��ǰ�صĸ�����Ϣ
		// mCurrentStageIndex��ʼ��Ϊ-1
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		
		// ��ʼ����ѡ������ֿ�
		mBtnSelectWords = initWordSelect();
		
		LayoutParams params = new LayoutParams(140, 140);
		
		// ���ԭ���Ĵ�
		// ���������һ��֮����һ�صĴ�������Ȼ����
		mViewWordsContainer.removeAllViews();
		
		// �����µĴ𰸿�
		// ��̬��Ϊ��ѡ�����ֿ���Ӷ�Ӧ������С�򣬽���ѡ�����ֿ���ӵ�Layout mViewWordsContainer��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton, params);
		}
		
		// ��ʾ��ǰ�ص�����
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		
		// �������
		mAllWords = initAllWord();
		
		// ��������------MyGridView
		mMyGridView.updateData(mAllWords);
		
		// һ��ʼ�Ͳ������֣���Ӧ�þͿ�ʼ�������֣�
		handlePlayButton();
	}
	
	/**
	 * ��ʼ����ѡ���ֿ�
	 * @return
	 */
	private ArrayList<WordButton> initAllWord() {
		
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		// ������д�ѡ����
		String[] words = generateWords();
		
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			
			WordButton button = new WordButton();
			
			button.mWordString = words[i];
			
			data.add(button);
			
		}
		
		return data;
		
	}
	
	/**
	 * ��ʼ����ѡ�����ֿ򣬾��Ǵ���ÿ��С��Ȼ������Ǳ���б�
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		
		//TODO
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			// ��Ҫת��Ϊһ��view������ӵ����ֵ���
			View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
			
			final WordButton holder = new WordButton();

			// mViewButton��WordButton��Ҫ����ʾ��ҳ���еĲ���
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			// ��ѡ���ֿ�ʹ�ѡ���ֿ�ı�����ɫ�ǲ�һ����
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			// ���ڻ����ɼ�����ֻ�Ǹ���ʶ����������mViewButton��Ĳ��ɼ���
			holder.mIsVisible = false;
			
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			// С�򱻵��ʱ��ʾ���������Ҫ�������Ȼ������Ķ�Ӧ�Ŀ�Ҫ��ʾ����
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
	 * �������еĴ�ѡ����
	 * @return
	 */
	private String[] generateWords() {
		
		Random random = new Random();
		
		String[] words = new String[MyGridView.COUNTS_WORDS];
		
		// ���������������ʼ�շ���ǰ���λ�á�
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		
		// ��ȡ������ֲ���������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// ϴ���㷨
		// ��������ں���˳��Ĵ��ң���Ϊ�����ڴ�����ЩС���ʱ��
		// Ҳ������MyGridView��getView�о��Ѿ����������ǵ�indexֵ������������������������
		// ��������˳�����ȴ�����Ԫ�������ѡȡһ�����һ��Ԫ�ؽ��н�����
		// Ȼ���ڵڶ���֮��ѡ��һ��Ԫ����ڶ���������ֱ�����һ��Ԫ��
		// �����ܹ�ȷ��ÿ��Ԫ����ÿ��λ�õĸ��ʶ���1/n
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		
		return words;
	}
	
	// http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	// ����һ�����ִ� 16 ����ʼ���������ġ���λ�ֽڡ��ķ�Χ�� 0xB0 - 0xF7������λ�ֽڡ��ķ�Χ�� 0xA1 - 0xFE
	/**
	 * �����������
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		// ���ֵĸ�λ
		int hightPos;
		// ���ֵĵ�λ
		int lowPos;
		
		Random random = new Random();

		// 176=oxB0 39�Ǵ�01��39������Ҫ��87������Ȼ����һЩ��Ƨ�ֲ���
		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		// �����ֽڣ���λ�͵�λ��Ȼ��ת��Ϊ��Ӧ���ֽ�
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos).byteValue());
		b[1] = (Integer.valueOf(lowPos).byteValue());
		
		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// ���ص���char����
		return str.charAt(0);
		
	}
	
	/**
	 * ���𰸣����ﶼ��ͨ��WordButton�����Խ��м���
	 * @return ״̬
	 */
	private int checkTheAnswer() {
		// �ȼ�鳤��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// ����пյģ�˵���𰸻�������
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		// �����������������ȷ��
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}
		
		return (sb.toString().equals(mCurrentSong.getSongName())) ? 
				STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * ������˸
	 *
	 * �𰸴���ʱ��˸���֣�������ʾ��ɫ�Ͱ�ɫ���֣�
	 *
	 */
	private void sparkTheWords() {
		// ��ʱ�����
		TimerTask task = new TimerTask() {
			boolean mChange = false;

			// ��˸����
			int mSpardTimers = 0;
			
			public void run() {
				// ������UI�߳����޸�UI
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTimers > SPASH_TIMES) {
							// �����return�Ļ����ͻᰴ�������趨��ʱ�䲻�ϵ�ִ��
							return;
						}
						
						// ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ����
						// view��ͨ��mViewButton���޸ĵ�
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

		// ������������������ʲôʱ��ʼ������ʱ��
		// ����˵150msִ��һ����
		timer.schedule(task, 1, 150);
	}
	
	/**
	 * �Զ�ѡ��һ����(��ʾһ����ȷ����)
	 */
	private void tipAnswer() {
		// ��ǰ�Ƿ��ҵ��˴�
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ݵ�ǰ�Ĵ𰸿�����ѡ���Ӧ�����ֲ�����
				// �൱�ڳ������һ�������ť
				onWordButtonClick(findIsAnswerWord(i));
				
				tipWord = true;
				
				// ���ٽ������
				if (!handleCoins(-getTipCoins())) {
					// ���������������ʾ��ʾ�Ի���
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				
				break;
			}
		}
		
		// û���ҵ���������λ��(û��λ�ÿ������������
		if (!tipWord) {
			// ��˸������ʾ�û�
			sparkTheWords();
		}
	}
	
	/**
	 * ɾ��һ������(�����)
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeleteWordCoins())) {
			// ��Ҳ�������ʾ��ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return; // �Ͳ�ִ��������߼���
		}
		
		// �����������Ӧ��WordButton����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * (���)�ҵ�һ�����Ǵ𰸵��ļ�, ���ҵ�ǰ�ǿɼ���
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
	 * �ҵ�һ��������
	 * @param index ��ǰ��Ҫ����𰸿������(��Ҫ����һ��λ�õĴ�)
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			// mCurrentSong.getNameCharacters()[index] ��ǰ��ѡ�����ֿ�index�ϵ���ȷ����
			if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		
		return null;
	}
	
	/**
	 * �ж�ĳ�������Ƿ�Ϊ��
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
	 * ���ӻ��߼���ָ�������Ľ��
	 * @param data
	 * @return true ����/���ٳɹ�, false ʧ��
	 * ���ٵ�ʱ��data����ֵ����
	 */
	private boolean handleCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		} else {
			// ��Ҳ���
			return false;
		}
	}
	
	/**
	 * ��ȡ�����ļ�Config.xml�е�����(ɾ��������Ҫ�Ľ����)
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}
	
	/**
	 * ��ȡ�����ļ�Config.xml�е�����(��ʾ������Ҫ�Ľ����)
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * ����ɾ����ѡ�����¼�
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
	 * ������ʾ�����¼�
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
	
	// �Զ���AlertDialog�¼���Ӧ
	// ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// ִ���¼�
					deleteOneWord();
				}
			};
	
	// ����ʾ
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// ִ���¼�
					tipAnswer();
				}
			};
	
	// ��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = 
			new IAlertDialogButtonListener() {
				
				@Override
				public void onClick() {
					// ִ���¼�
				}
			};
	
	/**
	 * ����idֵ��ʾ��Ӧ�ĶԻ���
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeleteWordCoins() + 
					"�����ȥ��һ������𰸣� ", mBtnOkDeleteWordListener);
			break;
			
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipCoins() + 
					"����һ��һ��������ʾ�� ", mBtnOkTipAnswerListener);
			break;
			
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ��㣬ȥ�̵겹�䣿 ", 
					mBtnOkLackCoinsListener);
			break;
		}
	}
	
}
