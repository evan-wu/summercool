/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bdconsulting.toolkit.image.ImageFormat;
import com.bdconsulting.toolkit.image.ImageWrapper;

/**
 * Datetime   ： 2011-5-20 下午04:43:54<br>
 * Title      :  ImageUtil3.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageUtil3 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

//		test();
		test1();
//		test2();
	}

	public static void test() throws IOException {
		String gifSrc = "D:/demo/demo.gif";
		String gifTarget = "D:/demo/demo_target.gif";
		FileInputStream fileInputStream = new FileInputStream(new File(gifSrc));
		FileOutputStream fileOutputStream = new FileOutputStream(new File(gifTarget));

		ImageWrapper imageWrapper = ImageReadHelper.readGIF(fileInputStream, true);
		ImageScaleHelper.scale(288, 355, imageWrapper);
		ImageWriteHelper.write(imageWrapper, fileOutputStream, ImageFormat.GIF);

		fileInputStream.close();
		fileOutputStream.close();
	}

	public static void test1() throws IOException {
		String gifSrc = "D:/demo/demo1.gif";
		String gifTarget = "D:/demo/demo1_target.gif";
		FileInputStream fileInputStream = new FileInputStream(new File(gifSrc));
		FileOutputStream fileOutputStream = new FileOutputStream(new File(gifTarget));

		ImageWrapper imageWrapper = ImageReadHelper.readGIF(fileInputStream, true);
		ImageScaleHelper.scale(250, 195, imageWrapper);
		ImageWriteHelper.write(imageWrapper, fileOutputStream, ImageFormat.GIF);

		fileInputStream.close();
		fileOutputStream.close();
	}
	
	public static void test2() throws IOException {
		String gifSrc = "D:/demo/qq.gif";
		String gifTarget = "D:/demo/qq_target.gif";
		FileInputStream fileInputStream = new FileInputStream(new File(gifSrc));
		FileOutputStream fileOutputStream = new FileOutputStream(new File(gifTarget));

		ImageWrapper imageWrapper = ImageReadHelper.readGIF(fileInputStream, true);
		ImageScaleHelper.scale(128, 128, imageWrapper);
		ImageWriteHelper.write(imageWrapper, fileOutputStream, ImageFormat.GIF);

		fileInputStream.close();
		fileOutputStream.close();
	}

}
