package org.lee.android.util;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lee.android.util.algorithm.GZIP;
import org.lee.android.utils.Log;

import android.content.Context;
import android.os.storage.StorageManager;


public class ExtraTools {
	/**
	 * 发送到服务端的参数数据要先进行加密处理
	 * 
	 * @param param
	 *            发送的数据（一般都是JSON格式）
	 * @return 加密过的数据
	 */
	public static String encryptePostContent(String param) {
		byte[] value = (param).getBytes();
		byte[] gzipvalue = GZIP.gZip(value);
		// 替换前两个字节的魔法数字为757B(十六进制)
		gzipvalue[0] = 0x75; // SUPPRESS CHECKSTYLE
		gzipvalue[1] = 0x7B; // SUPPRESS CHECKSTYLE

		return Base64.encodeToString(gzipvalue, Base64.DEFAULT);
	}

	/**
	 * 调用一个对象的隐藏方法。
	 * 
	 * @param obj
	 *            调用方法的对象.
	 * @param methodName
	 *            方法名。
	 * @param types
	 *            方法的参数类型。
	 * @param args
	 *            方法的参数。
	 * @return 如果调用成功，则返回true。
	 */
	public static boolean invokeHideMethod(Object obj, String methodName,
			Class<?>[] types, Object[] args) {
		boolean hasInvoked = false;
		try {
			Class<?> cls;
			if (obj instanceof Class<?>) { // 静态方法
				cls = (Class<?>) obj;
			} else { // 非静态方法
				cls = obj.getClass();
			}
			Method method = cls.getMethod(methodName, types);
			method.invoke(obj, args);
			hasInvoked = true;
			Log.d("Method \"" + methodName + "\" invoked success!");
		} catch (Exception e) {
			Log.d("Method \"" + methodName + "\" invoked failed: "
					+ e.getMessage());
		}
		return hasInvoked;
	}

	/**
	 * 调用一个对象的隐藏方法。
	 * 
	 * @param obj
	 *            调用方法的对象.
	 * @param methodName
	 *            方法名。
	 * @param types
	 *            方法的参数类型。
	 * @param args
	 *            方法的参数。
	 * @return 隐藏方法调用的返回值。
	 */
	public static Object invokeHideMethodForObject(Object obj,
			String methodName, Class<?>[] types, Object[] args) {
		Object o = null;
		try {
			Class<?> cls;
			if (obj instanceof Class<?>) { // 静态方法
				cls = (Class<?>) obj;
			} else { // 非静态方法
				cls = obj.getClass();
			}
			Method method = cls.getMethod(methodName, types);
			o = method.invoke(obj, args);
			Log.d("Method \"" + methodName + "\" invoked success!");
		} catch (Exception e) {
			Log.d("Method \"" + methodName + "\" invoked failed: "
					+ e.getMessage());
		}
		return o;
	}

	/**
	 * 调用一个对象的私有方法。
	 * 
	 * @param obj
	 *            调用方法的对象.
	 * @param methodName
	 *            方法名。
	 * @param types
	 *            方法的参数类型。
	 * @param args
	 *            方法的参数。
	 * @return 如果调用成功，则返回true。
	 */
	public static boolean invokeDeclaredMethod(Object obj, String methodName,
			Class<?>[] types, Object[] args) {
		boolean hasInvoked = false;
		try {
			Class<?> cls;
			if (obj instanceof Class<?>) { // 静态方法
				cls = (Class<?>) obj;
			} else { // 非静态方法
				cls = obj.getClass();
			}
			Method method = cls.getDeclaredMethod(methodName, types);
			method.setAccessible(true);
			method.invoke(obj, args);
			hasInvoked = true;
			Log.d("Method \"" + methodName + "\" invoked success!");
		} catch (Exception e) {
			Log.d("Method \"" + methodName + "\" invoked failed: "
					+ e.getMessage());
		}
		return hasInvoked;
	}

	/**
	 * 调用一个对象的私有方法。
	 * 
	 * @param obj
	 *            调用方法的对象.
	 * @param methodName
	 *            方法名。
	 * @param types
	 *            方法的参数类型。
	 * @param args
	 *            方法的参数。
	 * @return 私有方法调用的返回值。
	 */
	public static Object invokeDeclaredMethodForObject(Object obj,
			String methodName, Class<?>[] types, Object[] args) {
		Object o = null;
		try {
			Class<?> cls;
			if (obj instanceof Class<?>) { // 静态方法
				cls = (Class<?>) obj;
			} else { // 非静态方法
				cls = obj.getClass();
			}
			Method method = cls.getDeclaredMethod(methodName, types);
			method.setAccessible(true);
			o = method.invoke(obj, args);
			Log.d("Method \"" + methodName + "\" invoked success!");
		} catch (Exception e) {
			Log.d("Method \"" + methodName + "\" invoked failed: "
					+ e.getMessage());
		}
		return o;
	}

