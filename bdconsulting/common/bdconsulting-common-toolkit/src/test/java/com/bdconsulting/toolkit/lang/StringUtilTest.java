/**
 * 
 */
package com.bdconsulting.toolkit.lang;

/**
 * Datetime   ： 2011-5-5 下午05:11:58<br>
 * Title      :  StringUtilTest.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class StringUtilTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String str1 = "Ｈｅｌｌｏ　Ｗｏｒｌｄ！";
		System.out.println("全角转半角: " + str1 + " -- > " + StringUtil.ToDBC(str1));

		String str2 = "Hello World!";
		System.out.println("全角转半角: " + str2 + " -- > " + StringUtil.ToSBC(str2));

	}

}
