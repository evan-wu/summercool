package org.summercool.web.servlet.http;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

	/**
	 * 
	 * @param response
	 */
	public HttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

}
