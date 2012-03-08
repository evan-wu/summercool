package org.summercool.web.servlet.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * @Title: StringBufferedResponse.java
 * @Package org.summercool.web.servlet.http
 * @Description: 缓存类
 * @author 简道
 * @date 2011-8-8 下午8:45:44
 * @version V1.0
 */
public class StringBufferedResponse extends HttpServletResponseWrapper {

	private StringWriter sout;
	private PrintWriter pout;

	public StringBufferedResponse(HttpServletResponse response) {
		super(response);
		sout = new StringWriter();
		pout = new PrintWriter(sout);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return pout;
	}

	public String getResponseContent() {
		return sout.toString();
	}

}
