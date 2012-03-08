package org.summercool.web.context;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;

/**
 * 
 * @author:Dragonsoar
 * @date:2010-11-18
 * 
 */
public class ResponseContextHolder {

	private static final ThreadLocal<HttpServletResponse> responseHolder = new NamedThreadLocal<HttpServletResponse>(
			"Response");

	public static void resetResponse() {
		responseHolder.set(null);
	}

	public static void setResponse(HttpServletResponse response) {
		responseHolder.set(response);
	}

	public static HttpServletResponse getResponse() {
		return responseHolder.get();
	}

}
