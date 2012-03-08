package org.summercool.beans.url;

import java.util.Map;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 *
 */
public interface UrlBuilderBeanDefinition {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public boolean isMatched();
	
	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public String getUriTemplateModule();
	
	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public String getSeq();
	
	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public String getUriTemplate();
	
	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public Map<String,String> getUriTemplateVariables();
	
	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-13
	 * @return
	 */
	public String getUri();
}
