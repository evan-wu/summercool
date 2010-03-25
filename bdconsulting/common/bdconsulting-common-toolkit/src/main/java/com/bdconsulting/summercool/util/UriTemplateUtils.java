package com.bdconsulting.summercool.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public abstract class UriTemplateUtils {

	private static final Pattern pattern = Pattern.compile(":80[0-9]*");

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
	 * 测试用
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-11
	 * @param args
	 */
	public static void main(String[] args) {
		String uriTemplate = "/search-tid-{tid}-cid-{cid}.htm";
		String uri = "/search-tid-1-cid-a.htm";
		//
		Map<String, String> uriTemplateVariables = new HashMap<String, String>();
		System.out.println(analyseUri(uriTemplate, uri, uriTemplateVariables));
		//
		for (Map.Entry<String, String> entry : uriTemplateVariables.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
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
		for (Map.Entry<String, Object> entry : uriTemplateVariables.entrySet()) {
			uriTemplate = uriTemplate.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
		}
		return uriTemplate;
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
