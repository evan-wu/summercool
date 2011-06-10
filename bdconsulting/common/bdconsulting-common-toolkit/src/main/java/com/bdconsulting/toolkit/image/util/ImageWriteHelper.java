/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.w3c.dom.Node;

import com.bdconsulting.toolkit.image.ImageFormat;
import com.bdconsulting.toolkit.image.ImageWrapper;

/**
 * Datetime   ： 2011-5-20 下午02:07:36<br>
 * Title      :  ImageWriteHelper.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageWriteHelper {

	public static final int DEFAULT_MIN_QUALITY = 90;

	public static final String GIF_NATIVE_METADATA_FORMATNAME = "javax_imageio_gif_image_1.0";

	/**
	 * 输出图片
	 * 
	 * @param imgWrapper
	 * @param os
	 * @param outputFormat
	 */
	public static void write(ImageWrapper imgWrapper, OutputStream os, ImageFormat outputFormat) {
		try {
			if (outputFormat == ImageFormat.JPEG) {
				writeJPEG(imgWrapper, os, outputFormat);
			} else if (outputFormat == ImageFormat.PNG) {
				ImageIO.write(imgWrapper.getAsBufferedImage(), ImageFormat.getDesc(outputFormat), os);
			} else if (outputFormat == ImageFormat.GIF) {
				writeGIF(imgWrapper, os, outputFormat);
			} else {
				throw new IllegalArgumentException("Unsupported output format");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 写JPEG图片
	 * 
	 * @param imgWrapper
	 * @param os
	 * @param outputFormat
	 */
	public static void writeJPEG(ImageWrapper imgWrapper, OutputStream os, ImageFormat outputFormat) {
		MemoryCacheImageOutputStream imageOutputStream = null;
		ImageWriter writer = null;
		try {
			BufferedImage bufferedImage = imgWrapper.getAsBufferedImage();
			imageOutputStream = new MemoryCacheImageOutputStream(os);
			Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(ImageFormat.getDesc(outputFormat));
			writer = it.next();
			writer.setOutput(imageOutputStream);
			//
			ImageWriteParam defaultWriteParam = writer.getDefaultWriteParam();
			defaultWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			int quality = imgWrapper.getQuality();
			if (quality < DEFAULT_MIN_QUALITY) {
				quality = DEFAULT_MIN_QUALITY;
			}
			defaultWriteParam.setCompressionQuality(quality / 100.0f);
			//
			writer.write(null, new IIOImage(bufferedImage, null, null), defaultWriteParam);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.dispose();
			}
			ImageUtils.closeQuietly(imageOutputStream);
		}
	}

	/**
	 * 写GIF图片
	 * 
	 * @param imgWrapper
	 * @param os
	 * @param outputFormat
	 */
	public static void writeGIF(ImageWrapper imgWrapper, OutputStream os, ImageFormat outputFormat) {
		ImageOutputStream imageOutputStream = null;
		ImageWriter writer = null;
		try {
			BufferedImage[] images = imgWrapper.getAsBufferedImages();
			IIOMetadata[] metadatas = imgWrapper.getMetadatas();
			Node[] properties = new Node[metadatas.length];
			for (int i = 0; i < images.length; i++) {
				properties[i] = metadatas[i].getAsTree(GIF_NATIVE_METADATA_FORMATNAME);
			}
			//
			imageOutputStream = ImageIO.createImageOutputStream(os);
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(ImageFormat.getDesc(outputFormat));
			while (writers.hasNext()) {
				writer = writers.next();
				if (writer.canWriteSequence()) {
					break;
				}
			}
			//
			if (writer == null || !writer.canWriteSequence()) {
				throw new IllegalStateException("No GIF writer matched");
			}
			writer.setOutput(imageOutputStream);
			ImageWriteParam param = writer.getDefaultWriteParam();
			IIOMetadata streamMeta = writer.getDefaultStreamMetadata(param);
			writer.prepareWriteSequence(streamMeta);
			//
			for (int i = 0; i < images.length; i++) {
				ColorModel colorModel = images[i].getColorModel();
				SampleModel sampleModel = images[i].getSampleModel();
				ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
				IIOMetadata meta = writer.getDefaultImageMetadata(imageType, param);
				meta.mergeTree(GIF_NATIVE_METADATA_FORMATNAME, properties[i]);
				//
				IIOImage img = new IIOImage(images[i], null, meta);
				writer.writeToSequence(img, param);
			}
			writer.endWriteSequence();
			imageOutputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.dispose();
			}
			ImageUtils.closeQuietly(imageOutputStream);
		}
	}

}
