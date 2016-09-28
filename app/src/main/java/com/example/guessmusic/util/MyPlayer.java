package com.example.guessmusic.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * ���ֲ�����
 * 
 * @author DuanLiuchang
 *
 */
public class MyPlayer {
	
	// ����
	public final static int INDEX_STONE_ENTER = 0;
	public final static int INDEX_STONE_CANCEL = 1;
	public final static int INDEX_STONE_COIN = 2;
	
	// ��Ч���ļ���
	private final static String[] SONG_NAMES = {"enter.mp3", "cancel.mp3", "coin.mp3"};
	
	// ��Ч
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];

	// ��������
	private static MediaPlayer mMusicMediaPlayer;
	
	// ������Ч����ť��������������跴������
	public static void playTone(Context context, int toneIndex) {
		// ��������
		AssetManager assetManager = context.getAssets();
		
		if (mToneMediaPlayer[toneIndex] == null) {
			mToneMediaPlayer[toneIndex] = new MediaPlayer();
			
			try {
				// ֻ�����һ�ξͿ�����
				AssetFileDescriptor fileDescriptor = assetManager.openFd(SONG_NAMES[toneIndex]);
				mToneMediaPlayer[toneIndex].setDataSource(fileDescriptor.getFileDescriptor(), 
						fileDescriptor.getStartOffset(), fileDescriptor.getLength());
				mToneMediaPlayer[toneIndex].prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// ��������
		mToneMediaPlayer[toneIndex].start();
		
	}
	
	/**
	 * ���Ÿ���
	 * 
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}

		// ǿ������ ��Ϊ����ǵڶ��β��ŵ�ʱ����Ҫ��״̬���óɿɲ��ŵ�״̬
		// ��Էǵ�һ�β��ŵ�״̬
		mMusicMediaPlayer.reset();
		
		// ���������ļ�
		/**
		 *
		 * assets�ļ����µ��ļ����ᱻӳ�䵽R.java�У����ʵ�ʱ����ҪAssetManager��
		 *
		 * ��res/raw����ֱ��ʹ����ԴID���ʣ�
		 *
		 */
		AssetManager assetManager = context.getAssets();
		try {
			// ���������ļ�
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			// ��MediaPlayer��������Դ
			mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), 
					fileDescriptor.getStartOffset(), fileDescriptor.getLength());
			
			mMusicMediaPlayer.prepare();
			
			// ��������
			mMusicMediaPlayer.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ֹͣ���Ÿ���
	 * @param context
	 */
	public static void stopTheSong(Context context) {
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
		}
	}
	
}
