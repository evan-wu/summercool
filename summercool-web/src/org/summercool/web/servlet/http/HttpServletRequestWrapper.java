package org.summercool.web.servlet.http;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

	/**
	 * 
	 * @param request
	 */
	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

}
