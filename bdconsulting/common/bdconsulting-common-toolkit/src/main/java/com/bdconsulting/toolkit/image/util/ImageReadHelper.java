/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import com.bdconsulting.toolkit.image.ImageFormat;
import com.bdconsulting.toolkit.image.ImageWrapper;

/**
 * Datetime   ： 2011-5-20 下午12:25:50<br>
 * Title      :  ImageReadHelper.java<br>
 * Description:  读取Image图片工具类 <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageReadHelper {

	/**
	 * 读取图片文件
	 * 
	 * @param input
	 * @param readMetadata
	 * @return
	 */
	public static ImageWrapper read(InputStream input, boolean readMetadata) {
		try {
			input = ImageUtils.createMemoryStream(input);
			if (ImageUtils.isJPEG(input)) {
				return readJPEG(input, readMetadata);
			} else if (ImageUtils.isPNG(input)) {
				return readPNG(input, readMetadata);
			} else if (ImageUtils.isGIF(input)) {
				return readGIF(input, readMetadata);
			} else {
				throw new RuntimeException("Only support jpg/png/gif image format");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 读取JPEG图片
	 * 
	 * @param input
	 * @param readMetadata
	 * @return
	 */
	public static ImageWrapper readJPEG(InputStream input, boolean readMetadata) {
		ImageInputStream stream = null;
		ImageReader reader = null;
		try {
			stream = ImageIO.createImageInputStream(input);
			reader = ImageIO.getImageReaders(stream).next();
			reader.setInput(stream);
			ImageReadParam param = reader.getDefaultReadParam();
			//
			ImageTypeSpecifier typeToUse = null;
			for (Iterator<ImageTypeSpecifier> i = reader.getImageTypes(0); i.hasNext();) {
				ImageTypeSpecifier type = (ImageTypeSpecifier) i.next();
				if (type.getColorModel().getColorSpace().isCS_sRGB()) {
					typeToUse = type;
				}
			}
			// fix jre5 bugs
			if (typeToUse != null) {
				param.setDestinationType(typeToUse);
			}
			//
			ImageWrapper imageWrapper = new ImageWrapper(reader.read(0, param));
			imageWrapper.setImageFormat(ImageFormat.JPEG);
			return imageWrapper;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ImageUtils.closeQuietly(reader);
			ImageUtils.closeQuietly(stream);
		}
	}

	/**
	 * 读取PNG图片
	 * 
	 * @param input
	 * @param readMetadata
	 * @return
	 */
	public static ImageWrapper readPNG(InputStream input, boolean readMetadata) {
		ImageInputStream stream = null;
		ImageReader reader = null;
		try {
			stream = ImageIO.createImageInputStream(input);
			reader = ImageIO.getImageReaders(stream).next();
			reader.setInput(stream);
			ImageReadParam param = reader.getDefaultReadParam();
			//
			ImageTypeSpecifier typeToUse = null;
			for (Iterator<ImageTypeSpecifier> i = reader.getImageTypes(0); i.hasNext();) {
				ImageTypeSpecifier type = (ImageTypeSpecifier) i.next();
				if (type.getColorModel().getColorSpace().isCS_sRGB()) {
					typeToUse = type;
				}
			}
			// fix jre5 bugs
			if (typeToUse != null) {
				param.setDestinationType(typeToUse);
			}
			//
			ImageWrapper imageWrapper = new ImageWrapper(reader.read(0, param));
			imageWrapper.setImageFormat(ImageFormat.PNG);
			return imageWrapper;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ImageUtils.closeQuietly(reader);
			ImageUtils.closeQuietly(stream);
		}
	}

	/**
	 * 读取GIF图片
	 * 
	 * @param input
	 * @param readMetadata
	 * @return
	 */
	public static ImageWrapper readGIF(InputStream input, boolean readMetadata) {
		ImageInputStream stream = null;
		ImageReader reader = null;
		try {
			stream = ImageIO.createImageInputStream(input);
			reader = ImageIO.getImageReaders(stream).next();
			reader.setInput(stream);
			ImageReadParam param = reader.getDefaultReadParam();
			//
			ImageTypeSpecifier typeToUse = null;
			for (Iterator<ImageTypeSpecifier> i = reader.getImageTypes(0); i.hasNext();) {
				ImageTypeSpecifier type = (ImageTypeSpecifier) i.next();
				if (type.getColorModel().getColorSpace().isCS_sRGB()) {
					typeToUse = type;
				}
			}
			// fix jre5 bugs
			if (typeToUse != null) {
				param.setDestinationType(typeToUse);
			}
			int numOfImages = reader.getNumImages(true);
			BufferedImage[] images = new BufferedImage[numOfImages];
			IIOMetadata[] metadatas = new IIOMetadata[numOfImages];
			for (int i = 0; i < numOfImages; i++) {
				images[i] = reader.read(i);
				if (readMetadata) {
					metadatas[i] = reader.getImageMetadata(i);
				}
			}
			ImageWrapper imgageWrapper = new ImageWrapper(images);
			imgageWrapper.setImageFormat(ImageFormat.GIF);
			if (readMetadata) {
				imgageWrapper.setMetadatas(metadatas);
			}
			//
			return imgageWrapper;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			ImageUtils.closeQuietly(reader);
			ImageUtils.closeQuietly(stream);
		}
	}

}
