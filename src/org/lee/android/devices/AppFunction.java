package org.lee.android.devices;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.lee.android.util.Log;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

public class AppFunction {

	public static void shareTextTo(Context context, String content) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	/**
	 * 为程序创建桌面快捷方式。
	 * 
	 * @param activity
	 *            指定当前的Activity为快捷方式启动的对象
	 * @param nameId
	 *            快捷方式的名称
	 * @param iconId
	 *            快捷方式的图标
	 * @param appendFlags
	 *            需要在快捷方式启动应用的Intent中附加的Flag
	 */
	public static void addShortcut(Activity activity, int nameId, int iconId,
			int appendFlags) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				activity.getString(nameId));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象
		ComponentName comp = new ComponentName(activity.getPackageName(),
				activity.getClass().getName());
		Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		if (appendFlags != 0) {
			intent.addFlags(appendFlags);
		}
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				activity, iconId);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		activity.sendBroadcast(shortcut);
	}

	/**
	 * 获取指定resource id的Uri.
	 * 
	 * @param packageContext
	 *            指定的包。
	 * @param res
	 *            资源ID
	 * @return 资源的Uri
	 */
	public static Uri getResourceUri(Context packageContext, int res) {
		try {
			Resources resources = packageContext.getResources();
			return getResourceUri(resources, packageContext.getPackageName(),
					res);
		} catch (Resources.NotFoundException e) {
			Log.d("Resource not found: " + res + " in "
					+ packageContext.getPackageName());
			return null;
		}
	}

	/**
	 * 根据ApplicationInfo 获取Resource Uri.
	 * 
	 * @param context
	 *            context
	 * @param appInfo
	 *            ApplicationInfo
	 * @param res
	 *            资源ID
	 * @return 资源的Uri
	 */
	public static Uri getResourceUri(Context context, ApplicationInfo appInfo,
			int res) {
		try {
			Resources resources = context.getPackageManager()
					.getResourcesForApplication(appInfo);
			return getResourceUri(resources, appInfo.packageName, res);
		} catch (PackageManager.NameNotFoundException e) {
			Log.d("Resources not found for " + appInfo.packageName);
			return null;
		} catch (Resources.NotFoundException e) {
			Log.d("Resource not found: " + res + " in " + appInfo.packageName);
			return null;
		}
	}

	/**
	 * 构建Resource Uri。
	 * 
	 * @param resources
	 *            应用关联的资源。
	 * @param appPkg
	 *            应用包名。
	 * @param res
	 *            资源包名
	 * @return 资源的Uri
	 * @throws Resources.NotFoundException
	 *             资源没找到的异常。
	 */
	private static Uri getResourceUri(Resources resources, String appPkg,
			int res) throws Resources.NotFoundException {
		String resPkg = resources.getResourcePackageName(res);
		String type = resources.getResourceTypeName(res);
		String name = resources.getResourceEntryName(res);
		return makeResourceUri(appPkg, resPkg, type, name);
	}

	/**
	 * 构建Resource Uri.
	 * 
	 * @param appPkg
	 *            应用包名。
	 * @param resPkg
	 *            资源包名
	 * @param type
	 *            资源类型
	 * @param name
	 *            资源名
	 * @return 资源的Uri
	 */
	private static Uri makeResourceUri(String appPkg, String resPkg,
			String type, String name) {
		// 了解type种类。
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(ContentResolver.SCHEME_ANDROID_RESOURCE);
		uriBuilder.encodedAuthority(appPkg);
		uriBuilder.appendEncodedPath(type);
		if (!appPkg.equals(resPkg)) {
			uriBuilder.appendEncodedPath(resPkg + ":" + name);
		} else {
			uriBuilder.appendEncodedPath(name);
		}
		return uriBuilder.build();
	}

	/** 用来存储widget信息的文件名。 */
	public static final String WIDGET_INFO_FILE = "widgetInfo";

	/**
	 * 通过AppWidgetId与指定的key前缀来保存值。该函数主要用来保存每个widget实例的私有值。
	 * 支持的值类型有boolean、Float、Integer、Long以及String。
	 * 注意，如果value为null，则将会以String形式保存。
	 * 如果value为非支持的类型，则抛出UnsupportedOperationException。
	 * 
	 * @param context
	 *            Context.
	 * @param appWidgetId
	 *            每个widget实例对应的id.
	 * @param keyPrefix
	 *            key前缀，与appWidgetId联合生成key值.
	 * @param value
	 *            需要保存的值。
	 */
	public static void saveValueWithWidgetId(Context context, int appWidgetId,
			String keyPrefix, Serializable value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		String key = keyPrefix + appWidgetId;
		if (value == null) {
			editor.putString(key, null);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		} else {
			throw new UnsupportedOperationException();
		}

		editor.commit();
	}

	/**
	 * 通过AppWidgetId与指定的key前缀来获取值。该函数主要用来获取每个widget实例的私有保存值。
	 * 支持的值类型有boolean、Float、Integer、Long以及String。
	 * 注意，如果defaultValue为null，则将会以String形式获取值。
	 * 如果defaultValue为非支持的类型，则抛出UnsupportedOperationException。
	 * 
	 * @param context
	 *            Context.
	 * @param appWidgetId
	 *            每个widget实例对应的id.
	 * @param keyPrefix
	 *            key前缀，与appWidgetId联合生成key值.
	 * @param defaultValue
	 *            默认值.
	 * @return 如果存在与key配对的值，则返回该值；否则返回defaultValue。
	 */
	public static Object getValueWithWidgetId(Context context, int appWidgetId,
			String keyPrefix, Serializable defaultValue) {
		Object value = null;
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String key = keyPrefix + appWidgetId;
		if (defaultValue == null) {
			value = preferences.getString(key, null);
		} else if (defaultValue instanceof Boolean) {
			value = preferences.getBoolean(key, (Boolean) defaultValue);
		} else if (defaultValue instanceof Float) {
			value = preferences.getFloat(key, (Float) defaultValue);
		} else if (defaultValue instanceof Integer) {
			value = preferences.getInt(key, (Integer) defaultValue);
		} else if (defaultValue instanceof Long) {
			value = preferences.getLong(key, (Long) defaultValue);
		} else if (defaultValue instanceof String) {
			value = preferences.getString(key, (String) defaultValue);
		} else {
			throw new UnsupportedOperationException();
		}

		// <added by qumiao 2012.3.26 BEGIN
		// 如果用户强制清除数据，SharedPreferences将会被擦除，所以再从sdcard中读取信息
		if (value == defaultValue) {
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)
					|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

				File file = SDCard.getExternalFilesDir(context,
						WIDGET_INFO_FILE);
				// modify by qumiao 可能不存在外部存储
				if (file == null) {
					return value;
				}
				// end modify

				if (file.canRead()) {
					FileInputStream in = null;
					try {
						in = new FileInputStream(file);
						ObjectInputStream objectIn = new ObjectInputStream(in);
						@SuppressWarnings("unchecked")
						HashMap<String, Serializable> map = (HashMap<String, Serializable>) objectIn
								.readObject();
						value = map.get(key);
						in.close();
					} catch (Exception e) {
						e.printStackTrace();
						closeSafely(in);
					}
				}
			}
		}

		if (value == null) {
			value = defaultValue;
		}
		// added by qumiao 2012.3.26 END>

		return value;
	}

	/**
	 * 安全关闭.
	 * 
	 * @param closeable
	 *            Closeable.
	 */
	public static void closeSafely(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取签名信息
	 * 
	 * @param context
	 *            : 上下文
	 * @param pkgName
	 *            package name
	 * @return 签名String
	 */
	public static String getSign(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();
		// modify by qiaopu 2012-12-06 改进获取签名的方法
		// List<PackageInfo> apps = pm
		// .getInstalledPackages(PackageManager.GET_SIGNATURES);
		//
		// Iterator<PackageInfo> iter = apps.iterator();
		//
		// PackageInfo info;
		// while (iter.hasNext()) {
		// info = iter.next();
		//
		// // 按包名取签名
		// if (TextUtils.equals(info.packageName, pkgName)) {
		// if (info.signatures.length > 0) {
		// return info.signatures[0].toCharsString();
		// }
		// }
		// }
		// return "";

		String sign = "";
		try {
			PackageInfo packageInfo = pm.getPackageInfo(pkgName,
					PackageManager.GET_SIGNATURES);

			if (packageInfo != null && packageInfo.signatures.length > 0) {
				sign = packageInfo.signatures[0].toCharsString();
			}

		} catch (Exception e) {
			Log.d("get sign error!!! Exception:" + e);
		}

		return sign;
		// end modify
	}

}
