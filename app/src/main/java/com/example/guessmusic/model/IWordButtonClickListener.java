package com.example.guessmusic.model;

/**
 * 观察者模式
 *
 * 它的方法onWordButtonClick的具体实现是在MainActivity中重写的
 * 因为MainActivity实现了IWordButtonClickListener接口
 *
 */
public interface IWordButtonClickListener {

	void onWordButtonClick(WordButton wordButton);
	
}
