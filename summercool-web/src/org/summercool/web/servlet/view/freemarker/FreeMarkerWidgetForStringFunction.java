package org.summercool.web.servlet.view.freemarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 用来渲染FreeMarker页面模版的子页面模版函数,与SummerCool框架帮定
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerWidgetForStringFunction implements TemplateMethodModelEx {

	private Configuration configuration;

	private Map<String, Object> functions = new HashMap<String, Object>();

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Map<String, Object> getFunctions() {
		return functions;
	}

	public void setFunctions(Map<String, Object> functions) {
		this.functions = functions;
	}

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {

		// TODO
		return null;
	}

}
