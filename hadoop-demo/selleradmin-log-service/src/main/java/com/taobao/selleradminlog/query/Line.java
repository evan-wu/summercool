/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.taobao.selleradminlog.Constants;

/**
 * 代表日志文件中的一行。
 * @author guoyou
 */
public class Line {

	public final static String[] columnNames = new String[]{
		Constants.COLUMN_NAME_LINE_NUM, // "lineNum",	// 0
		Constants.COLUMN_NAME_TIME, // "time",	// 1
		Constants.COLUMN_NAME_MAIN_ID, // "mainId", 	// 2
		Constants.COLUMN_NAME_SUB_ID, //"subId", 	// 3
		Constants.COLUMN_NAME_OPERATION_TYPE, //"operationType",	// 4
		Constants.COLUMN_NAME_OBJECT1, // "object1",	// 5
		Constants.COLUMN_NAME_OBJECT2, // "object2",	// 6
		Constants.COLUMN_NAME_MEMO, // "memo"		// 7
	};
	
	/**
	 * columnNames的反向映射。
	 */
	private final static Map<String, Integer> map = new TreeMap<String, Integer>();
	static {
		for (int i = 0; i < columnNames.length; i++) {
			map.put(columnNames[i].toLowerCase(), i );
		}
	}
	
	private String[] columns;
	private String line;
	
	public Line(String line) throws LineFormatException {
		if (line == null) {
			throw new LineFormatException("Line can not be null.");
		}
		
		this.line = line;
		columns = line.split(Constants.DEFAULT_COLUMN_SEPARATOR);
		if (columns.length < columnNames.length) {
			throw new LineFormatException("Line format error: " + line);
		}
	}
	
	public String getLine() {
		return line;
	}
	
	public String getColumn(int index) {
		return columns[index];
	}
	
	public String getColumn(String columnName) {
		return columns[map.get( columnName.toLowerCase() )];
	}
	
	public Date getTime() {
		try {
			return new SimpleDateFormat(Constants.LINE_DATE_FORMAT).parse( columns[1] );
		} catch (ParseException e) {
		}
		return null;
	}
	
	public Long getMainId() {
		return Long.valueOf(columns[2]);
	}
	
	public Long getSubId() {
		return Long.valueOf(columns[3]);
	}
	
	public String getOperationType() {
		return columns[4];
	}
	
	public String getMemo() {
		return columns[7];
	}
	
	
	@Override
	public String toString() {
		return line;
	}
	
	/**
	 * 日志行格式错误异常。
	 * @author guoyou
	 */
	public static class LineFormatException extends Exception {
		private static final long serialVersionUID = 537497174715190863L;

		/**
	     * Constructs an <code>LineFormatException</code> with no
	     * specified detail message.
	     * @since JDK1.1
	     */
	    public LineFormatException() {
		super();
	    }

	    /**
	     * Constructs an <code>LineFormatException</code> with the specified
	     * detail message.
	     *
	     * @param s the detail message
	     * @since JDK1.1
	     */
	    public LineFormatException(String s) {
		super(s);
	    }
	}
	
}
