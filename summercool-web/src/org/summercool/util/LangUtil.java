package org.summercool.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: GexinUtils.java
 * @Package com.gexin.platform.biz.common.utils
 * @Description: 通用工具处理类
 * @author wujl
 * @date 2011-8-4 下午12:01:25
 * @version V1.0
 */
public class LangUtil {
	static Logger logger = LoggerFactory.getLogger(LangUtil.class);

	/***
	 * 
	 * @Title: convertToBig
	 * @Description: 将字符串转换为长整型数
	 * @author wujl
	 * @param data
	 * @return long 返回类型
	 */
	public static long convertToBig(String data, String fromBase, String toBase) {
		if (fromBase == null) {
			fromBase = "0123456789ABCDEF";
		}

		if (toBase == null) {
			toBase = "0123456789";
		}

		data = data.toUpperCase();
		int fromCount = fromBase.length();
		int toCount = toBase.length();
		String result = "";
		int length = data.length();

		int[] numArr = new int[length];
		for (int i = 0; i < length; i++) {
			numArr[i] = fromBase.indexOf(data.charAt(i));
		}

		int newLen;
		do {
			int divide = 0;
			newLen = 0;

			for (int i = 0; i < length; i++) {
				divide = divide * fromCount + numArr[i];
				if (divide >= toCount) {
					numArr[newLen++] = divide / toCount;
					divide = divide % toCount;
				} else if (newLen > 0) {
					numArr[newLen++] = 0;
				}
			}

			length = newLen;
			result = toBase.charAt(divide) + result;

		} while (newLen != 0);
		return Long.parseLong(result);
	}

	/**
	 * @Title: parseInt
	 * @Description: Int解析方法，可传入Integer或String值
	 * @author Yanjh
	 * @param value
	 *        Integer或String值
	 * @return Integer 返回类型
	 */
	public static Integer parseInt(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String) {
				try {
					return Integer.valueOf((String) value);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * @Title: getASCIIString
	 * @Description: 获取ASCII编码字符串
	 * @author Yanjh
	 * @param str
	 * @param @return
	 * @return String 返回类型
	 */
	public static String getASCIIString(String str) {
		if (str != null) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(StackTraceUtil.getStackTrace(e));
			}
		}
		return null;
	}

	/**
	 * @Title: getUTF8String
	 * @Description: 获取UTF8编码字符串
	 * @author Yanjh
	 * @param str
	 * @param @return
	 * @return String 返回类型
	 */
	public static String getUTF8String(String str) {
		if (str != null) {
			try {
				return URLDecoder.decode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(StackTraceUtil.getStackTrace(e));
			}
		}
		return null;
	}

	/***
	 * 
	 * @Title: parseLong
	 * @Description: long解析方法，可传入Long或String值
	 * @author wujl
	 * @param @param value Integer或String值
	 * @param @return
	 * @return Long 返回类型
	 */
	public static Long parseLong(Object value) {
		if (value != null) {
			if (value instanceof Long) {
				return (Long) value;
			}
			if (value instanceof Integer) {
				return ((Integer) value).longValue();
			} else if (value instanceof String) {
				try {
					return Long.valueOf((String) value);
				} catch (Exception ex) {
					return null;
				}
			}
		}
		return null;
	}

	public static String parseString(Object value) {
		if (value != null) {
			return String.valueOf(value);
		}
		return null;
	}

	public static Boolean parseBoolean(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return ((Integer) value).intValue() == 1;
			} else if (value instanceof String) {
				return "1".equals(value) || "true".equals(value);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> parseList(Object list, Class<T> requiredType) {
		try {
			if (list instanceof List) {
				for (Object obj : (List<Object>)list) {
					if (!requiredType.isInstance(obj)) {
						return null;
					}
				}	
				return (List<T>)list;
			} else {
				return null;
			}			
		} catch (Exception ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, E> Map<T, E> parseMap(Object map, Class<T> keyType, Class<E> valueType) {
		try {
			if (map instanceof Map) {
				for (Entry<Object, Object> entry : ((Map<Object, Object>)map).entrySet()) {
					Object key = entry.getKey();
					Object value = entry.getValue();
					if (!keyType.isInstance(key)) {
						return null;
					}
					if (!valueType.isInstance(value)) {
						return null;
					}
				}	
				return (Map<T, E>)map;
			} else {
				return null;
			}			
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static void main(String arg[]) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		System.out.println(parseList(list, Integer.class));
		

		List<String> list2 = new ArrayList<String>();
		list2.add("a");
		list2.add("2");
		List<Integer> list3 = parseList(list2, Integer.class);		
		System.out.println(list3);		
		System.out.println(parseList("2", Integer.class));	
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("111", 11);
		map.put("112", "aa");
		System.out.println(parseMap(map, String.class, Object.class));		
		System.out.println(parseMap("2", String.class, Object.class));		
		System.out.println(parseMap(map, Integer.class, Object.class));		
		System.out.println(parseMap(map, String.class, Integer.class));	
		Map<String, List<Integer>> map1 = new HashMap<String, List<Integer>>();
		map1.put("aaa", list);
		System.out.println(parseMap(map1, String.class, List.class));	
		
		List<Map<String, Object>> list4 = new ArrayList<Map<String, Object>>();
		list4.add(map);
		System.out.println(parseList(list4, Map.class));		
	}
}
