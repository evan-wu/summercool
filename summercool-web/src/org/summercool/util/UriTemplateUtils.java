package org.summercool.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

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
	 * 测试用
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-11
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		String uriTemplate = "/{tid}c{page}p0t0h0v{keyword}q-large.htm";
		String uri = "/2503c2p0t0h0v2vq-large.htm";
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
