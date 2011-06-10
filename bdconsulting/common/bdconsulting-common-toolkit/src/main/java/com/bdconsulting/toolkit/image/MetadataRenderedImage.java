/**
 * 
 */
package com.bdconsulting.toolkit.image;

import javax.imageio.metadata.IIOMetadata;

/**
 * Datetime   ： 2011-5-20 下午12:47:57<br>
 * Title      :  MetadataRenderedImage.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public abstract class MetadataRenderedImage {
	// Used by JPEG
	protected int quality;
	protected int[] horizontalSamplingFactors = new int[] { 1, 1, 1, 1 };
	protected int[] verticalSamplingFactors = new int[] { 1, 1, 1, 1 };

	// Used by all image types
	protected IIOMetadata[] metadatas;

	protected ImageFormat format;

	/**
	 * 
	 */
	public MetadataRenderedImage() {
		super();
	}

	public void setImageFormat(ImageFormat format) {
		this.format = format;
	}

	public ImageFormat getImageFormat() {
		return this.format;
	}

	public IIOMetadata getMetadata(int index) {
		return metadatas[index];
	}

	public IIOMetadata getMetadata() {
		return metadatas[0];
	}

	public IIOMetadata[] getMetadatas() {
		return metadatas;
	}

	public void setMetadata(int index, IIOMetadata metadata) {
		metadatas[index] = metadata;
	}

	public void setMetadatas(IIOMetadata[] metadatas) {
		this.metadatas = metadatas;
	}

	public void setMetadata(IIOMetadata metadata) {
		if (metadatas == null) {
			metadatas = new IIOMetadata[1];
		}

		metadatas[0] = metadata;
	}

	public void setHorizontalSamplingFactor(int component, int subsample) {
		horizontalSamplingFactors[component] = subsample;
	}

	public int getHorizontalSamplingFactor(int component) {
		return horizontalSamplingFactors[component];
	}

	public int getHorizontalSubsampling(int component) {
		int subsampling = horizontalSamplingFactors[component] == 1 ? 2 : 1;

		return subsampling;
	}

	public void setVerticalSamplingFactor(int component, int subsample) {
		verticalSamplingFactors[component] = subsample;
	}

	public int getVerticalSamplingFactor(int component) {
		return verticalSamplingFactors[component];
	}

	public int getVerticalSubsampling(int component) {
		int subsampling = verticalSamplingFactors[component] == 1 ? 2 : 1;

		return subsampling;
	}

	public int getQuality() {
		return quality;
	}
}
