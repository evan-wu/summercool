package org.summercool.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-24
 * 
 */
public class BeanUtils {

	private static final String SLASH = "/";

	public static final String WIDGET_BEAN_SUFFIX = "Widget";

	public static final String CONTROLLER_BEAN_SUFFIX = "Controller";

	private static Pattern CONTROLLER_KEY_PATTERN = Pattern.compile("\\p{Upper}");

	private static Pattern URI_PATTERN = Pattern.compile("\\_\\p{Lower}");

	public static boolean isWidget(String className) {
		if (!StringUtils.hasText(className)) {
			return false;
		}
		if (className.endsWith(WIDGET_BEAN_SUFFIX)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过类名来判断当前类是否是ControllerBean
	 * 
	 * @param className
	 * @return
	 */
	public static boolean isController(String className) {
		if (!StringUtils.hasText(className)) {
			return false;
		}
		if (className.endsWith(CONTROLLER_BEAN_SUFFIX)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-24
	 * @param path
	 * @return
	 */
	public static String makeControllerOrWidgetMappingKey(String path) {
		int position = path.lastIndexOf(SLASH);
		String packagePath = path.substring(0, position);
		String objectName = path.substring(position + 1);
		String mappingKey = packagePath + SLASH + objectName.substring(0, 1).toLowerCase() + objectName.substring(1);
		return mappingKey;
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-4-16
	 * @param path
	 * @return
	 */
	public static String processControllerKey(String path) {
		StringBuffer result = new StringBuffer();
		Matcher matcher = CONTROLLER_KEY_PATTERN.matcher(path);
		while (matcher.find()) {
			matcher.appendReplacement(result, "_" + matcher.group().toLowerCase());
		}
		matcher.appendTail(result);
		return result.toString();
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-7-21
	 * @param uri
	 * @return
	 */
	public static String processUri(String uri) {
		StringBuffer result = new StringBuffer();
		Matcher matcher = URI_PATTERN.matcher(uri);
		while (matcher.find()) {
			matcher.appendReplacement(result, matcher.group().toUpperCase().replace("_", ""));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	/**
	 * 去掉字符串后面指定的字符
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2009-8-5
	 * @param string
	 * @return
	 */
	public static String stripEndString(String string, String endString) {
		int sepIndex = string.lastIndexOf(endString);
		return (sepIndex != -1 ? string.substring(0, sepIndex) : string);
	}
}
