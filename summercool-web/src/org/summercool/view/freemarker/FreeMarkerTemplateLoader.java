package org.summercool.view.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import freemarker.cache.TemplateLoader;

public class FreeMarkerTemplateLoader implements TemplateLoader {

	protected final Log logger = LogFactory.getLog(getClass());

	private final ResourceLoader resourceLoader;

	private final String templateLoaderPath;

	/**
	 * Create a new SpringTemplateLoader.
	 * 
	 * @param resourceLoader the Spring ResourceLoader to use
	 * @param templateLoaderPath the template loader path to use
	 */
	public FreeMarkerTemplateLoader(ResourceLoader resourceLoader, String templateLoaderPath) {
		this.resourceLoader = resourceLoader;
		if (!templateLoaderPath.endsWith("/")) {
			templateLoaderPath += "/";
		}
		this.templateLoaderPath = templateLoaderPath;
		if (logger.isInfoEnabled()) {
			logger.info("SpringTemplateLoader for FreeMarker: using resource loader [" + this.resourceLoader
					+ "] and template loader path [" + this.templateLoaderPath + "]");
		}
	}

	public Object findTemplateSource(String name) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for FreeMarker template with name [" + name + "]");
		}
		Resource resource = this.resourceLoader.getResource(this.templateLoaderPath + name);
		return (resource.exists() ? resource : null);
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Resource resource = (Resource) templateSource;
		try {
			return new InputStreamReader(resource.getInputStream(), encoding);
		} catch (IOException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not find FreeMarker template: " + resource);
			}
			throw ex;
		}
	}

	public long getLastModified(Object templateSource) {
		try {
			return ((Resource) templateSource).lastModified();
		} catch (IOException e) {
			return -1;
		}
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
	}

}
