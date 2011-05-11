package com.bdconsulting.summercool.web.filter.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferedResponse extends HttpServletResponseWrapper {

	private StringWriter sout;
	private PrintWriter pout;

	public BufferedResponse(HttpServletResponse response) {
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
