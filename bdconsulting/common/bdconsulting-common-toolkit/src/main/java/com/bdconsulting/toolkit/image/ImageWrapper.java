/**
 * 
 */
package com.bdconsulting.toolkit.image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 * Datetime   ： 2011-5-20 下午12:48:58<br>
 * Title      :  ImageWrapper.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageWrapper extends MetadataRenderedImage {

	public static final int DEFAULT_QUALITY = 93;

	protected RenderedImage[] images;

	public ImageWrapper(BufferedImage bi) {
		this(bi, DEFAULT_QUALITY);
	}

	public ImageWrapper(BufferedImage bi, int quality) {
		this.quality = quality;
		this.images = new RenderedImage[1];
		this.images[0] = bi;
	}

	public ImageWrapper(BufferedImage[] imgs) {
		setImages(imgs);
	}

	public BufferedImage getAsBufferedImage(int index) {
		return (BufferedImage) images[index];
	}

	public BufferedImage getAsBufferedImage() {
		return getAsBufferedImage(0);
	}

	public BufferedImage[] getAsBufferedImages() {
		BufferedImage[] imgs = new BufferedImage[images.length];

		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = getAsBufferedImage(i);
		}

		return imgs;
	}

	public void setImages(BufferedImage[] imgs) {
		images = new RenderedImage[imgs.length];
		for (int i = 0; i < imgs.length; i++) {
			images[i] = imgs[i];
		}
	}

	public void setImage(int index, BufferedImage bi) {
		this.images[index] = bi;
	}

	public void setImage(BufferedImage bi) {
		setImage(0, bi);
	}

	public int getNumOfImages() {
		return images.length;
	}
}
