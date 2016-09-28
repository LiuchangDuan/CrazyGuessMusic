package com.example.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.guessmusic.R;
import com.example.guessmusic.model.IWordButtonClickListener;
import com.example.guessmusic.model.WordButton;
import com.example.guessmusic.util.Util;

import java.util.ArrayList;

public class MyGridView extends GridView {
	
	public final static int COUNTS_WORDS = 24;
	
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	
	private MyGridAdapter mAdapter;

	// 待选文字框开始加载时的动画
	private Context mContext;
	
	private Animation mScaleAnimation;

	// WordButton点击监听器
	private IWordButtonClickListener mWordButtonListener;

	public MyGridView(Context context, AttributeSet attributeSet) {
		
		super(context, attributeSet);
		
		mContext = context;
		
		mAdapter = new MyGridAdapter();

		// 关联适配器，用this，因为我们这个类本身是继承GridView的，所以这里直接用this
		this.setAdapter(mAdapter);
		
	}

	// 更新数据，加载数据。所以数据是从外面传进来的。
	public void updateData(ArrayList<WordButton> list) {
		
		mArrayList = list;
		
		//重新设置数据源
		setAdapter(mAdapter);
		
	}

	class MyGridAdapter extends BaseAdapter {
		
		public int getCount() {
			return mArrayList.size();
		}
		
		public Object getItem(int pos) {
			return mArrayList.get(pos);
		}
		
		public long getItemId(int pos) {
			return pos;
		}

		// 每次返回的都是一个Button控件的view
		public View getView(int pos, View v, ViewGroup p) {
			
			final WordButton holder;
			
			if (v == null) {
				// convertView为每个小方格Button的布局文件
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);
				
				holder = mArrayList.get(pos);

				// 这里的动画是让24个小方格一次出现
				// 加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				
				// 设置动画的延迟时间
				mScaleAnimation.setStartOffset(pos * 100);
				
				holder.mIndex = pos;

				/**
				 * 这里踩了一个坑
				 * 在于GridView的item的layout中android:layout_height定义为wrap_content
				 * 绘制item高度时系统并不知道item应该绘制多高
				 * 它会先取一条来试探以确定item绘制的具体高度
				 * 这样就导致多调用了一次getView方法。
				 *
				 * 这里表现为getView()在position = 0调用了多次
				 * 所以相当于第一个按钮上初始化了多个按钮（重叠了）
				 * 导致第一个按钮点击之后无法消失
				 *
				 */
				if (holder.mViewButton == null) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton.setOnClickListener(new View.OnClickListener() {
						// MyGridView捕获事件 MainActivity处理逻辑

						// 点击后的逻辑动作要在主界面中实现，而不是在GridView中实现。
						// 所以我们在这里使用“观察者模式”，即接口。
						// private IWordButtonClickListener mWordButtonListener;
						// 这里IWordButtonClickListener类是在其他地方定义的，它是一个接口
						// 接口内部有一个onWordButtonClick函数，这个函数是在MainActivity中实现的
						// 我们这里申请了这样一个变量，然后通过在下面registOnWordButtonClick这个函数将实例传进来
						// 然后就可以调用这个实例内部的onWordButtonClick方法。

						// 相当于在这里调用MainActivity中实现的onWordButtonClick()方法
						@Override
						public void onClick(View v) {
							mWordButtonListener.onWordButtonClick(holder);
						}
					});
				}
				
				v.setTag(holder);
				
			} else {
				holder = (WordButton) v.getTag();
			}
			
			holder.mViewButton.setText(holder.mWordString);
			
			// 播放动画
			v.startAnimation(mScaleAnimation);
			
			return v;
		}
		
	}

	/**
	 * 注册监听接口，这个函数的功能是实例化我们这个类中的变量：
	 * private IWordButtonClickListener mWordButtonListener;
	 *
	 * 在MainActivity中调用
	 *
	 * @param listener
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonListener = listener;
	}
	
}
