package org.summercool.web.servlet.view.freemarker;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-18
 * 
 */
public interface FreeMarkerWidget {

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-18
	 * @param request
	 * @param model
	 */
	public void referenceData(HttpServletRequest request, Map<String, Object> model);

}
