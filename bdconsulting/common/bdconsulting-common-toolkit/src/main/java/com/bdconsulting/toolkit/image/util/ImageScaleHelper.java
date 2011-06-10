/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

import com.bdconsulting.toolkit.image.ImageWrapper;
import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * Datetime   ： 2011-5-20 下午05:19:40<br>
 * Title      :  ImageScaleHelper.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageScaleHelper {

	/**
	 * 压缩图片
	 * 
	 * @param width
	 * @param height
	 * @param imageWrapper
	 */
	public static void scale(int width, int height, ImageWrapper imageWrapper) {

		int srcWidth = imageWrapper.getAsBufferedImage().getWidth();
		int srcHeight = imageWrapper.getAsBufferedImage().getHeight();

		if (imageWrapper.getNumOfImages() == 1) {

			BufferedImage bufferedImage = imageWrapper.getAsBufferedImage();

			ResampleOp resampleOp = new ResampleOp(width, height);
			resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			BufferedImage rescaledImage = resampleOp.filter(bufferedImage, null);

			imageWrapper.setImage(rescaledImage);

			return;
		}

		IIOMetadata[] metadatas = imageWrapper.getMetadatas();
		for (int i = 0; i < imageWrapper.getNumOfImages(); i++) {
			//
			int offX = 0;
			int offY = 0;
			if (metadatas != null) {
				NodeList nl = metadatas[i].getAsTree(ImageWriteHelper.GIF_NATIVE_METADATA_FORMATNAME).getChildNodes();
				for (int count = 0; count < nl.getLength(); count++) {
					IIOMetadataNode node = (IIOMetadataNode) nl.item(count);
					if (node.getNodeName().equals("ImageDescriptor")) {
						offX = (Integer.parseInt(node.getAttribute("imageLeftPosition")));
						offY = (Integer.parseInt(node.getAttribute("imageTopPosition")));

					}
				}
			}
			BufferedImage image = imageWrapper.getAsBufferedImage(i);
			int currentWidth = image.getWidth();
			int currentHeight = image.getHeight();


			BufferedImage bufferedImage = new BufferedImage(500, 389, BufferedImage.TYPE_INT_RGB);
			Graphics g = bufferedImage.createGraphics();

			// TODO 此处color如何设置待调查
			g.setColor(Color.green);
			g.fillRect(0, 0, 500, 389);
			g.drawImage(image, 0, 0, null);
			g.dispose();

			ResampleOp resampleOp = new ResampleOp(500, 389);
			resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
			resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
			BufferedImage rescaledImage = resampleOp.filter(bufferedImage, null);

			imageWrapper.setImage(i, rescaledImage);
		}
	}

}