	/**
	 * 获取设备上某个volume对应的存储路径
	 * 
	 * @param volume
	 *            存储介质
	 * @return 存储路径
	 */
	public static String getVolumePath(Object volume) {
		String result = "";
		Object o = ExtraTools.invokeHideMethodForObject(volume, "getPath",
				null, null);
		if (o != null) {
			result = (String) o;
		}

		return result;
	}

	/**
	 * 获取设备上所有volume
	 * 
	 * @param context
	 *            context
	 * @return Volume数组
	 */
	public static Object[] getVolumeList(Context context) {
		StorageManager manager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		Object[] result = null;
		Object o = ExtraTools.invokeHideMethodForObject(manager,
				"getVolumeList", null, null);
		if (o != null) {
			result = (Object[]) o;
		}

		return result;
	}

	/**
	 * 获取设备上某个volume的状态
	 * 
	 * @param context
	 *            context
	 * @param volumePath
	 *            volumePath
	 * @return result
	 */
	public static String getVolumeState(Context context, String volumePath) {
		StorageManager manager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		String result = "";
		Object o = ExtraTools.invokeHideMethodForObject(manager,
				"getVolumeState", new Class[] { String.class },
				new Object[] { volumePath });
		if (o != null) {
			result = (String) o;
		}

		return result;
	}

	/**
	 * 把二进制byte数组生成 md5 32位 十六进制字符串，单个字节小于0xf，高位补0。
	 * 
	 * @param bytes
	 *            输入
	 * @param upperCase
	 *            true：大写， false 小写字符串
	 * @return 把二进制byte数组生成 md5 32位 十六进制字符串，单个字节小于0xf，高位补0。
	 */
	public static String toMd5(byte[] bytes, boolean upperCase) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(bytes);
			return toHexString(algorithm.digest(), "", upperCase);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
	 * 
	 * @param bytes
	 *            输入
	 * @param separator
	 *            分割线
	 * @param upperCase
	 *            true：大写， false 小写字符串
	 * @return 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
	 */
	private static String toHexString(byte[] bytes, String separator,
			boolean upperCase) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String str = Integer.toHexString(0xFF & b); // SUPPRESS CHECKSTYLE
			if (upperCase) {
				str = str.toUpperCase();
			}
			if (str.length() == 1) {
				hexString.append("0");
			}
			hexString.append(str).append(separator);
		}
		return hexString.toString();
	}

	/** 1KB. */
	public static final int KB = 1024;
	/** 1MB. */
	public static final int MB = 1024 * 1024;
	/** 1GB. */
	public static final int GB = 1024 * 1024 * 1024;

	/**
	 * 生成文件大小的字符串.
	 * 
	 * @param size
	 *            文件大小
	 * @return 表示经过格式的字符串
	 */
	public static String generateFileSizeText(long size) {
		String unit;
		Float outNumber;
		if (size < KB) {
			return size + "B";
		} else if (size < MB) {
			unit = "KB";
			outNumber = (float) size / KB;
		} else if (size < GB) {
			unit = "MB";
			outNumber = (float) size / MB;
		} else {
			unit = "GB";
			outNumber = (float) size / GB;
		}
		/*
		 * 文件大小显示格式化. 大于1KB的文件大小数字显示形如1011.11,小于1KB的文件显示具体大小
		 */
		DecimalFormat formatter = new DecimalFormat("####.##");
		return formatter.format(outNumber) + unit;
	}

	/** 根据系统时间生成文件名的格式 */
	private static SimpleDateFormat sDateFormat = null;

	/**
	 * 根据系统时间生成文件名
	 * 
	 * @param suffix
	 *            文件后缀名
	 * @return 文件名
	 */
	public static synchronized String createFileName(String suffix) {
		if (null == sDateFormat) {
			sDateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss-SSS");
		}
		Date date = new Date();
		return String.format("%s.%s", sDateFormat.format(date), suffix);
	}
}
