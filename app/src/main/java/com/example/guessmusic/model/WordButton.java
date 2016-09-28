package com.example.guessmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 * @author LiuchangDuan
 *
 */
public class WordButton {

	// 索引
	public int mIndex;

	public boolean mIsVisible;

	// 当前显示文字
	public String mWordString;

	// 它的view，就是说它的布局，这个Button的布局，是用来显示出来的部分
	public Button mViewButton;

	// 构造方法，默认可见，文字为空
	public WordButton() {
		mIsVisible = true;
		mWordString = "";
	}
	
}
