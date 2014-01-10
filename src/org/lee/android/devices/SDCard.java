package org.lee.android.devices;

import java.io.File;
import java.io.IOException;

import org.lee.android.util.Log;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class SDCard {

	/** SD卡存储cache路径 */
	private static final String EXTERNAL_STORAGE_DIRECTORY = "/baidu/searchbox";
	/** SD卡存储cache路径 */
	private static final String EXTERNAL_STORAGE_WIDGET_DIRECTORY = "/baidu/searchbox/widget";

	/**
	 * 获取外置存储目录中的文件。
	 * 
	 * @param context
	 *            Context.
	 * @param fileName
	 *            文件名.
	 * @return 文件.
	 */
	public static File getExternalFilesDir(Context context, String fileName) {
		File file = null;

		// if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
		// file = ExternalFilesDir7.getExternalFilesDir(context, fileName);
		// } else {
		// // modify by qumiao 可能不存在外部存储
		// File dir = context.getExternalFilesDir(null);
		// if (dir != null) {
		// String path = dir.getAbsolutePath();
		// file = new File(path, fileName);
		// }
		// // end modify
		// }
		// modify by liliang04
		// android defined file directory will be cleaned for "clear user data"
		// operation,
		// use a safer place instead
		File dir = new File(Environment.getExternalStorageDirectory(),
				EXTERNAL_STORAGE_WIDGET_DIRECTORY);

		if (ensureDirectoryExist(dir)) {
			file = new File(dir, fileName);
		}

		return file;
	}

	/**
	 * 获得百度搜索在SD卡上的默认存储目录下的一个文件
	 * 
	 * @param fileName
	 *            文件名
	 * @return 在SD卡默认存储目录下的File对象。若不为null,则可以使用，默认存储目录保证存在；若为null，则默认存储目录创建失败。
	 */
	public static File getPublicExternalDiretory(String fileName) {
		File dir = new File(Environment.getExternalStorageDirectory(),
				EXTERNAL_STORAGE_DIRECTORY);
		File file = null;
		if (ensureDirectoryExist(dir)) {
			file = new File(dir, fileName);
		}

		return file;
	}

	/**
	 * 确定SD卡缓存路径在使用前已经存在.
	 * 
	 * @param dir
	 *            目录
	 * @return 是否建立成功
	 */
	public static boolean ensureDirectoryExist(final File dir) {
		if (dir == null) {
			return false;
		}
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (SecurityException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * SDK2.1(API 7)获取外置存储目录中的文件。
	 * 
	 * @author qumiao
	 * 
	 */
	private static class ExternalFilesDir7 {
		/**
		 * 获取外置存储目录中的文件。
		 * 
		 * @param context
		 *            Context.
		 * @param fileName
		 *            文件名.
		 * @return File
		 */
		static File getExternalFilesDir(Context context, String fileName) {
			String root = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String path = "/Android/data/" + context.getPackageName()
					+ "/files/";
			return new File(root + path, fileName);
		}
	}

	/**
	 * 判断外部存储是否可写
	 * 
	 * 此方法内采用文件读写操作来检测，所以相对比较耗时，请谨慎使用。
	 * 
	 * @return true:可写; false 不存在/没有mounted/不可写
	 */
	public static boolean isExternalStorageWriteable() {
		boolean writealbe = false;
		long start = System.currentTimeMillis();
		if (TextUtils.equals(Environment.MEDIA_MOUNTED,
				Environment.getExternalStorageState())) {
			File esd = Environment.getExternalStorageDirectory();
			if (esd.exists() && esd.canWrite()) {
				File file = new File(esd,
						".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB");
				if (file.exists()) {
					writealbe = true;
				} else {
					try {
						writealbe = file.createNewFile();
					} catch (IOException e) {
						Log.d("isExternalStorageWriteable() can't create test file.");
					}
				}
			}
		}
		long end = System.currentTimeMillis();
		Log.d("Utility.isExternalStorageWriteable(" + writealbe + ") cost "
				+ (end - start) + "ms.");
		return writealbe;
	}
}
