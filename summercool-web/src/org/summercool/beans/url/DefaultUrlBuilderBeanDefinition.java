package org.summercool.beans.url;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-11
 * 
 */
public class DefaultUrlBuilderBeanDefinition implements UrlBuilderBeanDefinition {

	// 同一类型的Uri所对应的模块名
	private String uriTemplateModule;

	//
	private boolean matched;

	// 同一个UrlBuilderModule下面的UrlBuilder序列
	private String seq;

	// 当前请求的uri
	private String uri;

	// 匹配到的uri模板
	private String uriTemplate;

	// 匹配到的uri模板中的参数Map
	public Map<String, String> uriTemplateVariables = new HashMap<String, String>();

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setUriTemplateVariables(Map<String, String> uriTemplateVariables) {
		this.uriTemplateVariables = uriTemplateVariables;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getUri() {
		return uri;
	}

	public String getUriTemplate() {
		return uriTemplate;
	}

	public void setUriTemplate(String uriTemplate) {
		this.uriTemplate = uriTemplate;
	}

	public String getUriTemplateModule() {
		return uriTemplateModule;
	}

	public void setUriTemplateModule(String uriTemplateModule) {
		this.uriTemplateModule = uriTemplateModule;
	}

	public Map<String, String> getUriTemplateVariables() {
		return uriTemplateVariables;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

}
