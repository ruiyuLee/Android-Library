package org.lee.android.devices;

import org.lee.android.utils.Log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppInfo {

	public static long getApplicationInfo(Context context, String key,
			long defValue) {
		try {
			context = context.getApplicationContext();
			PackageManager pgm = context.getPackageManager();
			ApplicationInfo ai = pgm.getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			Object value = ai.metaData.get(key);
			if (value != null) {
				return Long.valueOf(value.toString()).longValue();
			}
		} catch (Exception e) {
			Log.d("Exception" + " Exception:" + e);
		}
		return defValue;
	}

	public static int getCurrentVersion(Context context) {
		int versionCode = 0;
		PackageManager packagemanager = context.getPackageManager();
		String packName = context.getPackageName();
		PackageInfo packageinfo = null;
		try {
			packageinfo = packagemanager.getPackageInfo(packName, 0);
			versionCode = packageinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	// 暂且将app分为三种类别：后装app、前装app、前装app的升级包。
	/** 后装app */
	private static final int USER_APP = 0;
	/** 前装app(OEM内置且具有系统权限) */
	private static final int SYSTEM_APP = 1;
	/** 前装app的升级包(OEM内置且具有系统权限的升级包) */
	private static final int SYSTEM_UPDATE_APP = 2;

	/**
	 * 获取app类型,目的是了解app是否具备系统权限.
	 * 
	 * @param context
	 *            上下文
	 * @param pkgName
	 *            包名
	 * @return app类型: 0代表后装app; 1代表前装system app; 2代表前装app的升级包; .
	 */
	public static int getAppType(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();

		int type = USER_APP;

		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);

			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) {
				type = SYSTEM_UPDATE_APP;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				type = SYSTEM_APP;
			} else {
				type = USER_APP;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return type;
	}

	public static class AppStatus { // SUPPRESS CHECKSTYLE

		/**
		 * 判断应用是否在前台。
		 * 
		 * @return 如果在前台，返回true.
		 */
		public static boolean isAppInForeground() {
			return mLiveActivityNum > 0;
		}

		/** 当前活动的Activity数，用来判断该应用是否在前台运行。 */
		private static int mLiveActivityNum = 0;

		/**
		 * 不能直接继承baseactivity的activity，在onresume是调用
		 */
		public static void addLiveActivityNum() {
			mLiveActivityNum++;
		}

		/**
		 * 不能直接继承baseactivity的activity，onpause时调用
		 */
		public static void decLiveActivityNum() {
			mLiveActivityNum--;
		}
	}
}
