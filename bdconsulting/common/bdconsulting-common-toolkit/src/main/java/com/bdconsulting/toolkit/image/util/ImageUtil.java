/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.io.FileUtils;

import com.bdconsulting.toolkit.image.ImageScaler;

/**
 * Datetime   ： 2011-5-6 上午10:36:57<br>
 * Title      :  ImageUtil.java<br>
 * Description:  只支持jpg和png并生成jpg文件，优点是速度快 <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageUtil {

	public static final String[] JPG_OR_JPEG_TYPES = { "JPG", "jpg", "JPEG", "jpeg" };

	public static final String[] GIF_TYPES = { "GIF", "gif" };

	public static final String[] PNG_TYPES = { "PNG", "png" };

	/**
	 * 判断是否是JPG或JPEG类型的图片<br>
	 * 输入流没有关闭，请使用者自行关闭
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-5-11
	 * @param in
	 * @return
	 */
	public static boolean isJPGOrJPEG(InputStream in) {
		return isImageType(in, JPG_OR_JPEG_TYPES);
	}

	/**
	 * 判断是否是GIF类型的图片<br>
	 * 输入流没有关闭，请使用者自行关闭
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-5-11
	 * @param in
	 * @return
	 */
	public static boolean isGIF(InputStream in) {
		return isImageType(in, GIF_TYPES);
	}

	/**
	 * 判断是否是PNG类型的图片<br>
	 * 输入流没有关闭，请使用者自行关闭
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-5-11
	 * @param in
	 * @return
	 */
	public static boolean isPNG(InputStream in) {
		return isImageType(in, PNG_TYPES);
	}

	private static boolean isImageType(InputStream in, String[] types) {
		MemoryCacheImageInputStream mcis = null;
		try {
			mcis = new MemoryCacheImageInputStream(in);
			Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
			//
			while (itr.hasNext()) {
				ImageReader reader = itr.next();
				String formatName = reader.getFormatName();
				if (formatName != null) {
					for (String type : types) {
						if (type.equals(formatName)) {
							return true;
						}
					}
				}
			}
		} catch (IOException e) {
			if (mcis != null) {
				try {
					mcis.close();
				} catch (IOException ioe) {
				}
			}
		}
		return false;
	}

	/**
	 * 压缩并转换图片为JPG格式的InputStream
	 * 
	 * @param inputStream
	 * @param size 最大长或宽
	 * @param keepAspect 是否等比压缩
	 * @param quality 质量
	 * @param zoomIn 是否进行放大
	 * @return
	 */
	public static ByteArrayInputStream scaleImage2JPG2InputStream(byte[] byteArray, int size,
			boolean keepAspect, float quality, boolean zoomIn) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			out = scaleImage2JPG2OutputStream(byteArray, size, keepAspect, quality, zoomIn);
			return new ByteArrayInputStream(out.toByteArray());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 压缩并转换图片为JPG格式的InputStream
	 * 
	 * @param inputStream
	 * @param size 最大长或宽
	 * @param keepAspect 是否等比压缩
	 * @param quality 质量
	 * @param zoomIn 是否进行放大
	 * @return
	 */
	public static ByteArrayOutputStream scaleImage2JPG2OutputStream(byte[] byteArray, int size,
			boolean keepAspect, float quality, boolean zoomIn) throws Exception {
		InputStream inputStream = new ByteArrayInputStream(byteArray);
		BufferedImage original = ImageIO.read(inputStream);
		if (original == null) {
			throw new Exception("Unsupported file format!");
		}
		ByteArrayOutputStream out;
		int originalWidth = original.getWidth();
		int originalHeight = original.getHeight();
		//
		if (zoomIn) {
			out = ImageScaler.scaleImage2OutputStream(byteArray, size, size, keepAspect, quality);
		} else {
			if (originalWidth > size || originalHeight > size) {
				out = ImageScaler.scaleImage2OutputStream(byteArray, size, size, keepAspect, quality);
			} else {
				out = ImageScaler.scaleImage2OutputStream(byteArray, originalWidth, originalHeight, keepAspect, quality);
			}
		}
		return out;
	}

	/**
	 * 压缩图片为JPG格式
	 * 
	 * @param inputStream 输入流
	 * @param size 图片大小
	 * @param zoomIn 是否支持放大
	 */
	public static void compressImage2JPG(byte[] byteArray, File destFile, int size, boolean zoomIn)
			throws Exception {
		InputStream  inputStream = new ByteArrayInputStream(byteArray);
		if (!(isJPGOrJPEG(inputStream) || isPNG(inputStream))) {
			throw new Exception("只支持JPG或PNG格式图片!");
		}
		//
		ByteArrayOutputStream out = null;
		try {
			out = scaleImage2JPG2OutputStream(byteArray, size, true, 0.9f, zoomIn);
			FileUtils.writeByteArrayToFile(destFile, out.toByteArray());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
