/*
 * ImageScaler.java - Copyright (c) 2004 Torsten - dode@luniks.net
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.bdconsulting.toolkit.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.bdconsulting.toolkit.lang.IOUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Class to demonstrate a way to scale an image without the
 * requirement of a graphical environment and without enabling
 * the headless support available since Java 1.4
 * @author Torsten
 * @author www.luniks.net
 */
public class ImageScaler {

	/**
	 * Reads an image of format GIF, JPEG or PNG, scales and saves it
	 * as a JPEG image where no graphical environment is available
	 * without enabling headless support.
	 * Works thanks to the class ImageGenerator from j3d.org
	 * @param infile the image file to be used as input
	 * @param outfile write the scaled image to this file
	 * @param width the width to scale to
	 * @param height the height to scale to
	 * @param keepAspect if the aspect should be kept or not
	 * @param quality the compression quality
	 * @see org.j3d.util.ImageGenerator
	 */
	public static void scaleImage(File infile, File outfile, int width, int height, boolean keepAspect, float quality)
			throws Exception {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(infile);
			byte[] byteArray = IOUtil.transformInputstream(inputStream);
			scaleImage(byteArray, outfile, width, height, keepAspect, quality);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param width
	 * @param height
	 * @param keepAspect
	 * @param quality
	 * @throws Exception
	 */
	public static void scaleImage(byte[] byteArray, File outfile, int width, int height, boolean keepAspect,
			float quality) throws Exception {
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = scaleImage2OutputStream(byteArray, width, height, keepAspect, quality);
			FileUtils.writeByteArrayToFile(outfile, outputStream.toByteArray());
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param infile
	 * @param width
	 * @param height
	 * @param keepAspect
	 * @param quality
	 * @return
	 * @throws Exception
	 */
	public static ByteArrayOutputStream scaleImage2OutputStream(byte[] byteArray, int width, int height,
			boolean keepAspect, float quality) throws Exception {
		InputStream infile = new ByteArrayInputStream(byteArray);
		BufferedImage original = ImageIO.read(infile);
		if (original == null) {
			throw new Exception("Unsupported file format!");
		}

		ByteArrayOutputStream byteOut = null;
		BufferedOutputStream bufferedOut = null;
		try {
			byteOut = new ByteArrayOutputStream();
			bufferedOut = new BufferedOutputStream(byteOut);
			//
			int originalWidth = original.getWidth();
			int originalHeight = original.getHeight();
			float factorX = (float) originalWidth / width;
			float factorY = (float) originalHeight / height;
			if (keepAspect) {
				factorX = Math.max(factorX, factorY);
				factorY = factorX;
			}
			// The scaling will be nice smooth with this filter
			AreaAveragingScaleFilter scaleFilter = new AreaAveragingScaleFilter(
					Math.round(originalWidth / factorX),
					Math.round(originalHeight / factorY));
			ImageProducer producer = new FilteredImageSource(original.getSource(), scaleFilter);
			ImageGenerator generator = new ImageGenerator();
			producer.startProduction(generator);
			BufferedImage scaled = generator.getImage();
			//
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bufferedOut);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(scaled);
			jep.setQuality(quality, true);
			encoder.encode(scaled, jep);
			//
			return byteOut;
		} finally {
			try {
				if (bufferedOut != null) {
					bufferedOut.close();
				}
			} catch (Exception e) {
			}
			try {
				if (byteOut != null) {
					byteOut.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Converts a java.awt.Image to a java.awt.image.BufferedImage.
	 * Requires a graphics context. Not used in this class.
	 * @param image the Image to convert to a BufferedImage
	 * @return the BufferedImage the Image has been converted to
	 */
	public static BufferedImage convert(Image image) {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bi;
	}
}
