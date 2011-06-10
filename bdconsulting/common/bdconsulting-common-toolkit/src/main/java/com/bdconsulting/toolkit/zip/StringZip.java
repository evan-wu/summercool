/**
 * 
 */
package com.bdconsulting.toolkit.zip;

import java.io.IOException;
import java.util.zip.DataFormatException;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Datetime   ： 2011-5-5 下午08:02:17<br>
 * Title      :  StringZip.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class StringZip {

	/**
	 * 压缩字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	public static String zipString(String str) throws Exception {
		if (str == null) {
			return str;
		}
		byte[] bytes = CompressionTools.compressString(str);
		return new BASE64Encoder().encodeBuffer(bytes);
	}

	/**
	 * 解压字符串
	 * 
	 * @param str
	 * @return
	 * @throws IOException 
	 * @throws DataFormatException 
	 */
	public static String unzipString(String str) throws IOException, DataFormatException {
		if (str == null) {
			return str;
		}
		byte[] bytes = new BASE64Decoder().decodeBuffer(str);
		return CompressionTools.decompressString(bytes);
	}

}
