/**
 * 
 */
package com.bdconsulting.toolkit.uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;


/**
 * Datetime   ： 2011-5-11 下午05:22:36<br>
 * Title      :  UriTemplateUtils.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public abstract class UriTemplateUtils {

	private static final Pattern pattern = Pattern.compile(":80[0-9]*");

	private static final String ENCODING = "UTF-8";

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-12
	 * @param uri
	 * @return
	 */
	public static String dropUri80Port(String uri) {
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			if (":80".equals(matcher.group())) {
				uri = matcher.replaceAll("");
			}
		}
		return uri;
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-12
	 * @param uriTemplate
	 * @param uriTemplateVariables
	 * @return
	 */
	public static String makeUri(String uriTemplate, Map<String, Object> uriTemplateVariables) {
		if (!StringUtils.hasText(uriTemplate) || uriTemplateVariables == null || uriTemplateVariables.size() == 0) {
			return uriTemplate;
		}
		String url = uriTemplate;
		for (Map.Entry<String, Object> entry : uriTemplateVariables.entrySet()) {
			String placeholder = "{" + entry.getKey() + "}";
			if (uriTemplate.contains(placeholder)) {
				url = url.replace(placeholder, encodeUrl(String.valueOf(entry.getValue())));
			} else if (StringUtils.hasText(String.valueOf(entry.getValue()))) {
				url += (url.contains("?") ? "&" : "?") + entry.getKey() + "=" + encodeUrl(String.valueOf(entry.getValue()));
			}
		}
		return url;
	}

	private static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-11
	 * @param uriTemplate
	 * @param uri
	 * @param uriTemplateVariables
	 * @return
	 */
	public static boolean analyseUri(String uriTemplate, String uri, Map<String, String> uriTemplateVariables) {
		if (!StringUtils.hasText(uriTemplate) || !StringUtils.hasText(uri)) {
			return false;
		}
		AntPathStringMatcher antPathStringMatcher = new AntPathStringMatcher(uriTemplate, uri, uriTemplateVariables);
		return antPathStringMatcher.matchStrings();
	}
}