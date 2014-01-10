package org.lee.android.util.advanced;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lee.android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 服务器下发激活时间XML解析器
 * 
 * @author dongfengyu
 * 
 */
public final class ActiveTimeParser {

	/** 解析器单例 */
	private static ActiveTimeParser mParser = null;

	/** Defines a bridge from XML sources (files, stream etc.) to DOM trees. */
	private DocumentBuilder mDocumentBuilder = null;

	/**
	 * 私有构造函数
	 */
	private ActiveTimeParser() {
		init();
	}

	/**
	 * 获取单例
	 * 
	 * @return ActiveTimeParser单例
	 */
	public static ActiveTimeParser getInstance() {
		if (mParser == null) {
			mParser = new ActiveTimeParser();
		}
		return mParser;
	}

	/**
	 * 初始化函数
	 */
	private void init() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			mDocumentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.d("ParserConfigurationException");
		}
	}

	/**
	 * 通过字节流解析出Ding数据
	 * 
	 * @param is
	 *            输入流
	 * @return Ding数据
	 */
	public String parse(InputStream is) {

		String time = "0";
		try {
			Document doc = mDocumentBuilder.parse(is);
			Node node = doc.getElementsByTagName("timestamp").item(0);
			if (node != null) {
				time = node.getFirstChild().getNodeValue();
			}
			return time;
		} catch (SAXException e) {
			Log.d("SAXException");
		} catch (IOException e) {
			Log.d("IOException");
		} catch (Exception e) {
			Log.d("getDingFromXml-method-exception");
		}
		return null;
	}
}
