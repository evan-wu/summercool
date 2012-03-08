/*
 * Copyright 2010 the original author or authors.
 */

package org.summercool.web.servlet;

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
import org.summercool.view.freemarker.FreeMarkerParamFunction;
import org.summercool.view.freemarker.FreeMarkerParamsFunction;
import org.summercool.view.freemarker.FreeMarkerUrlFunction;
import org.summercool.web.context.ClassPathControllerAndWidgetScanner;
import org.summercool.web.context.ResponseContextHolder;
import org.summercool.web.context.XmlWebApplicationContext;
import org.summercool.web.module.CookieModuleConfigurer;
import org.summercool.web.module.UrlBuilderModuleConfigurer;
import org.summercool.web.module.WebModuleConfigurer;
import org.summercool.web.module.cookie.CookieModule;
import org.summercool.web.module.cookie.DefaultCookieModule;
import org.summercool.web.module.url.DefaultUrlBuilderModule;
import org.summercool.web.module.url.UrlBuilderModule;
import org.summercool.web.servlet.http.BufferedResponse;
import org.summercool.web.servlet.pipeline.AroundPipeline;
import org.summercool.web.servlet.pipeline.ExceptionPipeline;
import org.summercool.web.servlet.pipeline.PostProcessPipeline;
import org.summercool.web.servlet.pipeline.PreProcessPipeline;
import org.summercool.web.servlet.view.freemarker.FreeMarkerAbbreviateFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerBreaklineFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerCsrfFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerLocaleFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerMessageFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerThemeFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidgetForStringFunction;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidgetFunction;

