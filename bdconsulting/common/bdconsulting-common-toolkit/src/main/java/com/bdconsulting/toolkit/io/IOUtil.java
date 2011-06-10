package com.bdconsulting.toolkit.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IOUtil {

	/**
	 * inputStream转换byte数组
	 * 
	 * @param input
	 * @return 流是从当前位置开始读取的
	 * @throws Exception
	 */
	public static byte[] transformInputstream(InputStream input) throws Exception {
		try {
			byte[] byt= null;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        int b=0;       
	        b = input.read();
	        while( b != -1)
	        {
	            baos.write(b);
	            b = input.read();            
	        }
	        byt = baos.toByteArray();
	        return byt;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
