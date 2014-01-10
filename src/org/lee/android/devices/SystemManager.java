package org.lee.android.devices;

import java.util.List;

import org.lee.android.util.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class SystemManager {

	/**
	 * 获取屏幕的系统亮度
	 * 
	 * @param context
	 * @return
	 */
	public static int getSystemBrightness(Context context) {
		int system_screen_brightness = 250;
		try {
			system_screen_brightness = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return system_screen_brightness;
	}

	/**
	 * 判断是否开启了自动亮度调节
	 * 
	 * @param aContext
	 * @return
	 */
	public static boolean isAutoBrightness(Context context) {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return automicBrightness;
	}

	/**
	 * 设置亮度
	 * 
	 * @param activity
	 * @param brightness
	 */
	public static void setBrightness(Activity activity, int brightness) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
		activity.getWindow().setAttributes(lp);
	}

	/**
	 * Hides the input method.
	 * 
	 * @param context
	 *            context
	 * @param view
	 *            The currently focused view
	 * @return success or not.
	 */
	public static boolean hideInputMethod(Context context, View view) {
		if (context == null || view == null) {
			return false;
		}

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

		return false;
	}

	/**
	 * Show the input method.
	 * 
	 * @param context
	 *            context
	 * @param view
	 *            The currently focused view, which would like to receive soft
	 *            keyboard input
	 * @return success or not.
	 */
	public static boolean showInputMethod(Context context, View view) {
		if (context == null || view == null) {
			return false;
		}

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			return imm.showSoftInput(view, 0);
		}

		return false;
	}

	/**
	 * 启动不确定的Activity。该函数主要用来启动诸如闹钟日历这种无标准intent的Activity。
	 * 如果通过默认的包名和Activity名启动失败，则扫描应用程序，通过包名后缀和Activity后缀来匹配， 直到启动成功或者扫描结束为止。
	 * 注意，为了安全起见，目前只扫描内置的应用程序 。
	 * 
	 * @param context
	 *            Context.
	 * @param defaultPackageNames
	 *            默认的包名集合。
	 * @param defaultActivityNames
	 *            对应的默认Activity名集合。
	 * @param packagePostfix
	 *            包名后缀。
	 * @param activityPostfix
	 *            Activity后缀（请加上"."）。
	 * @param packageSaveKey
	 *            用来保存成功启动的组件包名。
	 * @param activitySaveKey
	 *            用来保存成功启动的组件Activity名。
	 * @return 如果能成功启动某个Activity，则返回它的组件名；否则返回null.
	 */
	public static ComponentName startUncertainActivitySafely(Context context,
			String[] defaultPackageNames, String[] defaultActivityNames,
			String packagePostfix, String activityPostfix,
			String packageSaveKey, String activitySaveKey) {
		ComponentName result = null;

		if (defaultPackageNames == null || defaultActivityNames == null) {
			return result;
		}

		int length = defaultPackageNames.length < defaultActivityNames.length ? defaultPackageNames.length
				: defaultActivityNames.length;

		ComponentName[] components = new ComponentName[length];
		for (int i = 0; i < length; i++) {
			components[i] = new ComponentName(defaultPackageNames[i],
					defaultActivityNames[i]);
		}

		result = startUncertainActivitySafely(context, components,
				packagePostfix, activityPostfix, packageSaveKey,
				activitySaveKey);

		return result;
	}

	/**
	 * 启动不确定的Activity。该函数主要用来启动诸如闹钟日历这种无标准intent的Activity。
	 * 如果通过默认的包名和Activity名启动失败，则扫描应用程序，通过包名后缀和Activity后缀来匹配， 直到启动成功或者扫描结束为止。
	 * 注意，为了安全起见，目前只扫描内置的应用程序 。
	 * 
	 * @param context
	 *            Context.
	 * @param defaultComponents
	 *            默认的组件名集合。
	 * @param packagePostfix
	 *            包名后缀。
	 * @param activityPostfix
	 *            Activity后缀（请加上"."）。
	 * @param packageSaveKey
	 *            用来保存成功启动的组件包名。
	 * @param activitySaveKey
	 *            用来保存成功启动的组件Activity名。
	 * @return 如果能成功启动某个Activity，则返回它的组件名；否则返回null.
	 */
	public static ComponentName startUncertainActivitySafely(Context context,
			ComponentName[] defaultComponents, String packagePostfix,
			String activityPostfix, String packageSaveKey,
			String activitySaveKey) {
		ComponentName result = null;

		String savePackageName = null;
		String saveActivityName = null;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		savePackageName = preferences.getString(packageSaveKey, null);
		saveActivityName = preferences.getString(activitySaveKey, null);

		if (savePackageName != null
				&& saveActivityName != null
				&& startActivitySafely(context, savePackageName,
						saveActivityName)) {
			result = new ComponentName(savePackageName, saveActivityName);
			return result;
		}

		if (defaultComponents != null) {
			for (ComponentName componentName : defaultComponents) {
				if (startActivitySafely(context, componentName)) {
					result = componentName;
					break;
				}
			}
		}

		if (result == null) {
			List<PackageInfo> packageInfos = context.getPackageManager()
					.getInstalledPackages(0);
			for (PackageInfo pi : packageInfos) { // 是内置应用，且包名以"clock"结束
				if (((pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP || (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
						&& pi.packageName.endsWith(packagePostfix)) {

					ComponentName componentName = new ComponentName(
							pi.packageName, pi.packageName + activityPostfix);
					// 有Activity名以"AlarmClock"结束，并能成功启动
					if (startActivitySafely(context, componentName)) {
						result = componentName;
						break;
					}
				}
			}
		}

		if (result != null) {
			Editor editor = preferences.edit();
			editor.putString(packageSaveKey, result.getPackageName());
			editor.putString(activitySaveKey, result.getClassName());
			editor.commit();
		}
		return result;
	}

	/**
	 * 安全启动应用程序，截获Exception，并返回是否成功启动。
	 * 
	 * @param context
	 *            Context.
	 * @param packageName
	 *            包名.
	 * @param activityName
	 *            Activity全名（加上包名前缀）.
	 * @return 是否成功启动Activity。
	 */
	public static boolean startActivitySafely(Context context,
			String packageName, String activityName) {
		boolean result = false;
		if (packageName != null && activityName != null) {
			ComponentName component = new ComponentName(packageName,
					activityName);
			result = startActivitySafely(context, component);
		}

		return result;
	}

	/**
	 * 安全启动应用程序，截获Exception，并返回是否成功启动。
	 * 
	 * @param context
	 *            Context.
	 * @param component
	 *            组件名，由包名和Activity全名（加上包名前缀）共同生成.
	 * @return 是否成功启动Activity。
	 */
	public static boolean startActivitySafely(Context context,
			ComponentName component) {
		boolean result = false;
		if (component != null) {
			Intent intent = new Intent();
			intent.setComponent(component);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				context.startActivity(intent);
				result = true;
			} catch (Exception e) {
				Log.d("Exception: " + e.getMessage());
			}
		}

		return result;
	}

	/**
	 * 把内容复制到剪切板
	 * 
	 * @author liudongqi
	 * @since 2013-1-8
	 * @param text
	 *            复制到剪切板的内容
	 * @param context
	 *            context
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void copyToClipboard(final String text, Context context) {
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
			// api level < 11
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getApplicationContext().getSystemService(
							Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				clipboard.setText(text);
				Toast.makeText(context, "内容已复制，长按输入框可粘贴", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			// api level >= 11
			android.content.ClipboardManager clipboard = (ClipboardManager) context
					.getApplicationContext().getSystemService(
							Context.CLIPBOARD_SERVICE);
			if (clipboard != null) {
				clipboard.setText(text);
				Toast.makeText(context, "内容已复制，长按输入框可粘贴", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
