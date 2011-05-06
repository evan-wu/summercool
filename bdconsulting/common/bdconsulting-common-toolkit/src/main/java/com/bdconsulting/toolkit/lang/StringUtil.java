package com.bdconsulting.toolkit.lang;

import java.io.UnsupportedEncodingException;

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
	 * 半角转全角
	 * 
	 * @param input String.
	 * @return 全角字符串.
	 */
	public static String ToSBC(String input) {
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
		//if(StringUtil)
		
		return null;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String str = "123abc试试看?";
		byte[] b = str.getBytes();
		System.out.println(str + "共包含" + b.length + "个字节");
	}

}
