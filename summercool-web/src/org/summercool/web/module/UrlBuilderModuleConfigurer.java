package org.summercool.web.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.summercool.beans.url.UrlBuilderBeanDefinition;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-13
 * 
 */
public class UrlBuilderModuleConfigurer {

	private Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap = new HashMap<String, List<UrlBuilderBeanDefinition>>();

	public Map<String, List<UrlBuilderBeanDefinition>> getUrlBuilderBeanMap() {
		return urlBuilderBeanMap;
	}

	public void setUrlBuilderBeanMap(Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap) {
		this.urlBuilderBeanMap = urlBuilderBeanMap;
	}

}
