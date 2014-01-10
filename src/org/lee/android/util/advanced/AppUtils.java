package org.lee.android.util.advanced;

import org.lee.android.util.Log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateUtils;

/**
 * 重新Application的辅助类
 * 
 * @author liuxinjian
 */
public final class AppUtils {

	/**
	 * 构造方法私有化
	 */
	private AppUtils() {

	}

	/**
	 * 重启Application
	 * 
	 * @param context
	 *            Context Object
	 */
	public static void restart(Context context) {
		restart(context, false);
	}

	/**
	 * 重启Application
	 * 
	 * @param context
	 *            Context Object
	 * @param home
	 *            是否返回到Home页
	 */
	public static void restart(Context context, boolean home) {
		restart(context, home, false);
	}

	/**
	 * 重启Application
	 * 
	 * @param context
	 *            Context Object
	 * @param home
	 *            是否返回到Home页
	 * @param zeus
	 *            是否为zeus的安装完成后的重启(需要在首页处弹出一个Toast)
	 */
	public static void restart(Context context, boolean home, boolean zeus) {
		exit(context, true, home, zeus);
	}

	/**
	 * 退出Application
	 * 
	 * @param context
	 *            Context Object
	 */
	public static void exit(Context context) {
		exit(context, false, false, false);
	}

	/**
	 * 退出Application
	 * 
	 * @param context
	 *            Context Object
	 * @param restart
	 *            退出后是否重启
	 * @param home
	 *            是否返回到Home页
	 * @param zeus
	 *            是否为zeus的安装完成后的重启(需要在首页处弹出一个Toast)
	 */
	private static void exit(final Context context, final boolean restart,
			final boolean home, final boolean zeus) {
		Log.d("restart=" + restart + ", home=" + home + ", zeus=" + zeus);
		final long delay = DateUtils.SECOND_IN_MILLIS;
		Runnable reboot = new Runnable() {
			public void run() {

				if (restart) {
					AlarmManager mgr = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent intent = null;
					if (home) {
						// intent = new Intent(context, MainActivity.class);
					} else {
						intent = new Intent(context, context.getClass());
					}

					if (zeus) {
						// intent.putExtra(MainActivity.INTENT_KEY_FIRST_IN_ZEUS,
						// true);
					}
					PendingIntent pendingIntent = PendingIntent.getActivity(
							context, 0, intent,
							PendingIntent.FLAG_CANCEL_CURRENT);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis()
							+ delay, pendingIntent);

					if (home) {
						// BaseActivity.clearTask();
					}
				} else {
					// BaseActivity.clearTask();
				}

				android.os.Process.killProcess(android.os.Process.myPid());
			}
		};
		new Handler().post(reboot);
	}
}
