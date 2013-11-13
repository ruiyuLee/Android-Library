/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */
package org.lee.android.util.io;

import org.lee.android.utils.Log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.util.SparseIntArray;

/**
 * 音效播放器
 * 
 * @author liuxinjian
 * @since 2012-11-21
 */
public final class SoundPlayer {

	/** Log switch */
	private static final boolean DEBUG = true & false;

	/** SoundPool实例：播放较短的音乐效果时，应该使用SoundPool而不使用MediaPlayer */
	private SoundPool mSoundPool;

	/** 已加载过的音效文件，缓存住其ID，避免重复加载 */
	private SparseIntArray mSoundPoolCache;

	/** SoundPool最大同时播放音乐效果的个数 */
	public static final int MAX_STREAMS = 5;

	/** Single instance */
	private static SoundPlayer sSoundPlayer;

	/** 预估加载sound音频消费的时间 */
	private static final long LOAD_SOUND_MILLIS = 100L;

	/**
	 * 工具类隐藏其构造方法
	 */
	private SoundPlayer() {
		mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolCache = new SparseIntArray();
	}

	/**
	 * 播放音效
	 * 
	 * @param context
	 *            Context
	 * @param resId
	 *            放在raw文件下的资源的id
	 */
	@SuppressLint("NewApi")
	public static void play(Context context, final int resId) {
		if (null == sSoundPlayer) {
			sSoundPlayer = new SoundPlayer();
		}

		int id = sSoundPlayer.mSoundPoolCache.get(resId);
		if (0 == id) {
			final int soundId = sSoundPlayer.mSoundPool.load(context, resId, 1);
			if (DEBUG) {
				Log.d(String.format("SoundPool.load(resId=%d): soundId=%d",
						resId, soundId));
			}
			sSoundPlayer.mSoundPoolCache.put(resId, soundId);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				sSoundPlayer.mSoundPool
						.setOnLoadCompleteListener(new OnLoadCompleteListener() {
							@Override
							public void onLoadComplete(SoundPool soundPool,
									int sampleId, int status) {
								if (DEBUG) {
									Log.d(String
											.format("SoundPool.onLoadComplete(soundId=%d):sampleId=%d",
													soundId, sampleId));
								}
								if (0 == status && soundId == sampleId) {
									sSoundPlayer.mSoundPool.play(soundId, 1.0f,
											1.0f, MAX_STREAMS, 0, 1.0f);
								}
							}
						});
			} else {
				try {
					Thread.currentThread().join(LOAD_SOUND_MILLIS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sSoundPlayer.mSoundPool.play(soundId, 1.0f, 1.0f, MAX_STREAMS,
						0, 1.0f);
			}
		} else {
			sSoundPlayer.mSoundPool.play(id, 1.0f, 1.0f, MAX_STREAMS, 0, 1.0f);
		}
	}

	/**
	 * Release all loaded sound.
	 */
	public static void release() {
		if (null != sSoundPlayer) {
			for (int i = 0, n = sSoundPlayer.mSoundPoolCache.size(); i < n; i++) {
				sSoundPlayer.mSoundPool.unload(sSoundPlayer.mSoundPoolCache
						.valueAt(i));
			}
			sSoundPlayer.mSoundPool.release();
			sSoundPlayer.mSoundPool = null;
			sSoundPlayer.mSoundPoolCache.clear();
			sSoundPlayer.mSoundPoolCache = null;

			sSoundPlayer = null;
		}
	}
}
