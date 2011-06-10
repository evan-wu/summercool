/**
 * 
 */
package com.bdconsulting.toolkit.image.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * Datetime   ： 2011-5-20 下午12:27:02<br>
 * Title      :  ImageUtils.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class ImageUtils {

	/**
	 * 拷备至新InputStream
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static InputStream createMemoryStream(InputStream input) throws IOException {
		if (!(input instanceof ByteArrayInputStream)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtils.copy(input, outputStream);
			//
			input = new ByteArrayInputStream(outputStream.toByteArray());
			outputStream = null;
		}
		return input;
	}

	/**
	 * 判断是否是JPG/JPEG图片
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static boolean isJPEG(InputStream source) throws IOException {
		InputStream iis = source;

		if (!source.markSupported()) {
			throw new IllegalArgumentException("Input stream must support mark");
		}

		iis.mark(30);
		// If the first two bytes are a JPEG SOI marker, it's probably
		// a JPEG file. If they aren't, it definitely isn't a JPEG file.
		try {
			int byte1 = iis.read();
			int byte2 = iis.read();
			if ((byte1 == 0xFF) && (byte2 == 0xD8)) {

				return true;
			}
		} finally {
			iis.reset();
		}

		return false;
	}

	/**
	 * 判断是否是GIF图片
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static boolean isGIF(InputStream in) throws IOException {
		if (!in.markSupported()) {
			throw new IllegalArgumentException("Input stream must support mark");
		}

		byte[] b = new byte[6];

		try {
			in.mark(30);
			in.read(b);
		} finally {
			in.reset();
		}

		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	/**
	 * 判断是否是PNG图片
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static boolean isPNG(InputStream in) throws IOException {
		if (!in.markSupported()) {
			throw new IllegalArgumentException("Input stream must support mark");
		}

		byte[] b = new byte[8];
		try {
			in.mark(30);
			in.read(b);
		} finally {
			in.reset();
		}

		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13
				&& b[5] == (byte) 10 && b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	public static void closeQuietly(ImageInputStream inStream) {
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static void closeQuietly(ImageOutputStream outStream) {
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException ignore) {
			}
		}
	}

	public static void closeQuietly(ImageReader reader) {
		if (reader != null) {
			try {
				reader.dispose();
			} catch (Exception igonre) {
			}
		}
	}
}
