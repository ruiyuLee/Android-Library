package org.lee.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

public class HttpTools {
	/**
	 * 对url中的参数进行编码.
	 * 
	 * @param url
	 *            String
	 * @return 编码后的url
	 */
	public static String encodeUrl(String url) {
		int index = url.indexOf("?");
		if (index != -1 && index + 1 <= url.length()) { // 对url 问号后的部分编码
			String tmpUrl = url.substring(0, index + 1);
			String param = url.substring(index + 1);
			param = urlEncode(param);
			url = tmpUrl + param;
		}
		return url;
	}

	/**
	 * 对url参数的value进行utf-8编码.
	 * 
	 * @param str
	 *            Url.
	 * @return Url.
	 */
	private static String urlEncode(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}

		StringBuffer sb = new StringBuffer(str.length() * 2);
		String[] pairs = str.split("&");
		int size = pairs.length;
		String pair;
		for (int i = 0; i < size; i++) {
			pair = pairs[i];
			int idx = pair.indexOf("=");
			if (idx > 0 && pair.indexOf("%") < 0) {
				String value = pair.substring(idx + 1);
				sb.append(pair.substring(0, idx));
				sb.append('=');
				sb.append(Uri.encode(value));
			} else {
				sb.append(pair);
			}
			if (i < size - 1) {
				sb.append('&');
			}
		}

		return sb.toString().trim();
	}

	/**
	 * 如果url没有scheme，则增加http
	 * 
	 * @param url
	 *            url
	 * @return url
	 */
	public static String addSchemeIfNeed(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		// 为了效率考虑，没有用webaddress进行正则处理，指示简单判断http和https
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		} else {
			return "http://" + url;
		}
	}

	/*
	 * SEARHBOX-3123 【Sprint3】对于一些特殊链接，例如http://m.ubox.cn?c=jsxz, "?"不出现在"/"
	 * 后也应该满足,这里 在url查询参数处提供一个稍微粗粒度的检查
	 */
	public static final Pattern COARSE_WEB_URL = Pattern
			.compile("((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
					+ "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
					+ "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
					+ "((?:(?:["
					+ Patterns.GOOD_IRI_CHAR
					+ "]["
					+ Patterns.GOOD_IRI_CHAR
					+ "\\-]{0,64}\\.)+" // named host
					+ Patterns.TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
					+ "|(?:(?:25[0-5]|2[0-4]" // or ip address
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
					+ "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
					+ "|[1-9][0-9]|[0-9])))"
					+ "(?:\\:\\d{1,5})?)" // plus option port number
					+ "(\\?(?:(?:[" // 在源码基础上修改处，减去个'/'
					+ Patterns.GOOD_IRI_CHAR
					+ "\\;\\?\\:\\@\\&\\=\\#\\~" // plus option query params
													// //在源码基础上修改处，减去个'/'
					+ "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
					+ "(?:\\b|$)"); // and finally, a word boundary or end of
									// input. This is to stop foo.sure from
									// matching as foo.su

	/**
	 * 判断一个字符串是否为合法url
	 * 
	 * @param query
	 *            String
	 * @return true: 是合法url
	 */
	public static boolean isUrl(String query) {
		Matcher matcher = Patterns.WEB_URL.matcher(query);
		if (matcher.matches()) {
			return true;
		} else {
			return COARSE_WEB_URL.matcher(query).matches();
		}

	}

	/**
	 * 对url进行校正.
	 * 
	 * @param inUrl
	 *            输入的url
	 * @return 输出的url
	 */
	public static String fixUrl(String inUrl) {
		// Converting the url to lower case
		// duplicates functionality in smartUrlFilter().
		// However, changing all current callers of fixUrl to
		// call smartUrlFilter in addition may have unwanted
		// consequences, and is deferred for now.
		if (inUrl == null) {
			return "";
		}
		int colon = inUrl.indexOf(':');
		boolean allLower = true;
		for (int index = 0; index < colon; index++) {
			char ch = inUrl.charAt(index);
			if (!Character.isLetter(ch)) {
				break;
			}
			allLower &= Character.isLowerCase(ch);
			if (index == colon - 1 && !allLower) {
				inUrl = inUrl.substring(0, colon).toLowerCase()
						+ inUrl.substring(colon);
			}
		}
		if (inUrl.startsWith("http://") || inUrl.startsWith("https://")) {
			return inUrl;
		}
		if (inUrl.startsWith("http:") || inUrl.startsWith("https:")) {
			if (inUrl.startsWith("http:/") || inUrl.startsWith("https:/")) {
				inUrl = inUrl.replaceFirst("/", "//");
			} else {
				inUrl = inUrl.replaceFirst(":", "://");
			}
		}
		return inUrl;
	}

	/**
	 * 获取url中的某个字段，字段定义：&fieldName=fieldValue
	 * 
	 * @param url
	 *            : String
	 * @param fieldName
	 *            : 字段名
	 * @return fieldValue 字段值
	 */
	public static String getUrlField(String url, String fieldName) {
		return getUrlField(url, fieldName, "=", "&");
	}

	/**
	 * 获取url中的某个字段，字段定义：fieldName=fieldValue
	 * 
	 * @param url
	 *            : String
	 * @param fieldName
	 *            : 字段名
	 * @param keyvalueDivider
	 *            : key与value直接的连接符
	 * @param divideStr
	 *            ： 分隔字符串
	 * 
	 * @return fieldValue 字段值
	 */
	public static String getUrlField(String url, String fieldName,
			String keyvalueDivider, String divideStr) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fieldName)
				|| TextUtils.isEmpty(keyvalueDivider)
				|| TextUtils.isEmpty(divideStr)) {
			return "";
		}

		fieldName = fieldName + keyvalueDivider;

		// 不转换为小写，避免pu参数是urlencode的情况，将%2C之类的转成%2c，导致替换失败
		// String tmp = url.toLowerCase();
		String tmp = url;
		int index = tmp.indexOf("?");
		if (index == -1) {
			index = 0;
		}

		int p = tmp.indexOf(fieldName, index);
		int q;
		if (p != -1) {
			q = tmp.indexOf(divideStr, p);
			if (q != -1) {
				tmp = tmp.substring(p + fieldName.length(), q);
			} else {
				tmp = tmp.substring(p + fieldName.length());
			}
			// 不decode，避免pu参数是urlencode的情况，decode导致替换失败
			// return Uri.decode(tmp);
			return tmp;
		}
		return "";
	}

}
