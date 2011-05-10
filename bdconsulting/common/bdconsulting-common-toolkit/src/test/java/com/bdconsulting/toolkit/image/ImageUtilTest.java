package com.bdconsulting.toolkit.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.bdconsulting.toolkit.lang.IOUtil;

/**
 * 
 * Datetime   ： 2011-5-10 下午03:50:49<br>
 * Title      :  ImageUtilTest.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageUtilTest {

	public static void test1() {
		try {
			InputStream in = new FileInputStream(new File("D:/个人资料/照片/me.jpg"));
			byte[] byteArray = IOUtil.transformInputstream(in);
			ImageUtil.compressImage2JPG(byteArray, new File("d:/new_1.jpg"), 700, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() {
		try {
			ImageScaler.scaleImage(new File("D:/个人资料/照片/me.jpg"), new File("d:/new_1.jpg"), 500, 500, true, 0.9f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Just for testing...
	 */
	public static void main(String[] args) {
		test2();
	}

}
