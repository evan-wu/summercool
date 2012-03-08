package org.summercool.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AroundPipelineChain {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-29
	 * @param request
	 * @param response
	 * @param aroundPipelineChain
	 * @throws Exception
	 */
	public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,
			AroundPipelineChain aroundPipelineChain) throws Exception;
}
