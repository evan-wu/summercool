package org.summercool.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title: DateUtil.java
 * @Package com.gexin.platform.biz.common.utils
 * @Description: 日期辅助类
 * @author Yanjh
 * @date 2011-8-16 上午11:34:04
 * @version V1.0
 */
public class DateUtil {

	/**
	 * @Title: getDateString
	 * @Description: 使用"yyyy-MM-dd HH:mm:ss"格式化日期
	 * @author Yanjh
	 * @param date
	 * @return String 返回类型
	 */
	public static String getDateString(Date date) {
		return getDateString(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @Title: getDateString
	 * @Description: 格式化日期
	 * @author Yanjh
	 * @param date
	 *        日期
	 * @param format
	 *        模式
	 * @return String 返回类型
	 */
	public static String getDateString(Date date, String format) {
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			String dateString = formatter.format(date);
			return dateString;
		}
		return null;
	}
}