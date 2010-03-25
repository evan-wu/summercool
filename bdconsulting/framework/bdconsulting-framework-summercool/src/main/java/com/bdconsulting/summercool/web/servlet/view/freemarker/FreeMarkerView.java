package com.bdconsulting.summercool.web.servlet.view.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-11
 * 
 */
public class FreeMarkerView extends org.springframework.web.servlet.view.freemarker.FreeMarkerView {
	
	private boolean exposeRequestAttributes = false;

	private boolean allowRequestOverride = false;

	private boolean exposeSessionAttributes = false;

	private boolean allowSessionOverride = false;

	private boolean exposeSpringMacroHelpers = true;
	
	private TaglibFactory taglibFactory;

	private ServletContextHashModel servletContextHashModel;

	/**
	 * Set whether all request attributes should be added to the
	 * model prior to merging with the template. Default is "false".
	 */
	public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
		super.setExposeRequestAttributes(exposeRequestAttributes);
		this.exposeRequestAttributes = exposeRequestAttributes;
	}

	/**
	 * Set whether HttpServletRequest attributes are allowed to override (hide)
	 * controller generated model attributes of the same name. Default is "false",
	 * which causes an exception to be thrown if request attributes of the same
	 * name as model attributes are found.
	 */
	public void setAllowRequestOverride(boolean allowRequestOverride) {
		super.setAllowRequestOverride(allowRequestOverride);
		this.allowRequestOverride = allowRequestOverride;
	}

	/**
	 * Set whether all HttpSession attributes should be added to the
	 * model prior to merging with the template. Default is "false".
	 */
	public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
		super.setExposeSessionAttributes(exposeSessionAttributes);
		this.exposeSessionAttributes = exposeSessionAttributes;
	}

	/**
	 * Set whether HttpSession attributes are allowed to override (hide)
	 * controller generated model attributes of the same name. Default is "false",
	 * which causes an exception to be thrown if session attributes of the same
	 * name as model attributes are found.
	 */
	public void setAllowSessionOverride(boolean allowSessionOverride) {
		super.setAllowSessionOverride(allowSessionOverride);
		this.allowSessionOverride = allowSessionOverride;
	}

	/**
	 * Set whether to expose a RequestContext for use by Spring's macro library,
	 * under the name "springMacroRequestContext". Default is "true".
	 * <p>Currently needed for Spring's Velocity and FreeMarker default macros.
	 * Note that this is <i>not</i> required for templates that use HTML
	 * forms <i>unless</i> you wish to take advantage of the Spring helper macros.
	 * @see #SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE
	 */
	public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
		super.setExposeSpringMacroHelpers(exposeSpringMacroHelpers);
		this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
	}
	
	/**
	 * Invoked on startup. Looks for a single FreeMarkerConfig bean to
	 * find the relevant Configuration for this factory.
	 * <p>Checks that the template for the default Locale can be found:
	 * FreeMarker will check non-Locale-specific templates if a
	 * locale-specific one is not found.
	 * @see freemarker.cache.TemplateCache#getTemplate
	 */
	@Override
	protected void initServletContext(ServletContext servletContext) throws BeansException {
		super.initServletContext(servletContext);
		if (getConfiguration() != null) {
			this.taglibFactory = new TaglibFactory(servletContext);
		}
		else {
			FreeMarkerConfig config = autodetectConfiguration();
			setConfiguration(config.getConfiguration());
			this.taglibFactory = config.getTaglibFactory();
		}

		GenericServlet servlet = new GenericServletAdapter();
		try {
			servlet.init(new DelegatingServletConfig());
		}
		catch (ServletException ex) {
			throw new BeanInitializationException("Initialization of GenericServlet adapter failed", ex);
		}
		this.servletContextHashModel = new ServletContextHashModel(servlet, getObjectWrapper());
	}

	@Override
	protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response)
			throws IOException, TemplateException {
		StringWriter stringWriter = new StringWriter();
		Writer writer = response.getWriter();
		//
		template.process(model, stringWriter);
		writer.write(stringWriter.toString());
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-17
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String renderForWidget(HttpServletRequest request, Map<String, ?> model) throws Exception {
		// 第一步开始
		if (logger.isTraceEnabled()) {
			logger.trace("Rendering view with name '" + getBeanName() + "' with model " + model + " and static attributes " + getStaticAttributes());
		}
		// Consolidate static and dynamic model attributes.
		Map<String, Object> mergedModel = new HashMap<String, Object>(getStaticAttributes().size() + (model != null ? model.size() : 0));
		mergedModel.putAll(getStaticAttributes());
		if (model != null) {
			mergedModel.putAll(model);
		}
		// Expose RequestContext?
		if (getRequestContextAttribute() != null) {
			mergedModel.put(getRequestContextAttribute(), createRequestContext(request, null, mergedModel));
		}
		// 第一步结束
		// 第二步开始
		if (this.exposeRequestAttributes) {
			for (Enumeration en = request.getAttributeNames(); en.hasMoreElements();) {
				String attribute = (String) en.nextElement();
				if (mergedModel.containsKey(attribute) && !this.allowRequestOverride) {
					throw new ServletException("Cannot expose request attribute '" + attribute + "' because of an existing model object of the same name");
				}
				Object attributeValue = request.getAttribute(attribute);
				if (logger.isDebugEnabled()) {
					logger.debug("Exposing request attribute '" + attribute + "' with value [" + attributeValue + "] to model");
				}
				mergedModel.put(attribute, attributeValue);
			}
		}

		if (this.exposeSessionAttributes) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				for (Enumeration en = session.getAttributeNames(); en.hasMoreElements();) {
					String attribute = (String) en.nextElement();
					if (mergedModel.containsKey(attribute) && !this.allowSessionOverride) {
						throw new ServletException("Cannot expose session attribute '" + attribute + "' because of an existing model object of the same name");
					}
					Object attributeValue = session.getAttribute(attribute);
					if (logger.isDebugEnabled()) {
						logger.debug("Exposing session attribute '" + attribute + "' with value [" + attributeValue + "] to model");
					}
					mergedModel.put(attribute, attributeValue);
				}
			}
		}

		if (this.exposeSpringMacroHelpers) {
			if (mergedModel.containsKey(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE)) {
				throw new ServletException("Cannot expose bind macro helper '" + SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE + "' because of an existing model object of the same name");
			}
			// Expose RequestContext instance for Spring macros.
			mergedModel.put(SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, new RequestContext(request, null, getServletContext(), mergedModel));
		}
		// 第二步结束
		// 第三步开始
		exposeHelpers(mergedModel, request);
		// Expose model to JSP tags (as request attributes).
		exposeModelAsRequestAttributes(mergedModel, request);
		// Expose all standard FreeMarker hash models.
		AllHttpScopesHashModel fmModel = new AllHttpScopesHashModel(getObjectWrapper(), getServletContext(), request);
		fmModel.put(FreemarkerServlet.KEY_JSP_TAGLIBS, this.taglibFactory);
		fmModel.put(FreemarkerServlet.KEY_APPLICATION, this.servletContextHashModel);
		fmModel.put(FreemarkerServlet.KEY_SESSION, buildSessionModel(request, null));
		fmModel.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(request, null, getObjectWrapper()));
		fmModel.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS, new HttpRequestParametersHashModel(request));
		fmModel.putAll(mergedModel);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Rendering FreeMarker template [" + getUrl() + "] in FreeMarkerView '" + getBeanName() + "'");
		}
		// 第三步结束
		// 第四步开始
		Locale locale = RequestContextUtils.getLocale(request);
		StringWriter stringWriter = new StringWriter();
		getTemplate(locale).process(fmModel, stringWriter);
		// 第四步结束
		
		return stringWriter.toString();
	}
	
	/**
	 * Build a FreeMarker {@link HttpSessionHashModel} for the given request,
	 * detecting whether a session already exists and reacting accordingly.
	 * @param request current HTTP request
	 * @param response current servlet response
	 * @return the FreeMarker HttpSessionHashModel
	 */
	private HttpSessionHashModel buildSessionModel(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return new HttpSessionHashModel(session, getObjectWrapper());
		}
		else {
			return new HttpSessionHashModel(null, request, response, getObjectWrapper());
		}
	}
	
	/**
	 * Simple adapter class that extends {@link GenericServlet}.
	 * Needed for JSP access in FreeMarker.
	 */
	private static class GenericServletAdapter extends GenericServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3742829335451205034L;

		@Override
		public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
			// no-op
		}
	}

	/**
	 * Internal implementation of the {@link ServletConfig} interface,
	 * to be passed to the servlet adapter.
	 */
	private class DelegatingServletConfig implements ServletConfig {

		public String getServletName() {
			return FreeMarkerView.this.getBeanName();
		}

		public ServletContext getServletContext() {
			return FreeMarkerView.this.getServletContext();
		}

		public String getInitParameter(String paramName) {
			return null;
		}

		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(new HashSet<String>());
		}
	}
}
