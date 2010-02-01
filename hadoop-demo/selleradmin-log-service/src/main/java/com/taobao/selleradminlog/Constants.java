/**
 * 
 */
package com.taobao.selleradminlog;

/**
 * 常量
 */
public interface Constants {

	/**
	 * 日志文件中默认的列分隔符。
	 */
	public static final String DEFAULT_COLUMN_SEPARATOR = "\t";
	
	public static final String LINE_DATE_FORMAT = "yyyyMMddHHmmss.SSS";
	
	public static final String COLUMN_NAME_LINE_NUM = "lineNum";
	public static final String COLUMN_NAME_TIME = "time";
	public static final String COLUMN_NAME_MAIN_ID = "mainId";
	public static final String COLUMN_NAME_SUB_ID = "subId";
	
	public static final String COLUMN_NAME_OPERATION_TYPE = "operationType";
	public static final String COLUMN_NAME_OBJECT1 = "object1";
	public static final String COLUMN_NAME_OBJECT2 = "object2";
	public static final String COLUMN_NAME_MEMO = "memo";
	
}
