package org.summercool.web.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.Controller;
import org.summercool.web.module.WebModuleConfigurer;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;
import org.summercool.web.util.BeanUtils;

/**
 * SummerCool框架的自定义规则Controller扫描类
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class ClassPathControllerAndWidgetScanner {

	protected final Log logger = LogFactory.getLog(getClass());

	public static final String RESOURCE_PATTERN = "**/*.class";

	public static final String SLASH = "/";

	public static final String CONTROLLERS_PACKAGE = "controllers";

	public static final String VIEWS_PACKAGE = "views";

	private List<WebModuleConfigurer> webModuleConfigurers;

	private ApplicationContext applicationContext;

	private DefaultListableBeanFactory defaultListableBeanFactory;

	private ClassLoader classLoader;

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private Map<String, Object> controllers = new HashMap<String, Object>();

	private Map<String, FreeMarkerWidget> widgets = new HashMap<String, FreeMarkerWidget>();

	public ClassPathControllerAndWidgetScanner(List<WebModuleConfigurer> webModuleConfigurers,
			ApplicationContext applicationContext) {
		super();
		this.webModuleConfigurers = webModuleConfigurers;
		this.applicationContext = applicationContext;
		this.defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
				.getAutowireCapableBeanFactory();
		this.classLoader = applicationContext.getClassLoader();
	}

	public Map<String, Object> getControllers() {
		return controllers;
	}

	public Map<String, FreeMarkerWidget> getWidgets() {
		return widgets;
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-24
	 */
	public void scan() {
		if (webModuleConfigurers == null) {
			return;
		}
		for (WebModuleConfigurer webModuleConfigurer : webModuleConfigurers) {
			scan(webModuleConfigurer);
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-24
	 */
	private void scan(WebModuleConfigurer webModuleConfigurer) {
		try {
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(webModuleConfigurer.getModuleBasePackage())
					+ webModuleConfigurer.getContextPackage() + RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
			//
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					ClassMetadata classMetadata = reader.getClassMetadata();
					//
					if (classMetadata.isConcrete()) {
						String className = classMetadata.getClassName();
						if (!(BeanUtils.isWidget(className) || BeanUtils.isController(className))) {
							continue;
						}
						//
						AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
						Object clazz = beanFactory.createBean(classLoader.loadClass(className),
								AutowireCapableBeanFactory.AUTOWIRE_NO, false);
						//
						if (clazz instanceof FreeMarkerWidget) {
							String widget = className.replace(webModuleConfigurer.getModuleBasePackage(), "");
							widget = ClassUtils.convertClassNameToResourcePath(widget);
							widget = BeanUtils.stripEndString(widget, BeanUtils.WIDGET_BEAN_SUFFIX);
							String widgetKey = BeanUtils.makeControllerOrWidgetMappingKey(widget);
							webModuleConfigurer.getWidgetMap().put(widgetKey, (FreeMarkerWidget) clazz);
							widgets.put(widgetKey, (FreeMarkerWidget) clazz);
						}
						//
						if (clazz instanceof Controller) {
							String controller = className.replace(webModuleConfigurer.getModuleBasePackage(), "");
							controller = ClassUtils.convertClassNameToResourcePath(controller);
							controller = BeanUtils.stripEndString(controller, BeanUtils.CONTROLLER_BEAN_SUFFIX);
							String controllerKey = BeanUtils.makeControllerOrWidgetMappingKey(controller);
							String controllerRule = webModuleConfigurer.getContextPackage() + CONTROLLERS_PACKAGE
									+ SLASH;
							if (controllerKey.startsWith(controllerRule)) {
								//
								controllerKey = controllerKey.replaceFirst(controllerRule,
										webModuleConfigurer.getContext());
								//
								if ("/index".equals(controllerKey)) {
									webModuleConfigurer.getControllerMap().put("/", (Controller) clazz);
									controllers.put("/", (Controller) clazz);
									defaultListableBeanFactory.registerSingleton("/", (Controller) clazz);
								}
								//
								String controller_key = BeanUtils.processControllerKey(controllerKey);
								String controller_keyv = controller_key + webModuleConfigurer.getUriExtension();
								//
								webModuleConfigurer.getControllerMap().put(controller_keyv, (Controller) clazz);
								controllers.put(controller_keyv, (Controller) clazz);
								defaultListableBeanFactory.registerSingleton(controller_keyv, (Controller) clazz);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("Failed to scan classpath for unlisted classes", e);
			throw new RuntimeException("Failed to scan classpath for unlisted classes", e);
		} catch (ClassNotFoundException e) {
			logger.error("Failed to load classes from classpath", e);
			throw new RuntimeException("Failed to load classes from classpath", e);
		}
	}
}
