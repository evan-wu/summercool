package org.summercool.platform.web.module.petstore.config.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;
import org.summercool.platform.utils.NumberStatisticUtil;
import org.summercool.web.servlet.AroundPipelineChain;
import org.summercool.web.servlet.pipeline.AroundPipeline;

public class RequestMonitor implements AroundPipeline {

	private static final String N_CHAR = "/";
	
	private final Logger logger = LoggerFactory.getLogger(RequestMonitor.class);

	private int order;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private NumberStatisticUtil numberStatisticUtil = new NumberStatisticUtil();

	public RequestMonitor() {
		numberStatisticUtil.setInterval(N_CHAR, 1000L);
		urlPathHelper.setUrlDecode(false);
	}

	public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,
			AroundPipelineChain aroundPipelineChain) throws Exception {
		//
		if (!logger.isDebugEnabled()) {
			aroundPipelineChain.handleAroundInternal(request, response, aroundPipelineChain);
			return;
		}

		//
		long beginTime = System.currentTimeMillis();
		//
		numberStatisticUtil.incrementAndGet(N_CHAR);
		long threadCount = numberStatisticUtil.getValue(N_CHAR);
		//
		try {
			aroundPipelineChain.handleAroundInternal(request, response, aroundPipelineChain);
		} finally {
			if (logger.isInfoEnabled()) {
				String url = urlPathHelper.getLookupPathForRequest(request);
				long endTime = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String message = "当前Dispatcher[" + request.getRemoteAddr() + "-->" + url + "]"
						+ "当执行的时间为:[" + sdf.format(new Date(beginTime)) + "], " 
						+ "当前时间内(秒)执行线程数为:[" + threadCount + "], "
						+ "请求执行的时间为:[" + (endTime - beginTime) + "毫秒]";
				logger.info(message);
			}
		}
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
