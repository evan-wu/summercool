package org.summercool.web.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
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
import org.summercool.web.module.WebModuleUriExtensionConfigurer;
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

public class DispatcherFilter extends OncePerRequestFilter {

	public static final String DEFAULT_NAMESPACE_SUFFIX = "-filter";

	public static final Class<XmlWebApplicationContext> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

	public static final String FILTER_CONTEXT_PREFIX = DispatcherFilter.class.getName() + ".CONTEXT.";

	public static final String FILTER_NAME = DispatcherFilter.class.getName();

	public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";

	public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";

	public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";

	public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

	public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";

	public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";

	public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";

	public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";

	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";

	public static final String LOCALE_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";

	public static final String THEME_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_RESOLVER";

	public static final String THEME_SOURCE_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_SOURCE";

	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";

	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

	private static final Properties defaultStrategies;

	private static final String SUMMERCOOL_RESOURCE_PATTERN = "/summercool/spring/*.xml";

	public static final String SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME = "summercoolHandlerMapping";

	static {
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherServlet.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + ex.getMessage());
		}
	}

	private String contextAttribute;

	private Class<XmlWebApplicationContext> contextClass = DEFAULT_CONTEXT_CLASS;

	private String namespace;

	private String contextConfigLocation;

	private boolean publishContext = true;

	private boolean publishEvents = true;

	private boolean threadContextInheritable = false;

	private WebApplicationContext webApplicationContext;

	private boolean refreshEventReceived = false;

	private boolean detectAllHandlerMappings = true;

	private boolean detectAllHandlerAdapters = true;

	private boolean detectAllHandlerExceptionResolvers = true;

	private boolean detectAllViewResolvers = true;

	private boolean cleanupAfterInclude = true;

	private MultipartResolver multipartResolver;

	private LocaleResolver localeResolver;

	private ThemeResolver themeResolver;

	private List<HandlerMapping> handlerMappings;

	private List<HandlerAdapter> handlerAdapters;

	private List<HandlerExceptionResolver> handlerExceptionResolvers;

	private RequestToViewNameTranslator viewNameTranslator;

	private List<ViewResolver> viewResolvers;

	private HandlerMapping handlerMapping;

	private List<WebModuleConfigurer> webModuleConfigurers;

	private WebModuleUriExtensionConfigurer webModuleUriExtensionConfigurer;

	private UrlBuilderModuleConfigurer urlBuilderModuleConfigurer;

	private CookieModuleConfigurer cookieModuleConfigurer;

	private List<AroundPipeline> aroundPipelines;

	private List<PreProcessPipeline> preProcessPipelines;

	private List<PostProcessPipeline> postProcessPipelines;

	private List<ExceptionPipeline> exceptionPipelines;

	private Class<HttpServletRequest> requestClass;

	private Class<HttpServletResponse> responseClass;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}

	public String getContextAttribute() {
		return (this.contextAttribute != null ? this.contextAttribute : getFilterName() + DEFAULT_NAMESPACE_SUFFIX);
	}

	public void setContextClass(Class<XmlWebApplicationContext> contextClass) {
		this.contextClass = contextClass;
	}

	public Class<XmlWebApplicationContext> getContextClass() {
		return contextClass;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return (this.namespace != null ? this.namespace : getFilterName() + DEFAULT_NAMESPACE_SUFFIX);
	}

	public void setContextConfigLocation(String contextConfigLocation) {
		this.contextConfigLocation = contextConfigLocation;
	}

	public String getContextConfigLocation() {
		return this.contextConfigLocation;
	}

	public void setPublishContext(boolean publishContext) {
		this.publishContext = publishContext;
	}

	public void setPublishEvents(boolean publishEvents) {
		this.publishEvents = publishEvents;
	}

	public void setThreadContextInheritable(boolean threadContextInheritable) {
		this.threadContextInheritable = threadContextInheritable;
	}

	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		this.detectAllHandlerMappings = detectAllHandlerMappings;
	}

	public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
		this.detectAllHandlerAdapters = detectAllHandlerAdapters;
	}

	public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
		this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
	}

	public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
		this.detectAllViewResolvers = detectAllViewResolvers;
	}

	public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
		this.cleanupAfterInclude = cleanupAfterInclude;
	}

	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		getServletContext().log("Initializing Spring FrameworkFilter '" + getFilterName() + "'");
		if (this.logger.isInfoEnabled()) {
			this.logger.info("FrameworkFilter '" + getFilterName() + "': initialization started");
		}
		long startTime = System.currentTimeMillis();

		try {
			this.webApplicationContext = initWebApplicationContext();
			initFrameworkServlet();
		} catch (ServletException ex) {
			this.logger.error("Context initialization failed", ex);
			throw ex;
		} catch (RuntimeException ex) {
			this.logger.error("Context initialization failed", ex);
			throw ex;
		}

		if (this.logger.isInfoEnabled()) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			this.logger.info("FrameworkFilter '" + getFilterName() + "': initialization completed in " + elapsedTime + " ms");
		}
	}

	protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext wac = findWebApplicationContext();
		if (wac == null) {
			// No fixed context defined for this filter - create a local one.
			WebApplicationContext parent = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			wac = createWebApplicationContext(parent);
		}

		if (!this.refreshEventReceived) {
			// Apparently not a ConfigurableApplicationContext with refresh
			// support:
			// triggering initial onRefresh manually here.
			onRefresh(wac);
		}

		if (this.publishContext) {
			// Publish the context as a servlet context attribute.
			String attrName = getContextAttribute();
			getServletContext().setAttribute(attrName, wac);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Published WebApplicationContext of filter '" + getFilterName()
						+ "' as ServletContext attribute with name [" + attrName + "]");
			}
		}
		
		return wac;
	}

	protected WebApplicationContext findWebApplicationContext() {
		String attrName = getContextAttribute();
		if (attrName == null) {
			return null;
		}
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
//		if (wac == null) {
//			throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
//		}
		return wac;
	}

	protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
		Class<XmlWebApplicationContext> contextClass = getContextClass();
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Filter with name '" + getFilterName()
					+ "' will try to create custom WebApplicationContext context of class '" + contextClass.getName()
					+ "'" + ", using parent context [" + parent + "]");
		}
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Fatal initialization error in filter with name '" + getFilterName()
					+ "': custom WebApplicationContext class [" + contextClass.getName()
					+ "] is not of type ConfigurableWebApplicationContext");
		}
		ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext) BeanUtils
				.instantiateClass(contextClass);

		// Assign the best possible id value.
		ServletContext sc = getServletContext();
		if (sc.getMajorVersion() == 2 && sc.getMinorVersion() < 5) {
			// Servlet <= 2.4: resort to name specified in web.xml, if any.
			String servletContextName = sc.getServletContextName();
			if (servletContextName != null) {
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + servletContextName 
						+ "." + getFilterName());
			} else {
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + getFilterName());
			}
		} else {
			// Servlet 2.5's getContextPath available!
			wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + sc.getContextPath() 
					+ "/" + getFilterName());
		}

		wac.setParent(parent);
		wac.setServletContext(getServletContext());
		// wac.setServletConfig(getServletConfig());
		wac.setNamespace(getNamespace());
		wac.setConfigLocation(getContextConfigLocation());
		wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));

		postProcessWebApplicationContext(wac);
		wac.refresh();

		return wac;
	}

	protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {
		return createWebApplicationContext((ApplicationContext) parent);
	}

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

	public String getFilterContextAttributeName() {
		return FILTER_CONTEXT_PREFIX + getFilterName();
	}

	public final WebApplicationContext getWebApplicationContext() {
		return this.webApplicationContext;
	}

	protected void initFrameworkServlet() throws ServletException {
	}

	public void refresh() {
		WebApplicationContext wac = getWebApplicationContext();
		if (!(wac instanceof ConfigurableApplicationContext)) {
			throw new IllegalStateException("WebApplicationContext does not support refresh: " + wac);
		}
		((ConfigurableApplicationContext) wac).refresh();
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.refreshEventReceived = true;
		onRefresh(event.getApplicationContext());
	}

	protected void onRefresh(ApplicationContext context) {
		initStrategies(context);
	}

	protected final void processRequest(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		long startTime = System.currentTimeMillis();
		Throwable failureCause = null;

		// Expose current LocaleResolver and request as LocaleContext.
		LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
		LocaleContextHolder.setLocaleContext(buildLocaleContext(request), this.threadContextInheritable);

		// Expose current RequestAttributes to current thread.
		RequestAttributes previousRequestAttributes = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes requestAttributes = null;
		if (previousRequestAttributes == null || previousRequestAttributes.getClass().equals(ServletRequestAttributes.class)) {
			requestAttributes = new ServletRequestAttributes(request);
			RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Bound request context to thread: " + request);
		}

		try {
			doService(request, response);
			if (request.getAttribute(FILTER_NAME + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX) == null) {
				filterChain.doFilter(request, response);
			}
		} catch (ServletException ex) {
			failureCause = ex;
			throw ex;
		} catch (IOException ex) {
			failureCause = ex;
			throw ex;
		} catch (Throwable ex) {
			failureCause = ex;
			throw new NestedServletException("Request processing failed", ex);
		}

		finally {
			// Clear request attributes and reset thread-bound context.
			LocaleContextHolder.setLocaleContext(previousLocaleContext, this.threadContextInheritable);
			if (requestAttributes != null) {
				RequestContextHolder.setRequestAttributes(previousRequestAttributes, this.threadContextInheritable);
				requestAttributes.requestCompleted();
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Cleared thread-bound request context: " + request);
			}

			if (failureCause != null) {
				this.logger.debug("Could not complete request", failureCause);
			} else {
				this.logger.debug("Successfully completed request");
			}
			if (this.publishEvents) {
				// Whether or not we succeeded, publish an event.
				long processingTime = System.currentTimeMillis() - startTime;
				this.webApplicationContext.publishEvent(new ServletRequestHandledEvent(this, request.getRequestURI(),
						request.getRemoteAddr(), request.getMethod(), getFilterName(), WebUtils.getSessionId(request),
						getUsernameForRequest(request), processingTime, failureCause));
			}
		}
	}

	protected LocaleContext buildLocaleContext(final HttpServletRequest request) {
		return new LocaleContext() {
			public Locale getLocale() {
				return localeResolver.resolveLocale(request);
			}

			@Override
			public String toString() {
				return getLocale().toString();
			}
		};
	}

	protected String getUsernameForRequest(HttpServletRequest request) {
		Principal userPrincipal = request.getUserPrincipal();
		return (userPrincipal != null ? userPrincipal.getName() : null);
	}

	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (logger.isDebugEnabled()) {
			String requestUri = urlPathHelper.getRequestUri(request);
			logger.debug("DispatcherFilter with name '" + getFilterName() + "' processing " + request.getMethod()
					          + " request for [" + requestUri + "]");
		}

		// Keep a snapshot of the request attributes in case of an include,
		// to be able to restore the original attributes after the include.
		Map<String, Object> attributesSnapshot = null;
		if (WebUtils.isIncludeRequest(request)) {
			logger.debug("Taking snapshot of request attributes before include");
			attributesSnapshot = new HashMap<String, Object>();
			Enumeration<?> attrNames = request.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = (String) attrNames.nextElement();
				if (this.cleanupAfterInclude || attrName.startsWith("org.springframework.web.servlet")) {
					attributesSnapshot.put(attrName, request.getAttribute(attrName));
				}
			}
		}

		// Make framework objects available to handlers and view objects.
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
		request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
		request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
		request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

		try {
			doDispatch(request, response);
		} finally {
			// Restore the original attribute snapshot, in case of an include.
			if (attributesSnapshot != null) {
				restoreAttributesAfterInclude(request, attributesSnapshot);
			}
		}
	}

	protected void initStrategies(ApplicationContext context) {
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolvers(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		//
		initWebModuleConfigurer(context);
		initWebModuleUriExtensionConfigurer(context);
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

	private void initMultipartResolver(ApplicationContext context) {
		try {
			this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using MultipartResolver [" + this.multipartResolver + "]");
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// Default is no multipart resolver.
			this.multipartResolver = null;
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate MultipartResolver with name '" + MULTIPART_RESOLVER_BEAN_NAME
								 + "': no multipart request handling provided");
			}
		}
	}

	private void initLocaleResolver(ApplicationContext context) {
		try {
			this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using LocaleResolver [" + this.localeResolver + "]");
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate LocaleResolver with name '" + LOCALE_RESOLVER_BEAN_NAME
								 + "': using default [" + this.localeResolver + "]");
			}
		}
	}

	private void initThemeResolver(ApplicationContext context) {
		try {
			this.themeResolver = context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using ThemeResolver [" + this.themeResolver + "]");
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.themeResolver = getDefaultStrategy(context, ThemeResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate ThemeResolver with name '" + THEME_RESOLVER_BEAN_NAME
							     + "': using default [" + this.themeResolver + "]");
			}
		}
	}

	private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;

		if (this.detectAllHandlerMappings) {
			// Find all HandlerMappings in the ApplicationContext, including
			// ancestor contexts.
			Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
					HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
				// We keep HandlerMappings in sorted order.
				OrderComparator.sort(this.handlerMappings);
			}
		} else {
			try {
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerMappings found in filter '" + getFilterName() + "': using default");
			}
		}
	}

	private void initHandlerAdapters(ApplicationContext context) {
		this.handlerAdapters = null;

		if (this.detectAllHandlerAdapters) {
			// Find all HandlerAdapters in the ApplicationContext, including
			// ancestor contexts.
			Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
					HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerAdapters = new ArrayList<HandlerAdapter>(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				OrderComparator.sort(this.handlerAdapters);
			}
		} else {
			try {
				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				this.handlerAdapters = Collections.singletonList(ha);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// Ensure we have at least some HandlerAdapters, by registering
		// default HandlerAdapters if no other adapters are found.
		if (this.handlerAdapters == null) {
			this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerAdapters found in filter '" + getFilterName() + "': using default");
			}
		}
	}

	private void initHandlerExceptionResolvers(ApplicationContext context) {
		this.handlerExceptionResolvers = null;

		if (this.detectAllHandlerExceptionResolvers) {
			// Find all HandlerExceptionResolvers in the ApplicationContext,
			// including ancestor contexts.
			Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					context, HandlerExceptionResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>(matchingBeans.values());
				// We keep HandlerExceptionResolvers in sorted order.
				OrderComparator.sort(this.handlerExceptionResolvers);
			}
		} else {
			try {
				HandlerExceptionResolver her = context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME,
						HandlerExceptionResolver.class);
				this.handlerExceptionResolvers = Collections.singletonList(her);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, no HandlerExceptionResolver is fine too.
			}
		}

		// Ensure we have at least some HandlerExceptionResolvers, by
		// registering
		// default HandlerExceptionResolvers if no other resolvers are found.
		if (this.handlerExceptionResolvers == null) {
			this.handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No HandlerExceptionResolvers found in filter '" + getFilterName() + "': using default");
			}
		}
	}

	private void initRequestToViewNameTranslator(ApplicationContext context) {
		try {
			this.viewNameTranslator = context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME,
					RequestToViewNameTranslator.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using RequestToViewNameTranslator [" + this.viewNameTranslator + "]");
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			this.viewNameTranslator = getDefaultStrategy(context, RequestToViewNameTranslator.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate RequestToViewNameTranslator with name '"
						+ REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME + "': using default [" + this.viewNameTranslator + "]");
			}
		}
	}

	private void initViewResolvers(ApplicationContext context) {
		this.viewResolvers = null;

		if (this.detectAllViewResolvers) {
			// Find all ViewResolvers in the ApplicationContext, including
			// ancestor contexts.
			Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
					ViewResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
				// We keep ViewResolvers in sorted order.
				OrderComparator.sort(this.viewResolvers);
			}
		} else {
			try {
				ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
				this.viewResolvers = Collections.singletonList(vr);
			} catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default ViewResolver later.
			}
		}

		// Ensure we have at least one ViewResolver, by registering
		// a default ViewResolver if no other resolvers are found.
		if (this.viewResolvers == null) {
			this.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
			if (logger.isDebugEnabled()) {
				logger.debug("No ViewResolvers found in filter '" + getFilterName() + "': using default");
			}
		}
	}

	public final ThemeSource getThemeSource() {
		if (getWebApplicationContext() instanceof ThemeSource) {
			return (ThemeSource) getWebApplicationContext();
		} else {
			return null;
		}
	}

	public final MultipartResolver getMultipartResolver() {
		return this.multipartResolver;
	}

	protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
		List<T> strategies = getDefaultStrategies(context, strategyInterface);
		if (strategies.size() != 1) {
			throw new BeanInitializationException("DispatcherServlet needs exactly 1 strategy for interface ["
					+ strategyInterface.getName() + "]");
		}
		return strategies.get(0);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
		String key = strategyInterface.getName();
		String value = defaultStrategies.getProperty(key);
		if (value != null) {
			String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
			List<T> strategies = new ArrayList<T>(classNames.length);
			for (String className : classNames) {
				try {
					Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
					Object strategy = createDefaultStrategy(context, clazz);
					strategies.add((T) strategy);
				} catch (ClassNotFoundException ex) {
					throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class ["
							+ className + "] for interface [" + key + "]", ex);
				} catch (LinkageError err) {
					throw new BeanInitializationException("Error loading DispatcherServlet's default strategy class ["
							+ className + "] for interface [" + key + "]: problem with class file or dependent class",
							err);
				}
			}
			return strategies;
		} else {
			return new LinkedList<T>();
		}
	}

	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
		return context.getAutowireCapableBeanFactory().createBean(clazz);
	}

	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
		if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
			if (request instanceof MultipartHttpServletRequest) {
				logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, "
						+ "this typically results from an additional MultipartFilter in web.xml");
			} else {
				return this.multipartResolver.resolveMultipart(request);
			}
		}
		// If not returned before: return original request.
		return request;
	}

	protected void cleanupMultipart(HttpServletRequest request) {
		if (request instanceof MultipartHttpServletRequest) {
			this.multipartResolver.cleanupMultipart((MultipartHttpServletRequest) request);
		}
	}

	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		for (HandlerMapping hm : this.handlerMappings) {
			if (logger.isTraceEnabled()) {
				logger.trace("Testing handler map [" + hm + "] in DispatcherFilter with name '" + getFilterName() + "'");
			}
			HandlerExecutionChain handler = hm.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		for (HandlerAdapter ha : this.handlerAdapters) {
			if (logger.isTraceEnabled()) {
				logger.trace("Testing handler adapter [" + ha + "]");
			}
			if (ha.supports(handler)) {
				return ha;
			}
		}
		throw new ServletException("No adapter for handler [" + handler
				+ "]: Does your handler implement a supported interface like Controller?");
	}

	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {

		// Check registered HandlerExceptionResolvers...
		ModelAndView exMv = null;
		for (HandlerExceptionResolver handlerExceptionResolver : this.handlerExceptionResolvers) {
			exMv = handlerExceptionResolver.resolveException(request, response, handler, ex);
			if (exMv != null) {
				break;
			}
		}
		if (exMv != null) {
			if (exMv.isEmpty()) {
				return null;
			}
			// We might still need view name translation for a plain error
			// model...
			if (!exMv.hasView()) {
				exMv.setViewName(getDefaultViewName(request));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Handler execution resulted in exception - forwarding to resolved error view: " + exMv, ex);
			}
			WebUtils.exposeErrorRequestAttributes(request, ex, getFilterName());
			return exMv;
		}

		throw ex;
	}

	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Determine locale for request and apply it to the response.
		Locale locale = this.localeResolver.resolveLocale(request);
		response.setLocale(locale);

		View view;
		if (mv.isReference()) {
			// We need to resolve the view name.
			view = resolveViewName(mv.getViewName(), mv.getModel(), locale, request);
			if (view == null) {
				throw new ServletException("Could not resolve view with name '" + mv.getViewName()
						+ "' in servlet with name '" + getFilterName() + "'");
			}
		} else {
			// No need to lookup: the ModelAndView object contains the actual
			// View object.
			view = mv.getView();
			if (view == null) {
				throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a "
						+ "View object in filter with name '" + getFilterName() + "'");
			}
		}

		// Delegate to the View object for rendering.
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering view [" + view + "] in DispatcherFilter with name '" + getFilterName() + "'");
		}
		view.render(mv.getModel(), request, response);
	}

	protected String getDefaultViewName(HttpServletRequest request) throws Exception {
		return this.viewNameTranslator.getViewName(request);
	}

	protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
			throws Exception {

		for (ViewResolver viewResolver : this.viewResolvers) {
			View view = viewResolver.resolveViewName(viewName, locale);
			if (view != null) {
				return view;
			}
		}
		return null;
	}

	private void restoreAttributesAfterInclude(HttpServletRequest request, Map<String, Object> attributesSnapshot) {
		logger.debug("Restoring snapshot of request attributes after include");

		// Need to copy into separate Collection here, to avoid side effects
		// on the Enumeration when removing attributes.
		Set<String> attrsToCheck = new HashSet<String>();
		Enumeration<?> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			if (this.cleanupAfterInclude || attrName.startsWith("org.springframework.web.servlet")) {
				attrsToCheck.add(attrName);
			}
		}

		// Iterate over the attributes to check, restoring the original value
		// or removing the attribute, respectively, if appropriate.
		for (String attrName : attrsToCheck) {
			Object attrValue = attributesSnapshot.get(attrName);
			if (attrValue == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Removing attribute [" + attrName + "] after include");
				}
				request.removeAttribute(attrName);
			} else if (attrValue != request.getAttribute(attrName)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Restoring original value of attribute [" + attrName + "] after include");
				}
				request.setAttribute(attrName, attrValue);
			}
		}
	}

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

	private void initWebModuleUriExtensionConfigurer(ApplicationContext applicationContext) {
		//
		webModuleUriExtensionConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
				WebModuleUriExtensionConfigurer.class, true, false);
	}

	private void initSummerCoolHandlerMapping(ApplicationContext applicationContext) {
		handlerMapping = applicationContext
				.getBean(SUMMERCOOL_HANDLER_MAPPING_BEAN_NAME, SimpleUrlHandlerMapping.class);
	}

	private void initUrlBuilderModuleConfigurer(ApplicationContext applicationContext) {
		try {
			urlBuilderModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					UrlBuilderModuleConfigurer.class, true, false);
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a UrlBuilderModuleConfigurer.
			logger.debug("we do not need a UrlBuilderModuleConfigurer", ex);
		}

	}

	private void initCookieModuleConfigurer(ApplicationContext applicationContext) {
		try {
			cookieModuleConfigurer = BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext,
					CookieModuleConfigurer.class, true, false);
		} catch (NoSuchBeanDefinitionException ex) {
			// Ignore, we do not need a CookieModuleConfigurer.
			logger.debug("we do not need a CookieModuleConfigurer", ex);
		}

	}

	private void initAroundProcessPipeline(ApplicationContext applicationContext) {
		Map<String, AroundPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				AroundPipeline.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			aroundPipelines = new ArrayList<AroundPipeline>(matchingBeans.values());
			Collections.sort(aroundPipelines, new OrderComparator());
		}
	}

	private void initPreProcessPipeline(ApplicationContext applicationContext) {
		Map<String, PreProcessPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, PreProcessPipeline.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			preProcessPipelines = new ArrayList<PreProcessPipeline>(matchingBeans.values());
			Collections.sort(preProcessPipelines, new OrderComparator());
		}
	}

	private void initPostProcessPipeline(ApplicationContext applicationContext) {
		Map<String, PostProcessPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, PostProcessPipeline.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			postProcessPipelines = new ArrayList<PostProcessPipeline>(matchingBeans.values());
			Collections.sort(postProcessPipelines, new OrderComparator());
		}
	}

	private void initExceptionPipeline(ApplicationContext applicationContext) {
		Map<String, ExceptionPipeline> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, ExceptionPipeline.class, true, false);
		//
		if (!matchingBeans.isEmpty()) {
			exceptionPipelines = new ArrayList<ExceptionPipeline>(matchingBeans.values());
			Collections.sort(exceptionPipelines, new OrderComparator());
		}
	}

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

	private void buildUrlBuilderModule(HttpServletRequest request) {
		UrlBuilderModule urlBuilderModule = null;
		//
		if (urlBuilderModuleConfigurer != null) {
			urlBuilderModule = new DefaultUrlBuilderModule(request, urlBuilderModuleConfigurer.getUrlBuilderBeanMap());
		}
		//
		request.setAttribute(UrlBuilderModule.URL_BUILDER, urlBuilderModule);
	}

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

	private String requestToViewName(HttpServletRequest request) {
		String viewName = null;
		if (webModuleConfigurers == null || webModuleConfigurers.size() == 0) {
			return viewName;
		}
		//
		String lookupPath = urlPathHelper.getLookupPathForRequest(request);
		String srcLookupPath = new String(lookupPath);
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
		if ("/".equals(srcLookupPath)) {
			return viewName + "index";
		} else {
			return viewName;
		}
	}

	@Override
	public void destroy() {
		getServletContext().log("Destroying Spring FrameworkFilter '" + getFilterName() + "'");
		if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) this.webApplicationContext).close();
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		processRequest(request, response, filterChain);
	}

	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			ResponseContextHolder.resetResponse();
			if (request.getAttribute(FILTER_NAME + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX) != null) {
				byte[] byteArray = bufferedResponse.getBufferAsByteArray();
				if (byteArray != null && byteArray.length > 0) {
					response.getOutputStream().write(bufferedResponse.getBufferAsByteArray());
				}
			}
		}

	}

	public void doDispatchInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		int interceptorIndex = -1;
		Exception exception;
		//
		try {
			String requestUri = urlPathHelper.getRequestUri(request);
			if ("/".equals(requestUri)) {
				// do nothing;
			} else {
				boolean lookedup = false;
				List<String> uriExtensions = webModuleUriExtensionConfigurer.getUriExtensions();
				for (String uriExtension : uriExtensions) {
					if (requestUri.endsWith(uriExtension)) {
						lookedup = true;
						break;
					}
				}
				if (!lookedup) {
					return;
				}
			}
			//
			request.setAttribute(FILTER_NAME + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX, Boolean.TRUE);
			//
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
					logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getFilterName()
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

	private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
		public void onApplicationEvent(ContextRefreshedEvent event) {
			DispatcherFilter.this.onApplicationEvent(event);
		}
	}

	public static class AroundPipelineChainHandler implements AroundPipelineChain {

		private DispatcherFilter dispatcherFilter;

		private List<AroundPipeline> aroundPipelines;

		private int aroundPipelinesCount = 0;

		private int currentAroundPipelineIndex = 0;

		/**
		 * 
		 * @param dispatcherServlet
		 * @param aroundPipelines
		 */
		public AroundPipelineChainHandler(DispatcherFilter dispatcherFilter, List<AroundPipeline> aroundPipelines)
				throws Exception {
			//
			if (dispatcherFilter == null) {
				throw new ServletException("dispatcherFilter不能为空!");
			}
			//
			this.dispatcherFilter = dispatcherFilter;
			this.aroundPipelines = aroundPipelines;
			//
			if (!(aroundPipelines == null || aroundPipelines.size() == 0)) {
				aroundPipelinesCount = aroundPipelines.size();
			}
		}

		public void handleAroundInternal(HttpServletRequest request, HttpServletResponse response,
				AroundPipelineChain aroundPipelineChain) throws Exception {
			//
			if (aroundPipelinesCount == 0) {
				dispatcherFilter.doDispatchInternal(request, response);
				return;
			}
			//
			if (currentAroundPipelineIndex == aroundPipelinesCount) {
				dispatcherFilter.doDispatchInternal(request, response);
				return;
			}
			//
			AroundPipeline aroundPipeline = aroundPipelines.get(currentAroundPipelineIndex);
			currentAroundPipelineIndex = currentAroundPipelineIndex + 1;
			aroundPipeline.handleAroundInternal(request, response, aroundPipelineChain);
		}
	}

}