/**
 * 自定义Spring MVC的DispatcherServlet
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-9
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

	private static final long serialVersionUID = -7351946190705039889L;

	private static final String SUMMERCOOL_RESOURCE_PATTERN = "/summercool/spring/*.xml";

	public static final String SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME = "summercoolHandlerMapping";

	/** HandlerMapping used by this servlet */
	private HandlerMapping handlerMapping;

	/** List of webModuleConfigurers used by this servlet */
	private List<WebModuleConfigurer> webModuleConfigurers;

	/** urlBuilderModuleConfigurerused by this servlet　 */
	private UrlBuilderModuleConfigurer urlBuilderModuleConfigurer;

	/** cookieModuleConfigurer used by this servlet　 */
	private CookieModuleConfigurer cookieModuleConfigurer;

	/** aroundProcessPipelines used by this servlet　 */
	private List<AroundPipeline> aroundPipelines;

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
		//
		try {
			String path = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + SUMMERCOOL_RESOURCE_PATTERN;
			Resource[] resources = resourcePatternResolver.getResources(path);
			String[] locations = new String[resources.length];

			for (int i = 0; i < resources.length; i++) {
				locations[i] = resources[i].getURL().toExternalForm();
			}
			//
			wac.setConfigLocations(locations);
		} catch (IOException e) {
			throw new RuntimeException("扫描 summercool framework 配置文件失败!", e);
		}
	}

	@Override
	public Class<XmlWebApplicationContext> getContextClass() {
		return XmlWebApplicationContext.class;
	}

	/**
	 * Initialize the strategy objects that this servlet uses. <p> May be overridden in subclasses in order to
	 * initialize further strategy objects.
	 */
	public void initStrategies(ApplicationContext context) {
		super.initStrategies(context);
		initWebModuleConfigurer(context);
		initSummerCoolHandlerMapping(context);
		initUrlBuilderModuleConfigurer(context);
		initCookieModuleConfigurer(context);
		initAroundProcessPipeline(context);
		initPreProcessPipeline(context);
		initPostProcessPipeline(context);
		initExceptionPipeline(context);
		initRequestAndResponseWrapper(context);
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
		urlPathHelper.setUrlDecode(false);
		webModuleConfigurers = null;
		//
		Map<String, WebModuleConfigurer> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, WebModuleConfigurer.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			webModuleConfigurers = new ArrayList<WebModuleConfigurer>(matchingBeans.values());
		}
	}

	/**
	 * 初始化HandlerMapping
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-4-28
	 * @param applicationContext
	 */
	private void initSummerCoolHandlerMapping(ApplicationContext applicationContext) {
		handlerMapping = applicationContext
				.getBean(SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME, SimpleUrlHandlerMapping.class);
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
			urlBuilderModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
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
			cookieModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
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
		Map<String, AroundPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				AroundPipeline.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			aroundPipelines = new ArrayList<AroundPipeline>(matchingBeans.values());
			Collections.sort(aroundPipelines, new OrderComparator());
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
		//
		if (!matchingBeans.isEmpty()) {
			preProcessPipelines = new ArrayList<PreProcessPipeline>(matchingBeans.values());
			Collections.sort(preProcessPipelines, new OrderComparator());
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
		//
		if (!matchingBeans.isEmpty()) {
			postProcessPipelines = new ArrayList<PostProcessPipeline>(matchingBeans.values());
			Collections.sort(postProcessPipelines, new OrderComparator());
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
		//
		if (!matchingBeans.isEmpty()) {
			exceptionPipelines = new ArrayList<ExceptionPipeline>(matchingBeans.values());
			Collections.sort(exceptionPipelines, new OrderComparator());
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
			HttpServletRequest matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					HttpServletRequest.class, true, false);
			requestClass = (Class<HttpServletRequest>) matchingBean.getClass();
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a RequestClass.
			logger.debug("we do not need a RequestClass", ex);
		}
		try {
			HttpServletResponse matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					HttpServletResponse.class, true, false);
			responseClass = (Class<HttpServletResponse>) matchingBean.getClass();
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a ResponseClass.
			logger.debug("we do not need a ResponseClass", ex);
		}
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
			//
			FreeMarkerViewResolver matchingBean = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					FreeMarkerViewResolver.class, true, false);
			Map<String, Object> attributes = matchingBean.getAttributesMap();
			Map<String, Object> functions = new HashMap<String, Object>();

			// 注册abbreviate()函数
			functions.put("abbreviate", BeanUtils.instantiate(FreeMarkerAbbreviateFunction.class));

			// 注册breakline()函数
			functions.put("breakline", BeanUtils.instantiate(FreeMarkerBreaklineFunction.class));

			// 注册csrf token函数
			functions.put("csrf", BeanUtils.instantiate(FreeMarkerCsrfFunction.class));

			// 注册locale()函数
			functions.put("locale", BeanUtils.instantiate(FreeMarkerLocaleFunction.class));

			// 注册message()函数
			functions.put("message", BeanUtils.instantiate(FreeMarkerMessageFunction.class));

			// 注册param()函数
			functions.put("param", BeanUtils.instantiate(FreeMarkerParamFunction.class));

			// 注册params()函数
			functions.put("params", BeanUtils.instantiate(FreeMarkerParamsFunction.class));

			// 注册theme()函数
			functions.put("theme", BeanUtils.instantiate(FreeMarkerThemeFunction.class));

			// 注册url()函数
			if (urlBuilderModuleConfigurer != null) {
				FreeMarkerUrlFunction freemarkerUrlFunction = BeanUtils.instantiate(FreeMarkerUrlFunction.class);
				freemarkerUrlFunction.setUrlBuilderBeanMap(urlBuilderModuleConfigurer.getUrlBuilderBeanMap());
				functions.put("url", freemarkerUrlFunction);
			}

			// 注册widget()函数
			FreeMarkerWidgetFunction fmWF = BeanUtils.instantiate(FreeMarkerWidgetFunction.class);
			fmWF.setFreemarkerViewResolver((FreeMarkerViewResolver) matchingBean);
			Map<String, FreeMarkerWidget> widgets = fmWF.getWidgetsMap();
			List<WebModuleConfigurer> webMCs = webModuleConfigurers;
			for (WebModuleConfigurer webMC : (webMCs != null) ? webMCs : new ArrayList<WebModuleConfigurer>()) {
				widgets.putAll(webMC.getWidgetMap());
			}
			functions.put("widget", fmWF);

			// 注册widgetForString()函数
			FreeMarkerConfig freeMarkerConfig = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					FreeMarkerConfig.class, true, false);
			FreeMarkerWidgetForStringFunction fmWFSF = BeanUtils.instantiate(FreeMarkerWidgetForStringFunction.class);
			fmWFSF.setConfiguration(freeMarkerConfig.getConfiguration());
			fmWFSF.setFunctions(functions);
			functions.put("widgetFS", fmWFSF);

			// 注册函数结束
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
		//
		if (urlBuilderModuleConfigurer != null) {
			urlBuilderModule = new DefaultUrlBuilderModule(request, urlBuilderModuleConfigurer.getUrlBuilderBeanMap());
		}
		//
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
		//
		if (cookieModuleConfigurer != null) {
			cookieModule = new DefaultCookieModule(cookieModuleConfigurer.getClientName2CfgMap(),
					cookieModuleConfigurer.getName2CfgMap(), request, response);
		}
		//
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
		if (webModuleConfigurers == null || webModuleConfigurers.size() == 0) {
			return viewName;
		}
		//
		String lookupPath = urlPathHelper.getLookupPathForRequest(request);
		lookupPath = org.summercool.web.util.BeanUtils.processUri(lookupPath);
		//
		String bestPathMatch = null;
		String bestPathMatchPackage = null;
		String context = "";
		String contextPackage = "";
		for (WebModuleConfigurer webModuleConfigurer : webModuleConfigurers) {
			context = webModuleConfigurer.getContext();
			contextPackage = webModuleConfigurer.getContextPackage();
			if (lookupPath.startsWith(context) && (bestPathMatch == null || bestPathMatch.length() < context.length())) {
				bestPathMatch = context;
				bestPathMatchPackage = contextPackage;
			}
		}
		//
		if (bestPathMatch != null && bestPathMatchPackage != null) {
			viewName = lookupPath.replaceFirst(bestPathMatch, bestPathMatchPackage
					+ ClassPathControllerAndWidgetScanner.VIEWS_PACKAGE + ClassPathControllerAndWidgetScanner.SLASH);
			int position = viewName.lastIndexOf(".");
			viewName = (position != -1) ? viewName.substring(0, position) : viewName;
		}
		//
		return viewName;
	}

	@Override
	public void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置response默认编码
		response.setCharacterEncoding("UTF-8");
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
		BufferedResponse bufferedResponse = new BufferedResponse(response);
		try {
			ResponseContextHolder.setResponse(response);
			//
			buildUrlBuilderModule(request);
			buildCookieModule(request, response);
			//
			AroundPipelineChain aroundPipelineChain = new AroundPipelineChainHandler(this, aroundPipelines);
			aroundPipelineChain.handleAroundInternal(request, bufferedResponse, aroundPipelineChain);
			//
		} finally {
			response.getOutputStream().write(bufferedResponse.getBufferAsByteArray());
			ResponseContextHolder.resetResponse();
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-28
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void doDispatchInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		int interceptorIndex = -1;
		Exception exception;
		//
		try {
			String viewName = null;
			ModelAndView mv = null;
			boolean errorView = false;
			try {
				if (preProcessPipelines != null) {
					for (PreProcessPipeline preProcessPipeline : preProcessPipelines) {
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
				mappedHandler = getHandler(processedRequest);
				mappedHandler = mappedHandler == null ? handlerMapping.getHandler(processedRequest) : mappedHandler;
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
				if (postProcessPipelines != null) {
					for (PostProcessPipeline postProcessPipeline : postProcessPipelines) {
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
				exception = ex;
				logger.debug("ModelAndViewDefiningException encountered", ex);
				mv = ex.getModelAndView();
			} catch (Exception ex) {
				exception = ex;
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				try {
					mv = processHandlerException(processedRequest, response, handler, ex);
				} catch (Exception e) {
					exception = e;
				}
				errorView = (mv != null);
			}
			if (exceptionPipelines != null) {
				for (ExceptionPipeline exceptionPipeline : exceptionPipelines) {
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
				throw exception;
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

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * 
	 */
	public static class AroundPipelineChainHandler implements AroundPipelineChain {

		private DispatcherServlet dispatcherServlet;

		private List<AroundPipeline> aroundPipelines;

		private int aroundPipelinesCount = 0;

		private int currentAroundPipelineIndex = 0;

		/**
		 * 
		 * @param dispatcherServlet
		 * @param aroundPipelines
		 */
		public AroundPipelineChainHandler(DispatcherServlet dispatcherServlet, List<AroundPipeline> aroundPipelines)
				throws Exception {
			//
			if (dispatcherServlet == null) {
				throw new ServletException("dispatcherServlet不能为空!");
			}
			//
			this.dispatcherServlet = dispatcherServlet;
			this.aroundPipelines = aroundPipelines;
			//
			if (!(aroundPipelines == null || aroundPipelines.size() == 0)) {
				aroundPipelinesCount = aroundPipelines.size();
			}
		}

		/**
		 * 
		 * @author:shaochuan.wangsc
		 * @date:2010-3-28
		 * @param request
		 * @param response
		 * @param aroundPipelineChain
		 * @throws Exception
		 */
		public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,
				AroundPipelineChain aroundPipelineChain) throws Exception {
			//
			if (aroundPipelinesCount == 0) {
				dispatcherServlet.doDispatchInternal(request, response);
				return;
			}
			//
			if (currentAroundPipelineIndex == aroundPipelinesCount) {
				dispatcherServlet.doDispatchInternal(request, response);
				return;
			}
			//
			AroundPipeline aroundPipeline = aroundPipelines.get(currentAroundPipelineIndex);
			currentAroundPipelineIndex = currentAroundPipelineIndex + 1;
			aroundPipeline.handleAroundInternal(request, response, aroundPipelineChain);
		}
	}
}
