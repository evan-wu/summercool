/*
 * Copyright 2010 the original author or authors.
 */

package com.bdconsulting.summercool.web.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import com.bdconsulting.summercool.view.freemarker.FreeMarkerParamFunction;
import com.bdconsulting.summercool.view.freemarker.FreeMarkerParamsFunction;
import com.bdconsulting.summercool.view.freemarker.FreeMarkerUrlFunction;
import com.bdconsulting.summercool.web.context.ClassPathControllerAndWidgetScanner;
import com.bdconsulting.summercool.web.module.CookieModuleConfigurer;
import com.bdconsulting.summercool.web.module.UrlBuilderModuleConfigurer;
import com.bdconsulting.summercool.web.module.WebModuleConfigurer;
import com.bdconsulting.summercool.web.module.cookie.CookieModule;
import com.bdconsulting.summercool.web.module.cookie.DefaultCookieModule;
import com.bdconsulting.summercool.web.module.url.DefaultUrlBuilderModule;
import com.bdconsulting.summercool.web.module.url.UrlBuilderModule;
import com.bdconsulting.summercool.web.servlet.http.HttpServletRequestWrapper;
import com.bdconsulting.summercool.web.servlet.http.HttpServletResponseWrapper;
import com.bdconsulting.summercool.web.servlet.pipeline.AroundProcessPipeline;
import com.bdconsulting.summercool.web.servlet.pipeline.ExceptionPipeline;
import com.bdconsulting.summercool.web.servlet.pipeline.PostProcessPipeline;
import com.bdconsulting.summercool.web.servlet.pipeline.PreProcessPipeline;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerLocaleFunction;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerMessageFunction;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerThemeFunction;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerWidget;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerWidgetForStringFunction;
import com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerWidgetFunction;

