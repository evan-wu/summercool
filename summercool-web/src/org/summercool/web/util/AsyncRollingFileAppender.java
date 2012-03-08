package org.summercool.web.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.summercool.util.StackTraceUtil;

import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * @Title: AsyncAppender.java
 * @Package com.gexin.platform.biz.common.logback
 * @Description: 异步RollingFileAppender实现
 * @author Yanjh
 * @date 2011-8-17 下午6:05:06
 * @version V1.0
 */
public class AsyncRollingFileAppender<E> extends RollingFileAppender<E> {

	private BlockingQueue<E> queue = new LinkedBlockingQueue<E>();

	@Override
	public void start() {
		super.start();
		// 创建线程记录日志
		Thread thread = new Thread(new Dispatcher());
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	protected void subAppend(E event) {
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			superSubAppend(event);
		}
	}

	private void superSubAppend(E event) {
		super.subAppend(event);
	}

	private class Dispatcher implements Runnable {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		public void run() {
			while (started) {
				try {
					E event = queue.take();
					superSubAppend(event);
				} catch (InterruptedException e) {
					logger.error(StackTraceUtil.getStackTrace(e));
				}
			}
		}
	}
}
