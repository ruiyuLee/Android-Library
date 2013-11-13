package org.lee.android.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;

public class Bitmaper {
	/**
	 * 根据Bitmap获取其字节数据。
	 * 
	 * @param bmp
	 *            Bitmap.
	 * @return 字节数据。
	 */
	public static byte[] getBitmapData(Bitmap bmp) {
		if (bmp == null) {
			return new byte[0];
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final int quality = 100;
		bmp.compress(Bitmap.CompressFormat.PNG, quality, out);
		return out.toByteArray();
	}
}
