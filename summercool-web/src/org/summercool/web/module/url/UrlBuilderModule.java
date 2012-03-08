package org.summercool.web.module.url;

import org.summercool.beans.url.UrlBuilderBeanDefinition;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-14
 * 
 */
public interface UrlBuilderModule {

	String URL_BUILDER = UrlBuilderModule.class.getName() + ".URL_BUILDER";

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-14
	 * @param request
	 * @return
	 */
	public UrlBuilderBeanDefinition matchUrlBuilderBean();

}
