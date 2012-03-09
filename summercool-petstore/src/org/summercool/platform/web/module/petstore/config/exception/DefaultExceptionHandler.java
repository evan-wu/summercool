package org.summercool.platform.web.module.petstore.config.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.summercool.util.StackTraceUtil;
import org.summercool.web.servlet.pipeline.ExceptionPipeline;

public class DefaultExceptionHandler implements ExceptionPipeline {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private int order;

	public void handleExceptionInternal(HttpServletRequest request, HttpServletResponse response,
			ModelAndView mv, Throwable throwable) throws Exception {
		// 打印错误信息
		String stackTrace = StackTraceUtil.getStackTrace(throwable);
		// 记录错误信息
		logger.error(stackTrace);

		if (mv != null) {
			mv.setViewName("redirect:/index.htm");			
		} else {
			throwable.printStackTrace();
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

}
