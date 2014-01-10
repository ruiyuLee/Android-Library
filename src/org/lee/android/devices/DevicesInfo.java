package org.lee.android.devices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lee.android.util.ExtraTools;
import org.lee.android.util.Log;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

public class DevicesInfo {

	public DevicesInfo() {
	}

	public static Bitmap printScreen(Activity activity, boolean hasStatusBar) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();
		Screen screen = getScreen(activity);
		int height = hasStatusBar ? screen.height : screen.height - screen.statusBarHeight;
		Bitmap bitmap = Bitmap.createBitmap(b, 0, screen.statusBarHeight, screen.width, height);
        view.destroyDrawingCache();
        return bitmap;
	}

	public static Screen mScreen;
	public static class Screen {
		public int width;
		public int height;

		public int statusBarHeight;

		public Screen(int w, int h) {
			width = w;
			height = h;
		}
	}
	
	public static Screen getScreen(Activity activity) {
		if (mScreen == null) {
			DisplayMetrics metrics = activity.getResources()
					.getDisplayMetrics();
			mScreen = new Screen(metrics.widthPixels, metrics.heightPixels);
			Rect frame = new Rect();
			activity.getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(frame);
			mScreen.statusBarHeight = frame.top;
		}
		return mScreen;
	}

	/**
	 * 是否锁屏
	 * 
	 * @param context
	 *            上下文
	 * @return true锁屏， false
	 */
	public static boolean isScreenOn(Context context) {
		PowerManager powerManager = (PowerManager) context
				.getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		return powerManager.isScreenOn();
	}

	/**
	 * 生成设备id。 由于有些设备没有电话功能，所以不能依赖于 imei。 Secure.ANDROID_ID 在 froyo以前版本也不可靠
	 * 我们还需要考虑模拟器，模拟器目前看来有个电话号码可用。
	 * 
	 * 所以结合起来搞了一个md5.
	 * 
	 * deviceid 问题请参考
	 * http://android-developers.blogspot.com/2011/03/identifying-
	 * app-installations.html
	 * 
	 * @param context
	 *            Context
	 * @return device id.
	 */
	public static String getDeviceID(Context context) {
		String tmDevice = "";
		String androidId = "";
		String deviceMobileNo = "";

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			tmDevice = "" + tm.getDeviceId();
			// 针对 模拟器才取手机号。因为模拟器andorid2.2 以下版本 imei/ANDROID_ID 都不可用。
			if ("000000000000000".equals(tmDevice)) {
				deviceMobileNo = "" + tm.getLine1Number();
			}
		}

		androidId = ""
				+ Secure.getString(context.getContentResolver(),
						Secure.ANDROID_ID);

		String deviceUUID = ExtraTools.toMd5(
				(tmDevice + androidId + deviceMobileNo).getBytes(), false);

		return deviceUUID;
	}

	/**
	 * 获取软件用户唯一标示码。 2 + 16 + 4 + 16 位长。
	 * 
	 * 前两位为 产品标识码。百度搜索位 10.
	 * 
	 * 中间十六位和后十六位 位 deviceid 的 32 位 分为两部分。
	 * 
	 * 中间四位为采用一个规则从 32位deviceid中取出的4位。
	 * 
	 * 
	 * @param context
	 *            Context
	 * @return 软件uuid。
	 */
	public static String getSoftwareUUID(Context context) {
		final String productId = "10"; // 百度搜索产品id 为 10；
		// String deviceId = DeviceId.getDeviceID(context);
		String deviceId = "imei";

		// System.out.println(deviceId);

		StringBuffer sb = new StringBuffer();
		sb.append(productId);
		sb.append(deviceId.substring(0, deviceId.length() / 2));

		// 取第2位的十六进制转化为十进制作为 起始位置。
		int start = Integer
				.parseInt(Character.toString(deviceId.charAt(2)), 16); // SUPPRESS
																		// CHECKSTYLE

		for (int i = 0; i < 4; i++) { // SUPPRESS CHECKSTYLE
			int index = (start + i * 8 + 1) % deviceId.length(); // SUPPRESS
																	// CHECKSTYLE

			// System.out.println(index);

			sb.append(deviceId.charAt(index));
		}

		sb.append(deviceId.substring(deviceId.length() / 2));

		String result = sb.toString().toUpperCase();

		// System.out.println("result : " + result + " length = " +
		// result.length());

		return result;
	}

	/**
	 * 根据uri，取出相应的电话号码
	 * 
	 * @param context
	 *            context
	 * @param contactsUri
	 *            联系人的uri
	 * @return 电话号码
	 */
	public static String getPhoneNumber(Context context, Uri contactsUri) {
		String retStr = null;
		Cursor contactsCur = null;
		Cursor phoneNumberCur = null;
		int contactId = 0;
		String phoneNumber;

		String[] selPhoneCols = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
		try {
			if (contactsUri != null) {
				contactsCur = context.getContentResolver().query(contactsUri,
						null, null, null, null);
				if (contactsCur != null && contactsCur.getCount() > 0) {
					contactsCur.moveToFirst();
					contactId = contactsCur.getInt(contactsCur
							.getColumnIndex(ContactsContract.Contacts._ID));

					phoneNumberCur = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							selPhoneCols,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + contactId, null, null);

					if (phoneNumberCur != null && phoneNumberCur.getCount() > 0) {
						phoneNumberCur.moveToFirst();
						phoneNumber = null;
						do {
							phoneNumber = phoneNumberCur.getString(0);
							if (checkPhoneNumber(phoneNumber)) {
								break;
							}
						} while (phoneNumberCur.moveToNext());

						retStr = phoneNumber;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (contactsCur != null) {
				contactsCur.close();
				contactsCur = null;
			}

			if (phoneNumberCur != null) {
				phoneNumberCur.close();
				phoneNumberCur = null;
			}
		}

		return retStr;
	}

	/**
	 * This pattern is intended for searching for things that look like they
	 * might be phone numbers in arbitrary text, not for validating whether
	 * something is in fact a phone number. It will miss many things that are
	 * legitimate phone numbers.
	 * 
	 * <p>
	 * The pattern matches the following:
	 * <ul>
	 * <li>Optionally, a + sign followed immediately by one or more digits.
	 * Spaces, dots, or dashes may follow.
	 * <li>Optionally, sets of digits in parentheses, separated by spaces, dots,
	 * or dashes.
	 * <li>A string starting and ending with a digit, containing digits, spaces,
	 * dots, and/or dashes.
	 * </ul>
	 */
	public static final Pattern PHONE
	// sdd = space, dot, or dash
	= Pattern.compile("(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
			+ "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
			+ "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])"); // <digit><digit|sdd>+<digit>

	/**
	 * 检查电话号码是否合法
	 * 
	 * @param phoneNumber
	 *            电话号码
	 * @return 合法与否
	 */
	public static boolean checkPhoneNumber(String phoneNumber) {

		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}

		// add by qiaopu 先trim
		phoneNumber = phoneNumber.trim();

		if (phoneNumber.length() == 0) {
			return false;
		}
		// end add

		Matcher matcher = PHONE.matcher(phoneNumber);
		boolean ismatch = matcher.matches();

		return ismatch;
	}

	/**
	 * 网络是否可用。(
	 * 
	 * @param context
	 *            context
	 * @return 连接并可用返回 true
	 */
	public static boolean isNetworkConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		// return networkInfo != null && networkInfo.isConnected();
		boolean flag = networkInfo != null && networkInfo.isAvailable();
		Log.d("isNetworkConnected, rtn: " + flag);
		return flag;
	}

	/**
	 * 获取活动的连接。
	 * 
	 * @param context
	 *            context
	 * @return 当前连接
	 */
	private static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return null;
		}
		return connectivity.getActiveNetworkInfo();
	}

	/** density */
	private static int mDensity = android.util.DisplayMetrics.DENSITY_HIGH;

	/** 真正的Density，而不是densityDpi */
	private static float mFloatDensity = (float) 1.5; // SUPPRESS CHECKSTYLE

	/**
	 * 设置屏幕的density
	 * 
	 * @param context
	 *            context
	 */
	public static void setScreenDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		mDensity = dm.densityDpi;
		mFloatDensity = dm.density;
	}

	/**
	 * 获取屏幕density
	 * 
	 * @return density
	 */
	public static int getScreenDensity() {
		return mDensity;
	}

	/**
	 * 获取屏幕density
	 * 
	 * @return density
	 */
	public static float getScreenFloatDensity() {
		return mFloatDensity;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 *            上下文句柄
	 * @param dpValue
	 *            dp 的单位
	 * @return px(像素)的单位
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 *            上下文句柄
	 * @param pxValue
	 *            px(像素) 的单位
	 * @return dp的单位
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale);
	}

}
