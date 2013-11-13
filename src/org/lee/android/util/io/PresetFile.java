package org.lee.android.util.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

/**
 * 预设文件处理类
 * 
 * @author liubaowen
 * 
 */
public final class PresetFile {
	/** TAG */
	private static final String TAG = PresetFile.class.getSimpleName();
	/** DEBUG */
	private static final boolean DEBUG = true;
	/** 预设文件 */
	private static final String PRESET_FILE = "preset/preset_file.xml";
	/** Defines a bridge from XML sources (files, stream etc.) to DOM trees. */
	private DocumentBuilder mDocumentBuilder = null;
	/** Document */
	private Document mDoc = null;
	/** 上下文 */
	private Context mContext;
	/** 单例 */
	private static volatile PresetFile instance = null;

	/**
	 * 单例方法
	 * 
	 * @param context
	 *            Context
	 * @return PresetFile单例
	 */
	public static PresetFile getInstance(Context context) {
		if (instance == null) {
			synchronized (PresetFile.class) {
				if (instance == null) {
					instance = new PresetFile(context);
				}
			}
		}

		return instance;
	}

	/**
	 * 私有构造函数
	 * 
	 * @param context
	 *            Context
	 */
	private PresetFile(Context context) {
		mContext = context;
		init();
		parsePresetFile();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (DEBUG) {
			Log.i(TAG, "init preset file");
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			mDocumentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			if (DEBUG) {
				Log.d(TAG, "ParserConfigurationException");
			}
		}
	}

	/**
	 * 解析预设文件
	 */
	private void parsePresetFile() {
		try {
			InputStream is = mContext.getAssets().open(PRESET_FILE);
			mDoc = mDocumentBuilder.parse(is);
		} catch (IOException e) {
			if (DEBUG) {
				Log.d(TAG, "parsePresetFile IOException");
			}
		} catch (SAXException e) {
			if (DEBUG) {
				Log.d(TAG, "parsePresetFile SAXException");
			}
		}
	}

	/**
	 * 获取预设文件根节点
	 * 
	 * @return 预设文件的根节点
	 */
	public Element getRootElement() {
		return mDoc.getDocumentElement();
	}

}
