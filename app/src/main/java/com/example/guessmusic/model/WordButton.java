package com.example.guessmusic.model;

import android.widget.Button;

/**
 * ���ְ�ť
 * @author LiuchangDuan
 *
 */
public class WordButton {

	// ����
	public int mIndex;

	public boolean mIsVisible;

	// ��ǰ��ʾ����
	public String mWordString;

	// ����view������˵���Ĳ��֣����Button�Ĳ��֣���������ʾ�����Ĳ���
	public Button mViewButton;

	// ���췽����Ĭ�Ͽɼ�������Ϊ��
	public WordButton() {
		mIsVisible = true;
		mWordString = "";
	}
	
}
