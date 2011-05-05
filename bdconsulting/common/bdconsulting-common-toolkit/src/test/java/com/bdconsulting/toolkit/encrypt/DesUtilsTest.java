/**
 * 
 */
package com.bdconsulting.toolkit.encrypt;


/**
 * 
 * Datetime   ： 2011-5-5 下午07:38:04<br>
 * Title      :  DesUtils.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class DesUtilsTest {

	/** 
	 * main方法
	 * @author
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String test = "简道";
			DesUtils des = new DesUtils();// 自定义密钥
			System.out.println("加密前的字符：" + test);
			System.out.println("加密后的字符：" + des.encrypt(test));
			System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
