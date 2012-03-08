package org.summercool.util;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Title: ThreadExecutorHelper.java
 * @Package com.gexin.platform.biz.common.utils
 * @Description: 线程执行类
 * @author 简道
 * @date 2011-8-10 下午4:26:50
 * @version V1.0
 */
public abstract class ThreadExecutorHelper {

	protected final Log logger = LogFactory.getLog(getClass());

	private static ThreadPoolExecutor threadPoolExecutor;

	private final int[] errorCount = new int[] { 0 };

	static {
		//
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		int corePoolSize = Runtime.getRuntime().availableProcessors() / 2;
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
		int keepAliveTime = 30;
		//
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES,
				queue);
	}

	// 设置重试次数 -1:代表永远重试 0:代表不重试 >=1:代表重试的次数
	private int reTryCount = 0;

	private Map<String, Object> params;

	public ThreadExecutorHelper(int reTryCount) {
		this.reTryCount = reTryCount;
		if (!(reTryCount >= -1)) {
			throw new RuntimeException("ThreadExecutorHelper(int reTryCount) --> reTryCount 必须大于等于[-1]");
		}
	}

	public ThreadExecutorHelper(int reTryCount, Map<String, Object> params) {
		this.reTryCount = reTryCount;
		this.params = params;
		if (!(reTryCount >= -1)) {
			throw new RuntimeException("ThreadExecutorHelper(int reTryCount) --> reTryCount 必须大于等于[-1]");
		}
	}

	/**
	 * 
	 * @Title: execute
	 * @Description: 执行当前线程
	 * @author 简道
	 * @param 设定文件
	 * @return void 返回类型
	 */
	public void execute() {
		threadPoolExecutor.execute(new Runnable() {
			public void run() {
				try {
					doJob(params);
				} catch (Throwable e) {
					errorCount[0] = errorCount[0] + 1;
					logger.error(StackTraceUtil.getStackTrace(e));
					if (reTryCount == -1) {
						execute();
					} else if (reTryCount == 0) {
						// do nothing
					} else if (reTryCount > 0) {
						if (errorCount[0] < reTryCount) {
							execute();
						} else {
							doException(e, params);
						}
					}
				}
			}
		});
	}

	public abstract void doJob(Map<String, Object> params);

	public abstract void doException(Throwable e, Map<String, Object> params);

	/**
	 * 
	 * @ClassName: ThreadExecutorMonitor
	 * @Description: 监控ThreadExecutor，因为执行线程为非守护线程
	 * @author 简道
	 * @date 2011-8-11 上午10:50:19
	 * 
	 */
	public static class ThreadExecutorMonitor {

		/**
		 * 
		 * 
		 * @Title: shutdown
		 * @Description: 关掉线程池
		 * @author 简道
		 * @param 设定文件
		 * @return void 返回类型
		 */
		public void shutdown() {
			threadPoolExecutor.shutdown();
		}

	}
}
