package org.summercool.beans.url;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.summercool.util.UriTemplateUtils;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-14
 * 
 */
public class UrlBuilderBeanMap implements InitializingBean, FactoryBean<Map<String, List<DefaultUrlBuilderBeanDefinition>>> {

	private Map<String, List<DefaultUrlBuilderBeanDefinition>> urlBuilderBeanMap = new HashMap<String, List<DefaultUrlBuilderBeanDefinition>>();

	public Map<String, List<DefaultUrlBuilderBeanDefinition>> getUrlBuilderBeanMap() {
		return urlBuilderBeanMap;
	}

	public void setUrlBuilderBeanMap(Map<String, List<DefaultUrlBuilderBeanDefinition>> urlBuilderBeanMap) {
		this.urlBuilderBeanMap = urlBuilderBeanMap;
	}

	public void afterPropertiesSet() throws Exception {
		if (urlBuilderBeanMap == null || urlBuilderBeanMap.size() == 0) {
			return;
		}
		//
		for (Map.Entry<String, List<DefaultUrlBuilderBeanDefinition>> entry : urlBuilderBeanMap.entrySet()) {
			String uriTemplateModule = entry.getKey();
			List<DefaultUrlBuilderBeanDefinition> urlBuilderBeanDefinitions = entry.getValue();
			if (urlBuilderBeanDefinitions == null || urlBuilderBeanDefinitions.size() == 0) {
				continue;
			}
			for (DefaultUrlBuilderBeanDefinition urlBuilderBeanDefinition : urlBuilderBeanDefinitions) {
				urlBuilderBeanDefinition.setUriTemplateModule(uriTemplateModule);
				urlBuilderBeanDefinition.setUriTemplate(UriTemplateUtils.dropUri80Port(urlBuilderBeanDefinition.getUriTemplate()));
			}
		}
		Collections.unmodifiableMap(this.urlBuilderBeanMap);
	}

	public Map<String, List<DefaultUrlBuilderBeanDefinition>> getObject() throws Exception {
		return getUrlBuilderBeanMap();
	}

	public Class<?> getObjectType() {
		return Map.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
