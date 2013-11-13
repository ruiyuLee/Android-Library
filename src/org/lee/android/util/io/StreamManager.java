package org.lee.android.util.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Xml.Encoding;

public class StreamManager {

	/**
	 * 从输入流中获得字符串.
	 * 
	 * @param inputStream
	 *            {@link InputStream}
	 * @return 字符串
	 */
	public static String getStringFromInput(InputStream inputStream) {

		byte[] buf = getByteFromInputStream(inputStream);
		if (buf != null) {
			return new String(buf);
		}

		return null;
	}

	// add by fujiaxing 20120110 END>
	/**
	 * 从输入流中读出byte数组
	 * 
	 * @param inputStream
	 *            输入流
	 * @return byte[]
	 */
	public static byte[] getByteFromInputStream(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // SUPPRESS CHECKSTYLE
		do {
			int len = 0;
			try {
				len = inputStream.read(buffer, 0, buffer.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (len != -1) {
				// stringBuffer.append(new String(buffer, 0, len));
				bos.write(buffer, 0, len);
			} else {
				break;
			}
		} while (true);

		buffer = bos.toByteArray();
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer;
	}

	/**
	 * File buffer stream size.
	 */
	public static final int FILE_STREAM_BUFFER_SIZE = 8192;

	/**
	 * stream to bytes
	 * 
	 * @param is
	 *            inputstream
	 * @return bytes
	 */
	public static byte[] streamToBytes(InputStream is) {
		if (null == is) {
			return null;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[FILE_STREAM_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = is.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return output.toByteArray();
	}

	/**
	 * 转换Stream成string
	 * 
	 * @param is
	 *            Stream源
	 * @return 目标String
	 */
	public static String streamToString(InputStream is) {
		return streamToString(is, Encoding.UTF_8.toString());
	}

	/**
	 * 按照特定的编码格式转换Stream成string
	 * 
	 * @param is
	 *            Stream源
	 * @param enc
	 *            编码格式
	 * @return 目标String
	 */
	public static String streamToString(InputStream is, String enc) {
		if (null == is) {
			return null;
		}

		StringBuilder buffer = new StringBuilder();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, enc), FILE_STREAM_BUFFER_SIZE);
			while (null != (line = reader.readLine())) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buffer.toString();
	}
}
