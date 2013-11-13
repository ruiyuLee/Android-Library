package org.lee.android.log;

import org.lee.framework.print.Anchor;
import org.lee.java.util.ToString;

/**
 * 自定义Log
 * 
 * @author ruiyuLee
 * 
 */
public class ALog extends Anchor {

	public static final String TAG = "TAG";

	public static void d() {
		println(null);
	}

	public static void d(Object cls) {
		println(cls.toString());
	}

	public static void d(String[] a) {
		String str = ToString.toString(a);
		println(str);
	}
}
