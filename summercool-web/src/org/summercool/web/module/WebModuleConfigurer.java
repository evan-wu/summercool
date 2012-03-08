package org.summercool.web.module;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-11
 * 
 */
public class WebModuleConfigurer implements InitializingBean {

	// Uri扩展名
	private String uriExtension;

	// 模块名
	private String moduleName;

	// Module包的根路径
	private String moduleBasePackage;

	// Module的Uri映射
	private String context;

	// Module的Uri映射所对应的package
	private String contextPackage;

	// Widgets Map集合
	private Map<String, FreeMarkerWidget> widgetMap = new LinkedHashMap<String, FreeMarkerWidget>();

	// Controllers Map集合
	private Map<String, Controller> controllerMap = new LinkedHashMap<String, Controller>();

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getUriExtension() {
		return uriExtension;
	}

	public void setUriExtension(String uriExtension) {
		this.uriExtension = uriExtension;
	}

	public String getModuleBasePackage() {
		return moduleBasePackage;
	}

	public void setModuleBasePackage(String moduleBasePackage) {
		this.moduleBasePackage = moduleBasePackage;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContextPackage() {
		return contextPackage;
	}

	public void setContextPackage(String contextPackage) {
		this.contextPackage = contextPackage;
	}

	public Map<String, FreeMarkerWidget> getWidgetMap() {
		return widgetMap;
	}

	public Map<String, Controller> getControllerMap() {
		return controllerMap;
	}

	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.hasText(moduleName)) {
			throw new Exception("WebModuleConfigurer的moduleName不能为空!");
		}
		//
		if (!StringUtils.hasText(uriExtension)) {
			throw new Exception("WebModuleConfigurer的uriExtension不能为空!");
		}
		//
		if (!StringUtils.hasText(moduleBasePackage)) {
			throw new Exception("WebModuleConfigurer的moduleBasePackage不能为空!");
		}
		//
		if (!StringUtils.hasText(context)) {
			throw new Exception("WebModuleConfigurer的uriMapping不能为空!");
		}
		//
		if (!StringUtils.hasText(contextPackage)) {
			throw new Exception("WebModuleConfigurer的uriMappingPackage不能为空!");
		}
	}
}
