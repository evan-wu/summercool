package org.summercool.web.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferedResponse extends HttpServletResponseWrapper {

	private BufferedOutputStream outputStream;
	private PrintWriter writer;

	/** Creates a new instance of BufferedResponse */

	public BufferedResponse(HttpServletResponse internalResponse) {
		super(internalResponse);
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (outputStream == null) {
			outputStream = new BufferedOutputStream();
		}
		return outputStream;
	}

	public PrintWriter getWriter() throws IOException {
		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
		}
		return writer;
	}

	public byte[] getBufferAsByteArray() throws IOException {
		byte[] byteArray = new byte[0];
		if (writer != null) {
			writer.flush();
		}
		if (outputStream != null) {
			outputStream.flush();
			byteArray = outputStream.getContentsAsByteArray();
		}
		return byteArray;
	}

}
