package org.summercool.web.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import org.summercool.web.module.WebModuleConfigurer;
import org.summercool.web.servlet.DispatcherServlet;

public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext {

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	/** List of webModuleConfigurers used by this servlet */
	private List<WebModuleConfigurer> webModuleConfigurers;

	/**
	 * 初始化WebModuleConfigurer
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initWebModuleConfigurer(ApplicationContext applicationContext) {
		urlPathHelper.setUrlDecode(false);
		webModuleConfigurers = null;
		Map<String, WebModuleConfigurer> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, WebModuleConfigurer.class, true, false);
		if (!matchingBeans.isEmpty()) {
			webModuleConfigurers = new ArrayList<WebModuleConfigurer>(matchingBeans.values());
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		//
		super.postProcessBeanFactory(beanFactory);
		//
		initWebModuleConfigurer(this);
		scanControllerAndWidget(this, beanFactory);
	}

	/**
	 * 扫描Controllers和Widgets
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void scanControllerAndWidget(ApplicationContext applicationContext,
			ConfigurableListableBeanFactory beanFactory) {
		ClassPathControllerAndWidgetScanner scanner = new ClassPathControllerAndWidgetScanner(webModuleConfigurers,
				applicationContext);
		//
		scanner.scan();
		//
		SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
		simpleUrlHandlerMapping.setUrlMap(scanner.getControllers());
		simpleUrlHandlerMapping.setUrlPathHelper(urlPathHelper);
		simpleUrlHandlerMapping.setOrder(-Integer.MAX_VALUE);
		//
		beanFactory.initializeBean(simpleUrlHandlerMapping, DispatcherServlet.SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME);
		beanFactory.registerSingleton(DispatcherServlet.SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME, simpleUrlHandlerMapping);
	}

	public static Class<XmlWebApplicationContext> getClassType() {
		return XmlWebApplicationContext.class;
	}

}