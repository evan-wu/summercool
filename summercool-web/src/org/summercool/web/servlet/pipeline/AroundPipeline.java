package org.summercool.web.servlet.pipeline;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.PriorityOrdered;
import org.summercool.web.servlet.AroundPipelineChain;

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
