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
public interface ExceptionPipeline extends PriorityOrdered {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * @param request
	 * @param response
	 * @param modelAndView
	 * @param throwable
	 * @throws Exception
	 */
	public void handleExceptionInternal(HttpServletRequest request, HttpServletResponse response,
			ModelAndView modelAndView, Throwable throwable) throws Exception;
}