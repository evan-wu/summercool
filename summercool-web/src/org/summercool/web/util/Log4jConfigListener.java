package org.summercool.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;
import org.summercool.util.SystemPropertiesUtil;

public class Log4jConfigListener extends org.springframework.web.util.Log4jConfigListener {

	private static final String LOG4j_PROPERTIES_PARAM = "log4jProperties";

	private static final String DEFAULT_LOG4j_PROPERTIES = "log.properties";

	public void contextInitialized(ServletContextEvent event) {

		ServletContext servletContext = event.getServletContext();

		Properties properties = getProperties(servletContext);

		/**
		 * 根据 properties 中设置的 key value 值设置系统环境变量以供 log4j 配置文件使用
		 */
		Map<String, String> map = SystemPropertiesUtil.setSystemProperties(properties);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			servletContext.log("Set property: '" + entry.getKey() + "' = [" + entry.getValue() + "]");
		}
		super.contextInitialized(event);
	}

	/**
	 * 获取 log4j 的 properties 文件中配置的properties
	 * 
	 * @param servletContext
	 * @return
	 */
	private Properties getProperties(ServletContext servletContext) {

		String param = servletContext.getInitParameter(LOG4j_PROPERTIES_PARAM);
		String location = (param != null ? param : DEFAULT_LOG4j_PROPERTIES);

		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			// 支持Spring文件注入方式
			if (!ResourceUtils.isUrl(location)) {
				location = SystemPropertyUtils.resolvePlaceholders(location);
				location = WebUtils.getRealPath(servletContext, location);
			}

			File file = ResourceUtils.getFile(location);

			if (!file.exists()) {
				if (param != null) {
					throw new FileNotFoundException("Log properties file [" + location + "] not found");
				}
			} else {
				inputStream = new FileInputStream(file);
				properties.load(inputStream);
				PlaceHolderUtils.convertProperties(properties);
			}
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("Invalid 'log4jProperties' parameter:" + ex.getMessage());
		} catch (IOException ex) {
			throw new IllegalArgumentException("Invalid 'log4jProperties' parameter:" + ex.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}

		return properties;
	}

	public void contextDestroyed(ServletContextEvent event) {

		super.contextDestroyed(event);
		Properties properties = getProperties(event.getServletContext());
		SystemPropertiesUtil.removeSystemProperties(properties);

	}
}
