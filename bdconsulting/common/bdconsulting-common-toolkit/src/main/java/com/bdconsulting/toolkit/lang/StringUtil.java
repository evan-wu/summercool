package com.bdconsulting.toolkit.lang;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Datetime   ： 2011-5-5 下午01:40:14<br>
 * Title      :  StringUtil.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class StringUtil {

	/**
	 * 默认编码UTF-8
	 * 
	 * @param str
	 * @return
	 */
	public static String decode(String str) {
		if (str == null) {
			return StringUtils.EMPTY;
		}

		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 默认编码UTF-8
	 * 
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		if (str == null) {
			return StringUtils.EMPTY;
		}

		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 半角转全角
	 * 
	 * @param input String.
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
		if (input == null) {
			return input;
		}

		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input String.
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		if (input == null) {
			return input;
		}

		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 对字符串进行重新分组，每个字符串数据最大元素不不能超过bytesSize
	 * 
	 * @param input
	 * @param bytesSize
	 * @return
	 */
	public static String[] toArray(String input, int bytesSize) {
		return toArray(input, bytesSize, "UTF-8");
	}

	/**
	 * 对字符串进行重新分组，每个字符串数据最大元素不不能超过bytesSize
	 * 
	 * @param input
	 * @param bytesSize
	 * @param charsetName
	 * @return
	 */
	public static String[] toArray(String input, int bytesSize, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		int maxBytesPerChar = new Float(charset.newEncoder().maxBytesPerChar()).intValue();

		if (input == null) {
			return new String[] {};
		}
		if (bytesSize < maxBytesPerChar) {
			throw new RuntimeException("bytesSize不能小于" + maxBytesPerChar + "!");
		}

		byte[] inputArray;
		try {
			inputArray = input.getBytes(charsetName);
			if (inputArray.length <= bytesSize) {
				return new String[] { input };
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		List<String> stringList = new ArrayList<String>();
		int currentBytesSize = 0;
		StringBuilder sb = new StringBuilder();
		char[] charArray = input.toCharArray();

		for (int i = 0; i < charArray.length; i++) {
			if (!((charArray[i] & 0xFF80) != 0)) { // ascii码判断
				currentBytesSize = currentBytesSize + 1;
				sb.append(charArray[i]);
				if ((currentBytesSize + maxBytesPerChar) >= bytesSize) {
					stringList.add(sb.toString());
					sb = new StringBuilder();
					currentBytesSize = 0;
				} else if (i == (charArray.length - 1)) {
					stringList.add(sb.toString());
					sb = new StringBuilder();
					currentBytesSize = 0;
				}
			} else {
				currentBytesSize = currentBytesSize + maxBytesPerChar;
				sb.append(charArray[i]);
				if ((currentBytesSize + maxBytesPerChar) >= bytesSize) {
					stringList.add(sb.toString());
					sb = new StringBuilder();
					currentBytesSize = 0;
				} else if (i == (charArray.length - 1)) {
					stringList.add(sb.toString());
					sb = new StringBuilder();
					currentBytesSize = 0;
				}
			}
		}

		String[] stringArray = new String[stringList.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = stringList.get(i);
		}

		return stringArray;
	}

}
