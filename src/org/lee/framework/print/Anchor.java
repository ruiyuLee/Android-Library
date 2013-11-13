package org.lee.framework.print;

import org.lee.java.util.Empty;
import org.lee.java.util.ToString;
import org.lee.java.util.Type;

/**
 * 锚点类
 * 
 * @author ruiyuLee
 * 
 */
public class Anchor {

	static final boolean DEBUG = true;
	static final boolean hasPackageName = false;

	/**
	 * 数组打印分离符
	 */
	public static String array_separator = ", ";
	public static String MessageEnder = "";
	public static String MessageHeader = ":\n";

	public static void anchor() {
		anchor("");
	}

	/**
	 * 基方法
	 * 
	 * @param cls
	 */
	public static void anchor(Object cls) {
		if (!DEBUG) {
			return;
		}
		StackTraceElement[] stack = new Exception().getStackTrace();
		String me = stack[0].getMethodName();
		StringBuffer stringBuffer = new StringBuffer();
		int i = 0;
		int size = stack.length;
		for (; i < size; i++) {
			if (!me.equals(stack[i].getMethodName())) {
				if (Type.canToString(cls)) {
					stringBuffer.append(stack[i]
							+ (Empty.isEmpty(String.valueOf(cls)) ? " "
									: MessageHeader + String.valueOf(cls)));
				} else {
					stringBuffer.append(stack[i] + MessageHeader + cls);
				}
				break;
			}
		}

		if (!hasPackageName) {
			String fullClassName = stack[i].toString();
			fullClassName = fullClassName.substring(0,
					fullClassName.lastIndexOf("."));
			fullClassName = fullClassName.substring(0,
					fullClassName.lastIndexOf("."));
			String packageName = fullClassName.substring(0,
					fullClassName.lastIndexOf("."));
			stringBuffer.delete(0, packageName.length() + 1);
		}
		println(stringBuffer.toString() + MessageEnder);
	}

	public static void p(int[] arrays) {
		anchor("");
		String msg = ToString.toString(arrays);
		println(msg + MessageEnder);
	}

	public static void p(String[] arrays) {
		anchor("");
		String msg = ToString.toString(arrays);
		println(msg + MessageEnder);
	}

	public static final String TAG = "TAG";

	protected static void println(String msg) {
		android.util.Log.d(TAG, msg);
	}
}
