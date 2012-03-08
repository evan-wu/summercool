package org.summercool.web.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @Title: LevelRangeFilter.java
 * @Package com.gexin.platform.biz.common.logback.filter
 * @Description: Logback日志输出级别范围过滤器
 * @author Yanjh
 * @date 2011-8-17 上午11:54:46
 * @version V1.0
 */
public class LevelRangeFilter extends AbstractMatcherFilter<ILoggingEvent> {
	Level levelMin;
	Level levelMax;

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}

		if (levelMin != null && event.getLevel().toInt() < levelMin.toInt()) {
			return onMismatch;
		}
		if (levelMax != null && event.getLevel().toInt() > levelMax.toInt()) {
			return onMismatch;
		}
		return onMatch;
	}

	public void setLevelMin(String levelMin) {
		this.levelMin = Level.toLevel(levelMin);
	}

	public void setLevelMax(String levelMax) {
		this.levelMax = Level.toLevel(levelMax);
	}

	public void start() {
		if (this.levelMin != null || this.levelMax != null) {
			super.start();
		}
	}
}
