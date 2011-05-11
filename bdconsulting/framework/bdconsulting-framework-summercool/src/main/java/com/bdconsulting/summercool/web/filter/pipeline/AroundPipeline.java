package com.bdconsulting.summercool.web.filter.pipeline;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.PriorityOrdered;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 *
 */
public interface AroundPipeline extends PriorityOrdered {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * @param request
	 * @param response
	 * @param aroundPipelineChain
	 * @throws Exception
	 */
	public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,
			AroundPipelineChain aroundPipelineChain) throws Exception;

}
