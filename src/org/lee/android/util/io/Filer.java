package org.lee.android.util.io;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Filer {
	/**
	 * 缓存文件
	 * 
	 * @param context
	 *            Context Object
	 * @param file
	 *            本地文件名
	 * @param data
	 *            要保存的数据
	 * @param mode
	 *            打开文件的方式
	 * @return 是否保存成功
	 */
	public static boolean cache(Context context, String file, String data,
			int mode) {
		return cache(context, file, data.getBytes(), mode);
	}

	/**
	 * 缓存文件
	 * 
	 * @param context
	 *            Context Object
	 * @param file
	 *            本地文件名
	 * @param data
	 *            要保存的数据
	 * @param mode
	 *            打开文件的方式
	 * @return 是否保存成功
	 */
	public static boolean cache(Context context, String file, byte[] data,
			int mode) {
		boolean bResult = false;
		if (null != data && data.length > 0) {
			FileOutputStream fos = null;
			try {
				fos = context.openFileOutput(file, mode);
				fos.write(data);
				fos.flush();
				bResult = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != fos) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return bResult;
	}

	/**
	 * 读取文件
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String read(Context context, String file) {
		try {
			FileInputStream fin = context.openFileInput(file);
			InputStreamReader isr = new InputStreamReader(fin);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer buffer = new StringBuffer();
			String text;
			while ((text = br.readLine()) != null)
				buffer.append(text);
			return buffer.toString();
		} catch (IOException e) {
            e.printStackTrace();
			return null;
		}
	}

	/** unzip buffer size. */
	private static final int UNZIP_BUFFER = 2048;

	/**
	 * unzip file.
	 * 
	 * @param srcFileName
	 *            source file name.
	 * @param savePath
	 *            unzip save path.
	 */
	public static void unzipFile(String srcFileName, String savePath) {
		if (srcFileName == null) {
			return;
		}
		if (savePath == null) {
			savePath = new File(srcFileName).getParent();
		}

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			ZipFile zipFile = new ZipFile(srcFileName);
			Enumeration<? extends ZipEntry> enu = zipFile.entries();

			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
				File saveFile = new File(savePath + "/" + zipEntry.getName());
				if (zipEntry.isDirectory() && !saveFile.exists()) {
					saveFile.mkdirs();
					continue;
				}
				if (!saveFile.exists()) {
					saveFile.createNewFile();
				}
				bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
				FileOutputStream fos = new FileOutputStream(saveFile);
				bos = new BufferedOutputStream(fos, UNZIP_BUFFER);

				int count = -1;
				byte[] buf = new byte[UNZIP_BUFFER];
				while ((count = bis.read(buf, 0, UNZIP_BUFFER)) != -1) {
					bos.write(buf, 0, count);
				}

				bos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeSafely(bos);
			closeSafely(bis);
		}
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
	 * 删除指定文件、文件夹内容
	 * 
	 * @param path
	 *            文件或文件夹
	 * @return 是否成功删除
	 */
	public static boolean deleteFile(String path) {
		boolean isDeletedAll = true;
		File file = new File(path);
		if (file.exists()) {
			// 判断是否是文件,直接删除文件
			if (file.isFile()) {
				isDeletedAll &= file.delete();
				// 遍历删除一个目录
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					isDeletedAll &= deleteFile(files[i].getAbsolutePath()); // 迭代删除文件夹内容
				}
				isDeletedAll &= file.delete();
			}
		}
		return isDeletedAll;
	}
}
