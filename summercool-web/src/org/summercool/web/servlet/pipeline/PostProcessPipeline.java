package org.summercool.web.servlet.pipeline;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.PriorityOrdered;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public interface PostProcessPipeline extends PriorityOrdered {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * @param request
	 * @param response
	 * @param modelAndView
	 * @return
	 * @throws Exception
	 */
	public boolean isPermitted(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView)
			throws Exception;

	public ModelAndView handleProcessInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
