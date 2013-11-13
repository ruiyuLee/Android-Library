package org.lee.android.utils;

import android.content.Context;

/**
 * 自定义优化的Toast，继承自android.widget.Toast. <br>
 * 1.单例化。 <br>
 * 2.调用的方法更简单。 <br>
 * 3.内容即时显示。在前一次执行show且尚未消失时，这时show新内容时，可以即时显示新内容。
 * 
 * @author ruiyuLee
 * 
 */
public class Toast extends android.widget.Toast {

	private static String MESSAGE_NULLPOINTEREXCEPTION = "Toast.mContext == null, 请先调用init(Context context)方法进行初始化！";
	private static android.widget.Toast mToast;
	private static Context mContext = null;

	private Toast(Context context) {
		super(context);
	}

	private static android.widget.Toast newInstance(Context context) {
		mContext = context;
		return android.widget.Toast.makeText(context, null, Toast.LENGTH_SHORT);
	}

	/**
	 * 该方法很重要，为提前做初始化，方便之后调用方法时可以不传递Context.
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (context == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		newInstance(context);
	}

	public static void show(int textId) {
		if (mContext == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		show(textId, Toast.LENGTH_SHORT);
	}

	public static void show(String text) {
		if (mContext == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		show(text, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String text) {
		if (context == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		mContext = context;
		show(text, Toast.LENGTH_SHORT);
	}

	/**
	 * 
	 * @param context
	 * @param text
	 */
	public static void showInLongTime(Context context, String text) {
		if (context == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		show(text, Toast.LENGTH_LONG);
	}

	public static void show(int textId, int duration) {
		if (mContext == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		if (mToast == null) {
			mToast = newInstance(mContext);
		}
		mToast.setText(textId);
		mToast.setDuration(duration);
		mToast.show();
	}

	public static void show(String text, int duration) {
		if (mContext == null)
			throw new NullPointerException(MESSAGE_NULLPOINTEREXCEPTION);
		if (mToast == null) {
			newInstance(mContext);
		}
		mToast.setText(text);
		mToast.setDuration(duration);
		mToast.show();
	}
}
