package org.summercool.web.module.url;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.summercool.beans.url.DefaultUrlBuilderBeanDefinition;
import org.summercool.beans.url.UrlBuilderBeanDefinition;
import org.summercool.util.UriTemplateUtils;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-14
 * 
 */
public class DefaultUrlBuilderModule implements UrlBuilderModule {

	private HttpServletRequest request;

	private Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap;

	public DefaultUrlBuilderModule(HttpServletRequest request,
			Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap) {
		super();
		this.request = request;
		this.urlBuilderBeanMap = urlBuilderBeanMap;
	}

	public UrlBuilderBeanDefinition matchUrlBuilderBean() {
		return matchUrlBuilderBean(request);
	}

	private UrlBuilderBeanDefinition matchUrlBuilderBean(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return matchUrlBuilderBean(dropSemicolonString(uri));
	}

	private UrlBuilderBeanDefinition matchUrlBuilderBean(String uri) {
		if (!StringUtils.hasText(uri) || this.urlBuilderBeanMap == null || this.urlBuilderBeanMap.size() == 0) {
			return new DefaultUrlBuilderBeanDefinition();
		}
		uri = dropSemicolonString(uri);
		//
		Map<String, String> uriTemplateVariables = new HashMap<String, String>();
		for (Map.Entry<String, List<UrlBuilderBeanDefinition>> entry : this.urlBuilderBeanMap.entrySet()) {
			List<UrlBuilderBeanDefinition> urlBuilderBeanDefinitions = entry.getValue();
			if (urlBuilderBeanDefinitions == null || urlBuilderBeanDefinitions.size() == 0) {
				continue;
			}
			for (UrlBuilderBeanDefinition ubbd : urlBuilderBeanDefinitions) {
				if (UriTemplateUtils.analyseUri(ubbd.getUriTemplate(), uri, uriTemplateVariables)) {
					DefaultUrlBuilderBeanDefinition dubbd = new DefaultUrlBuilderBeanDefinition();
					dubbd.setMatched(true);
					dubbd.setUriTemplateModule(ubbd.getUriTemplateModule());
					dubbd.setSeq(ubbd.getSeq());
					dubbd.setUri(uri);
					dubbd.setUriTemplate(ubbd.getUriTemplate());
					dubbd.setUriTemplateVariables(uriTemplateVariables);
					return dubbd;
				}
			}
		}
		return new DefaultUrlBuilderBeanDefinition();
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-19
	 * @param uri
	 * @return
	 */
	private String dropSemicolonString(String uri) {
		int semicolonIndex = uri.indexOf(';');
		uri = (semicolonIndex != -1 ? uri.substring(0, semicolonIndex) : uri);
		return uri;
	}
}
