package org.summercool.web.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * @Title: BufferOutputStream.java
 * @Package org.summercool.web.servlet.http
 * @Description: 缓存
 * @author 简道
 * @date 2011-8-8 下午8:15:52
 * @version V1.0
 */
public class BufferedOutputStream extends ServletOutputStream {

	protected ByteArrayOutputStream buffer;

	/** Creates a new instance of BufferOutputStream */

	public BufferedOutputStream() {
		buffer = new ByteArrayOutputStream();
	}

	public void write(int b) throws IOException {
		buffer.write(b);
	}

	public void write(byte b[]) throws IOException {
		buffer.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		buffer.write(b, off, len);
	}

	public void flush() throws IOException {
		buffer.flush();
	}

	public void close() throws IOException {
		buffer.close();
	}

	public byte[] getContentsAsByteArray() throws IOException {
		flush();
		return buffer.toByteArray();
	}

}
