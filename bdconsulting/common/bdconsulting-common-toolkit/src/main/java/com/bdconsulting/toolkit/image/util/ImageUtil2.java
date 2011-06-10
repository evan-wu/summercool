/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * Datetime   ： 2011-5-19 下午09:19:57<br>
 * Title      :  ImageUtil2.java<br>
 * Description:  支持JPG、PNG和GIF文件格式，而且最清晰 <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageUtil2 {

	public static final String GIFnativeMetadataFormatName = "javax_imageio_gif_image_1.0";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		readImage(new File("D:/demo/demo1.gif"), new File("D:/demo/demo1_new.gif"));
	}

	/**
	 * Workaround for sRGB color space bug on JRE 1.5
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4705399
	 *  
	 * @param source (inputstream or File)
	 * @return fixed buffered image
	 * @throws IOException
	 */
	public static void readImage(Object source, File destFile) throws IOException {
		//
		int maxWidth = 240;
		int maxHeight = 240;
		// 读取gif文件
		ImageInputStream stream = ImageIO.createImageInputStream(source);
		ImageReader reader = ImageIO.getImageReaders(stream).next();
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
		// 生成gif图像
		int numOfImages = reader.getNumImages(true);
		BufferedImage[] images = new BufferedImage[numOfImages];
		IIOMetadata[] metadatas = new IIOMetadata[numOfImages];
		Node[] properties = new Node[metadatas.length];
		for (int i = 0; i < numOfImages; i++) {
			//
			images[i] = reader.read(i, param);
			metadatas[i] = reader.getImageMetadata(i);
			properties[i] = metadatas[i].getAsTree(GIFnativeMetadataFormatName);
			BufferedImage bufferedImage = images[i];
			//
			int width = 0;
			int height = 0;
			//
			{
				int iWidth = bufferedImage.getWidth();
				int iHeight = bufferedImage.getHeight();
				double rate = (double) iWidth / (double) iHeight;

				if (iWidth <= maxWidth && iHeight <= maxHeight) {

					width = iWidth;
					height = iHeight;

				} else if (iWidth > maxWidth && iHeight <= maxHeight) {

					width = maxWidth;
					height = (int) Math.round(maxWidth / rate);

				} else if (iWidth <= maxWidth && iHeight > maxHeight) {

					height = maxHeight;
					width = (int) Math.round(rate * maxHeight);

				} else if (iWidth > maxWidth && iHeight > maxHeight) {

					if (iWidth >= iHeight) {

						width = maxWidth;
						height = (int) Math.round(maxWidth / rate);
						//
						if (height > maxHeight) {
							height = maxHeight;
							width = (int) Math.round(rate * maxHeight);
						}

					} else {

						height = maxHeight;
						width = (int) Math.round(rate * maxHeight);
						//
						if (width > maxWidth) {
							width = maxWidth;
							height = (int) Math.round(maxWidth / rate);
						}

					}

				}
			}
			//
			// ResampleOp resampleOp = new ResampleOp(width, height);
			// resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			// resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			// BufferedImage rescaledImage = resampleOp.filter(bufferedImage, null);
			// //
			// ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			// BufferedOutputStream bufferedOut = new BufferedOutputStream(byteOut);

			// jpg
			// MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(bufferedOut);
			// Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix("jpg");
			// ImageWriter writer = it.next();
			// writer.setOutput(new MemoryCacheImageOutputStream(bufferedOut));
			// ImageWriteParam defaultWriteParam = writer.getDefaultWriteParam();
			// defaultWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// defaultWriteParam.setCompressionQuality(0.9f);
			// writer.write(null, new IIOImage(rescaledImage, null, null), defaultWriteParam);
			// writer.dispose();
			// //
			// FileUtils.writeByteArrayToFile(destFile, byteOut.toByteArray());
			// //
			// imageOutputStream.close();
			// bufferedOut.close();

			// png
			// MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(bufferedOut);
			// Iterator<ImageWriter> it = ImageIO.getImageWritersBySuffix("png");
			// ImageWriter writer = it.next();
			// writer.setOutput(new MemoryCacheImageOutputStream(bufferedOut));
			// ImageWriteParam defaultWriteParam = writer.getDefaultWriteParam();
			// writer.write(null, new IIOImage(rescaledImage, null, null), defaultWriteParam);
			// writer.dispose();
			// //
			// FileUtils.writeByteArrayToFile(destFile, byteOut.toByteArray());
			// //
			// imageOutputStream.close();
			// bufferedOut.close();

			 //gif
			 ResampleOp resampleOp = new ResampleOp(width, height);
			 resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			 resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			 BufferedImage rescaledImage = resampleOp.filter(bufferedImage, null);
			 
			 images[i] = rescaledImage;
		}
		
		// 生成gif
		ImageOutputStream imageOut = null;
		ImageWriter writer = null;
		imageOut = ImageIO.createImageOutputStream(new FileOutputStream(destFile));
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("GIF");
		while (writers.hasNext()) {
			writer = writers.next();
			if (writer.canWriteSequence()) {
				break;
			}
		}

		if (writer == null || !writer.canWriteSequence()) {
			throw new IllegalStateException("No GIF writer matched");
		}

		writer.setOutput(imageOut);

		ImageWriteParam defaultWriteParam = writer.getDefaultWriteParam();
		IIOMetadata streamMeta = writer.getDefaultStreamMetadata(defaultWriteParam);

		writer.prepareWriteSequence(streamMeta);
		for (int i = 0; i < images.length; i++) {
			ImageTypeSpecifier imageType = new ImageTypeSpecifier(images[i].getColorModel(), images[i].getSampleModel());
			IIOMetadata meta = writer.getDefaultImageMetadata(imageType, defaultWriteParam);
			meta.mergeTree(GIFnativeMetadataFormatName, properties[i]);

			IIOImage img = new IIOImage(images[i], null, meta);
			writer.writeToSequence(img, defaultWriteParam);
		}
		writer.endWriteSequence();
		//
		imageOut.flush();
		
		
		// 关闭资源
		reader.dispose();
		stream.close();
	}
}
