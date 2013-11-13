package org.lee.android.util.advanced;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class Advanced {
	/**
	 * @param convertPlus
	 *            true to convert '+' to ' '.
	 * @param s
	 *            s
	 * @param charset
	 *            charset
	 * @throws UnsupportedEncodingException
	 * @return decodestring
	 */
	public static String decode(String s, boolean convertPlus, String charset)
			throws UnsupportedEncodingException {
		if (s.indexOf('%') == -1 && (!convertPlus || s.indexOf('+') == -1)) {
			return s;
		}

		StringBuilder result = new StringBuilder(s.length());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < s.length();) {
			char c = s.charAt(i);
			if (c == '%') {
				do {
					if (i + 2 >= s.length()) {
						throw new IllegalArgumentException(
								"Incomplete % sequence at: " + i);
					}
					int d1 = hexToInt(s.charAt(i + 1));
					int d2 = hexToInt(s.charAt(i + 2));
					if (d1 == -1 || d2 == -1) {
						throw new IllegalArgumentException(
								"Invalid % sequence " + s.substring(i, i + 3)
										+ " at " + i); // SUPPRESS CHECKSTYLE
					}
					out.write((byte) ((d1 << 4) + d2)); // SUPPRESS CHECKSTYLE
					i += 3; // SUPPRESS CHECKSTYLE
				} while (i < s.length() && s.charAt(i) == '%');
				result.append(new String(out.toByteArray(), charset));
				out.reset();
			} else {
				if (convertPlus && c == '+') {
					c = ' ';
				}
				result.append(c);
				i++;
			}
		}
		return result.toString();
	}

	/**
	 * Like {@link Character#digit}, but without support for non-ASCII
	 * characters.
	 * 
	 * @param c
	 *            c
	 * @return hex int
	 */
	private static int hexToInt(char c) {
		if ('0' <= c && c <= '9') {
			return c - '0';
		} else if ('a' <= c && c <= 'f') {
			return 10 + (c - 'a'); // SUPPRESS CHECKSTYLE
		} else if ('A' <= c && c <= 'F') {
			return 10 + (c - 'A'); // SUPPRESS CHECKSTYLE
		} else {
			return -1;
		}
	}

	/**
	 * server如果下发为gip，则获取gzip inputstream .
	 * 
	 * @param resEntity
	 *            {@link HttpEntity}
	 * @return gzip InputStream or null
	 * @throws IOException
	 *             {@link IOException}
	 */
	public static InputStream getGzipInputStream(HttpEntity resEntity)
			throws IOException {
		Header header = resEntity.getContentEncoding();
		String contentEncoding = null;
		InputStream inputStream = null;
		if (header != null) {
			contentEncoding = header.getValue();
			if (contentEncoding.toLowerCase().indexOf("gzip") != -1) {
				inputStream = new GZIPInputStream(resEntity.getContent());
			}
		}

		return inputStream;
	}
}
