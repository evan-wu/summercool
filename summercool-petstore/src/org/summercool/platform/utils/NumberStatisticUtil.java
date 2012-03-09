package org.summercool.platform.utils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数值型统计辅助类
 * 
 * @Title: NumberStatisticUtil.java
 * @Package com.gexin.platform.biz.common.utils
 * @author Yanjh
 * @date 2012-2-14 上午11:21:33
 * @version V1.0
 */
public class NumberStatisticUtil {
	/**
	 * 用于保存各指标的统计数值
	 */
	private ConcurrentHashMap<String, StatisticHandler> statisticMap = new ConcurrentHashMap<String, StatisticHandler>();

	/**
	 * 指标的单位统计时间（毫秒），默认为1分钟
	 */
	private HashMap<String, Long> intervalMap = new HashMap<String, Long>();

	/**
	 * 为指标设定单位统计时间
	 * 
	 * @Title: setInterval
	 * @author Yanjh
	 * @param key
	 *            指标key
	 * @param interval
	 *            单位统计时间，毫秒
	 * @return void 返回类型
	 */
	public void setInterval(String key, long interval) {
		if (interval <= 0) {
			throw new IllegalArgumentException("interval must great than 0.");
		}
		intervalMap.put(key, interval);
	}

	/**
	 * 获取指定指标，前一次统计值
	 * 
	 * @Title: getPreValue
	 * @author Yanjh
	 * @param key
	 *            指标key
	 * @return long 返回类型
	 */
	public long getPreValue(String key) {
		StatisticHandler statisticHandler = statisticMap.get(key);
		if (statisticHandler == null) {
			return 0L;
		}

		return statisticHandler.getPreValue();
	}

	/**
	 * 获取指定指标，当前统计值
	 * 
	 * @Title: getValue
	 * @author Yanjh
	 * @param key
	 *            指标Key
	 * @return long 返回类型
	 */
	public long getValue(String key) {
		StatisticHandler statisticHandler = statisticMap.get(key);
		if (statisticHandler == null) {
			return 0L;
		}

		return statisticHandler.getValue();
	}

	/**
	 * 为指定指标增加指定量，并返回当前统计值
	 * 
	 * @Title: addAndGet
	 * @author Yanjh
	 * @param key
	 *            指标Key
	 * @param delta
	 *            增量
	 * @return long 返回类型
	 */
	public long addAndGet(String key, long delta) {
		StatisticHandler statisticHandler = statisticMap.get(key);
		//
		if (statisticHandler == null) {
			Long interval = intervalMap.get(key);
			if (interval == null) {
				statisticHandler = new DefaultStatisticHandlerImpl();
			} else {
				statisticHandler = new DefaultStatisticHandlerImpl(interval);
			}
			StatisticHandler existedHandler = statisticMap.putIfAbsent(key, statisticHandler);
			// 处理并发
			if (existedHandler != null) {
				statisticHandler = existedHandler;
			}
		}

		return statisticHandler.addValue(delta);
	}

	/**
	 * 为指定指标加1，并返回当前统计值
	 * 
	 * @Title: incrementAndGet
	 * @author Yanjh
	 * @param key
	 *            指标Key
	 * @return long 返回类型
	 */
	public long incrementAndGet(String key) {
		return addAndGet(key, 1);
	}

	private interface StatisticHandler {
		/**
		 * 获取前一次统计值
		 * 
		 * @Title: getPreValue
		 * @author Yanjh
		 * @return long 返回类型
		 */
		long getPreValue();

		/**
		 * 获取当前统计值
		 * 
		 * @Title: getValue
		 * @author Yanjh
		 * @return long 返回类型
		 */
		long getValue();

		/**
		 * 增加指定值后，返回当前统计值
		 * 
		 * @Title: addValue
		 * @author Yanjh
		 * @param delta
		 *            增量
		 * @return long 返回类型
		 */
		long addValue(long delta);
	}

	/**
	 * 以分钟为单位的统计算法
	 * 
	 * @ClassName: DefaultStatisticHandlerImpl
	 * @author Yanjh
	 * @date 2012-2-14 上午11:28:32
	 * 
	 */
	private static class DefaultStatisticHandlerImpl implements StatisticHandler {
		private long interval = 60 * 1000;

		private AtomicLong preValue = new AtomicLong();
		private AtomicLong value = new AtomicLong();
		private long time;

		public DefaultStatisticHandlerImpl() {

		}

		public DefaultStatisticHandlerImpl(long interval) {
			this.interval = interval;
		}

		public long getPreValue() {
			checkTime();
			return preValue.get();
		}

		public long getValue() {
			checkTime();
			return value.get();
		}

		public long addValue(long delta) {
			checkTime();
			return value.addAndGet(delta);
		}

		private void checkTime() {
			long now = System.currentTimeMillis();
			if (now > time + interval) {
				// 此处加锁后，重新做判断，以避免并发调用
				synchronized (this) {
					if (now > time + interval) {
						preValue.set(value.get());
						value.set(0L);
						time = now;
					}
				}
			}
		}

	}

}