/**
 * 自定义Spring MVC的DispatcherServlet
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-9
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

	private static final long serialVersionUID = -7351946190705039889L;

	private static final String SUMMERCOOL_RESOURCE_PATTERN = "/META-INF/spring/summercool/*.xml";

	/** HandlerMapping used by this servlet */
	private HandlerMapping handlerMapping;

	/** List of webModuleConfigurers used by this servlet */
	private List<WebModuleConfigurer> webModuleConfigurers;

	/** urlBuilderModuleConfigurerused by this servlet　 */
	private UrlBuilderModuleConfigurer urlBuilderModuleConfigurer;

	/** cookieModuleConfigurer used by this servlet　 */
	private CookieModuleConfigurer cookieModuleConfigurer;

	/** aroundProcessPipelines used by this servlet　 */
	private List<AroundProcessPipeline> aroundProcessPipelines;

	/** preProcessPipelines used by this servlet　 */
	private List<PreProcessPipeline> preProcessPipelines;

	/** postProcessPipelines used by this servlet　 */
	private List<PostProcessPipeline> postProcessPipelines;

	/** exceptionPipelines used by this servlet　 */
	private List<ExceptionPipeline> exceptionPipelines;

	/** requestClass used by this servlet　 */
	private Class<HttpServletRequest> requestClass;

	/** responseClass used by this servlet　 */
	private Class<HttpServletResponse> responseClass;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	@Override
	protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			String path = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + SUMMERCOOL_RESOURCE_PATTERN;
			Resource[] resources = resourcePatternResolver.getResources(path);
			String[] locations = new String[resources.length];
			//
			for (int i = 0; i < resources.length; i++) {
				locations[i] = resources[i].getURL().toExternalForm();
			}
			//
			wac.setConfigLocations(locations);
		} catch (IOException e) {
			throw new RuntimeException("扫描 summer-cool framework 配置文件失败!", e);
		}
	}

	/**
	 * Initialize the strategy objects that this servlet uses.
	 * <p>
	 * May be overridden in subclasses in order to initialize further strategy objects.
	 */
	public void initStrategies(ApplicationContext context) {
		super.initStrategies(context);
		initWebModuleConfigurer(context);
		initUrlBuilderModuleConfigurer(context);
		initCookieModuleConfigurer(context);
		initAroundProcessPipeline(context);
		initPreProcessPipeline(context);
		initPostProcessPipeline(context);
		initExceptionPipeline(context);
		initRequestAndResponseWrapper(context);
		scanControllerAndWidget(context);
		registerFreeMarkerFunction(context);
	}

	/**
	 * 初始化WebModuleConfigurer
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initWebModuleConfigurer(ApplicationContext applicationContext) {
		this.urlPathHelper.setUrlDecode(false);
		this.webModuleConfigurers = null;
		Map<String, WebModuleConfigurer> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, WebModuleConfigurer.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.webModuleConfigurers = new ArrayList<WebModuleConfigurer>(matchingBeans.values());
		}
	}

	/**
	 * 初始化UrlBuilderModuleConfigurer
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initUrlBuilderModuleConfigurer(ApplicationContext applicationContext) {
		try {
			this.urlBuilderModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					UrlBuilderModuleConfigurer.class, true, false);
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a UrlBuilderModuleConfigurer.
			logger.debug("we do not need a UrlBuilderModuleConfigurer", ex);
		}
	}

	/**
	 * 初始化CookieModuleConfigurer
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initCookieModuleConfigurer(ApplicationContext applicationContext) {
		try {
			this.cookieModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					CookieModuleConfigurer.class, true, false);
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a CookieModuleConfigurer.
			logger.debug("we do not need a CookieModuleConfigurer", ex);
		}
	}

	/**
	 * 初始化AroundProcessPipeline
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initAroundProcessPipeline(ApplicationContext applicationContext) {
		Map<String, AroundProcessPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, AroundProcessPipeline.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.aroundProcessPipelines = new ArrayList<AroundProcessPipeline>(matchingBeans.values());
			// We keep aroundProcessPipelines in sorted order.
			Collections.sort(this.aroundProcessPipelines, new OrderComparator());
		}
	}

	/**
	 * 初始化PreProcessPipeline
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initPreProcessPipeline(ApplicationContext applicationContext) {
		Map<String, PreProcessPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, PreProcessPipeline.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.preProcessPipelines = new ArrayList<PreProcessPipeline>(matchingBeans.values());
			// We keep preProcessPipelines in sorted order.
			Collections.sort(this.preProcessPipelines, new OrderComparator());
		}
	}

	/**
	 * 初始化PostProcessPipeline
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initPostProcessPipeline(ApplicationContext applicationContext) {
		Map<String, PostProcessPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, PostProcessPipeline.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.postProcessPipelines = new ArrayList<PostProcessPipeline>(matchingBeans.values());
			// We keep postProcessPipelines in sorted order.
			Collections.sort(this.postProcessPipelines, new OrderComparator());
		}
	}

	/**
	 * 初始化ExceptionProcessPipeline
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void initExceptionPipeline(ApplicationContext applicationContext) {
		Map<String, ExceptionPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, ExceptionPipeline.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.exceptionPipelines = new ArrayList<ExceptionPipeline>(matchingBeans.values());
			// We keep exceptionPipelines in sorted order.
			Collections.sort(this.exceptionPipelines, new OrderComparator());
		}
	}

	/**
	 * 初始化自定义的RequestWrapper和ResponseWrapper
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	@SuppressWarnings("unchecked")
	private void initRequestAndResponseWrapper(ApplicationContext applicationContext) {
		try {
			HttpServletRequestWrapper matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					HttpServletRequestWrapper.class, true, false);
			this.requestClass = (Class<HttpServletRequest>) matchingBean.getClass();
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a RequestClass.
			logger.debug("we do not need a RequestClass", ex);
		}
		try {
			HttpServletResponseWrapper matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					HttpServletResponseWrapper.class, true, false);
			this.responseClass = (Class<HttpServletResponse>) matchingBean.getClass();
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a ResponseClass.
			logger.debug("we do not need a ResponseClass", ex);
		}
	}

	/**
	 * 扫描Controllers和Widgets
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void scanControllerAndWidget(ApplicationContext applicationContext) {
		ClassPathControllerAndWidgetScanner scanner = new ClassPathControllerAndWidgetScanner(
				this.webModuleConfigurers, applicationContext);
		scanner.scan();
		SimpleUrlHandlerMapping simpleUrlHandlerMapping = applicationContext.getAutowireCapableBeanFactory()
				.createBean(SimpleUrlHandlerMapping.class);
		simpleUrlHandlerMapping.setUrlMap(scanner.getControllers());
		simpleUrlHandlerMapping.setUrlPathHelper(this.urlPathHelper);
		this.handlerMapping = simpleUrlHandlerMapping;
	}

	/**
	 * 注册FreeMarker的内置函数
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param applicationContext
	 */
	private void registerFreeMarkerFunction(ApplicationContext applicationContext) {
		try {
			FreeMarkerViewResolver matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, FreeMarkerViewResolver.class, true, false);
			Map<String, Object> attributes = matchingBean.getAttributesMap();
			Map<String, Object> functions = new HashMap<String, Object>();
			// 注册param()函数
			functions.put("param", BeanUtils.instantiate(FreeMarkerParamFunction.class));
			// 注册params()函数
			functions.put("params", BeanUtils.instantiate(FreeMarkerParamsFunction.class));
			// 注册url()函数
			FreeMarkerUrlFunction freemarkerUrlFunction = BeanUtils.instantiate(FreeMarkerUrlFunction.class);
			functions.put("url", freemarkerUrlFunction);
			// 注册locale()函数
			functions.put("locale", BeanUtils.instantiate(FreeMarkerLocaleFunction.class));
			// 注册message()函数
			functions.put("message", BeanUtils.instantiate(FreeMarkerMessageFunction.class));
			// 注册theme()函数
			functions.put("theme", BeanUtils.instantiate(FreeMarkerThemeFunction.class));
			// 注册widgetForString()函数
			FreeMarkerConfig freeMarkerConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, FreeMarkerConfig.class, true, false);
			FreeMarkerWidgetForStringFunction freemarkerWidgetForStringFunction = BeanUtils.instantiate(FreeMarkerWidgetForStringFunction.class);
			freemarkerWidgetForStringFunction.setConfiguration(freeMarkerConfig.getConfiguration());
			freemarkerWidgetForStringFunction.setFunctions(functions);
			functions.put("widgetForString", freemarkerWidgetForStringFunction);
			// 注册widget()函数
			if (matchingBean instanceof com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerViewResolver) {
				FreeMarkerWidgetFunction freeMarkerWidgetFunction = BeanUtils.instantiate(FreeMarkerWidgetFunction.class);
				freeMarkerWidgetFunction.setFreemarkerViewResolver((com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerViewResolver) matchingBean);
				Map<String, FreeMarkerWidget> widgets = freeMarkerWidgetFunction.getWidgetsMap();
				if (this.webModuleConfigurers != null) {
					for (WebModuleConfigurer webModuleConfigurer : webModuleConfigurers) {
						widgets.putAll(webModuleConfigurer.getWidgetMap());
					}
				}
				functions.put("widget", freeMarkerWidgetFunction);
			}
			attributes.putAll(functions);
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a FreeMarkerViewResolver.
			logger.debug("we do not need a FreeMarkerViewResolver", ex);
		}

	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param request
	 * @return
	 */
	private void buildUrlBuilderModule(HttpServletRequest request) {
		UrlBuilderModule urlBuilderModule = null;
		if (this.urlBuilderModuleConfigurer != null) {
			urlBuilderModule = new DefaultUrlBuilderModule(request, this.urlBuilderModuleConfigurer.getUrlBuilderBeanMap());
		}
		request.setAttribute(UrlBuilderModule.URL_BUILDER, urlBuilderModule);
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-22
	 * @param request
	 * @return
	 */
	private void buildCookieModule(HttpServletRequest request, HttpServletResponse response) {
		CookieModule cookieModule = null;
		if (this.cookieModuleConfigurer != null) {
			cookieModule = new DefaultCookieModule(this.cookieModuleConfigurer.getClientName2CfgMap(), this.cookieModuleConfigurer.getName2CfgMap(), request, response);
		}
		request.setAttribute(CookieModule.COOKIE, cookieModule);
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-24
	 * @param request
	 * @return
	 */
	private String requestToViewName(HttpServletRequest request) {
		String viewName = null;
		if (this.webModuleConfigurers == null || this.webModuleConfigurers.size() == 0) {
			return viewName;
		}
		//
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		String bestPathMatch = null;
		String bestPathMatchPackage = null;
		String context = "";
		String contextPackage = "";
		for (WebModuleConfigurer webModuleConfigurer : this.webModuleConfigurers) {
			context = webModuleConfigurer.getContext();
			contextPackage = webModuleConfigurer.getContextPackage();
			if (lookupPath.startsWith(context) && (bestPathMatch == null || bestPathMatch.length() < context.length())) {
				bestPathMatch = context;
				bestPathMatchPackage = contextPackage;
			}
		}
		//
		if (bestPathMatch != null && bestPathMatchPackage != null) {
			viewName = lookupPath.replaceFirst(bestPathMatch, bestPathMatchPackage + ClassPathControllerAndWidgetScanner.VIEWS_PACKAGE + ClassPathControllerAndWidgetScanner.SLASH);
			int position = viewName.lastIndexOf(".");
			viewName = (position != -1) ? viewName.substring(0, position) : viewName;
		}
		return viewName;
	}

	@Override
	public void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置自定义的request和response包装类
		if (requestClass != null) {
			Constructor<HttpServletRequest> ctor = requestClass.getDeclaredConstructor(HttpServletRequest.class);
			request = BeanUtils.instantiateClass(ctor, request);
		}
		if (responseClass != null) {
			Constructor<HttpServletResponse> ctor = responseClass.getDeclaredConstructor(HttpServletResponse.class);
			response = BeanUtils.instantiateClass(ctor, response);
		}
		//
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		int interceptorIndex = -1;
		Exception exception;
		//
		try {
			String viewName;
			ModelAndView mv;
			boolean errorView = false;
			try {
				//
				buildUrlBuilderModule(processedRequest);
				buildCookieModule(processedRequest, response);
				//
				if (this.preProcessPipelines != null) {
					for (PreProcessPipeline preProcessPipeline : this.preProcessPipelines) {
						if (!preProcessPipeline.isPermitted(processedRequest, response)) {
							mv = preProcessPipeline.handleProcessInternal(processedRequest, response);
							render(mv, processedRequest, response);
							return;
						}
					}
				}
				//
				processedRequest = checkMultipart(request);
				// Determine handler for the current request.
				mappedHandler = getHandler(processedRequest, false);
				mappedHandler = mappedHandler == null ? this.handlerMapping.getHandler(processedRequest) : mappedHandler;
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					viewName = requestToViewName(request);
					mv = new ModelAndView();
					mv.setViewName(viewName != null ? viewName : getDefaultViewName(request));
					render(mv, processedRequest, response);
					return;
				}
				// Apply preHandle methods of registered interceptors.
				HandlerInterceptor[] interceptors = mappedHandler.getInterceptors();
				if (interceptors != null) {
					for (int i = 0; i < interceptors.length; i++) {
						HandlerInterceptor interceptor = interceptors[i];
						if (!interceptor.preHandle(processedRequest, response, mappedHandler.getHandler())) {
							triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, null);
							return;
						}
						interceptorIndex = i;
					}
				}
				// Actually invoke the handler.
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
				// Do we need view name translation?
				if (mv != null && !mv.hasView()) {
					viewName = requestToViewName(request);
					mv.setViewName(viewName != null ? viewName : getDefaultViewName(request));
				}
				// Apply postHandle methods of registered interceptors.
				if (interceptors != null) {
					for (int i = interceptors.length - 1; i >= 0; i--) {
						HandlerInterceptor interceptor = interceptors[i];
						interceptor.postHandle(processedRequest, response, mappedHandler.getHandler(), mv);
					}
				}
				//
				if (this.postProcessPipelines != null) {
					for (PostProcessPipeline postProcessPipeline : this.postProcessPipelines) {
						if (!postProcessPipeline.isPermitted(processedRequest, response, mv)) {
							mv = postProcessPipeline.handleProcessInternal(processedRequest, response);
							render(mv, processedRequest, response);
							return;
						}
					}
				}
				//
				render(mv, processedRequest, response);
				triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, null);
				return;
			} catch (ModelAndViewDefiningException ex) {
				logger.debug("ModelAndViewDefiningException encountered", ex);
				mv = ex.getModelAndView();
				exception = ex;
			} catch (Exception ex) {
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				mv = processHandlerException(processedRequest, response, handler, ex);
				errorView = (mv != null);
				exception = ex;
			}
			if (this.exceptionPipelines != null) {
				for (ExceptionPipeline exceptionPipeline : this.exceptionPipelines) {
					exceptionPipeline.handleExceptionInternal(processedRequest, response, mv, exception);
				}
			}
			// Did the handler return a view to render?
			if (mv != null && !mv.wasCleared()) {
				render(mv, processedRequest, response);
				if (errorView) {
					WebUtils.clearErrorRequestAttributes(request);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getServletName()
							+ "': assuming HandlerAdapter completed request handling");
				}
			}
			// Trigger after-completion for successful outcome.
			triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, null);
		} catch (Exception ex) {
			// Trigger after-completion for thrown exception.
			triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, ex);
			throw ex;
		} catch (Error err) {
			ServletException ex = new NestedServletException("Handler processing failed", err);
			// Trigger after-completion for thrown exception.
			triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, ex);
			throw ex;
		} finally {
			// Clean up any resources used by a multipart request.
			if (processedRequest != request) {
				cleanupMultipart(processedRequest);
			}
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-24
	 * @param mappedHandler
	 * @param interceptorIndex
	 * @param request
	 * @param response
	 * @param ex
	 * @throws Exception
	 */
	public void triggerAfterCompletion(HandlerExecutionChain mappedHandler, int interceptorIndex,
			HttpServletRequest request, HttpServletResponse response, Exception ex) throws Exception {
		// Apply afterCompletion methods of registered interceptors.
		if (mappedHandler != null) {
			HandlerInterceptor[] interceptors = mappedHandler.getInterceptors();
			if (interceptors != null) {
				for (int i = interceptorIndex; i >= 0; i--) {
					HandlerInterceptor interceptor = interceptors[i];
					try {
						interceptor.afterCompletion(request, response, mappedHandler.getHandler(), ex);
					} catch (Throwable ex2) {
						logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
					}
				}
			}
		}
	}

}
