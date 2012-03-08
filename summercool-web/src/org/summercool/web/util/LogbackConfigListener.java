package org.summercool.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;
import org.summercool.util.StackTraceUtil;
import org.summercool.util.SystemPropertiesUtil;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * @Title: LogbackConfigListener.java
 * @Package com.gexin.platform.biz.common.logback
 * @Description: Logback配置监听类
 * @author Yanjh
 * @date 2011-8-17 上午10:21:51
 * @version V1.0
 */
public class LogbackConfigListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(LogbackConfigListener.class);

	private static final String CONFIG_LOCATION = "logbackConfigLocation";
	private static final String LOGBACK_PROPERITES_PARAM = "logbackProperties";
	private static final String CONFIG_REFRESH_INTERVAL = "configRefreshInterval";
	private static final String REFRESH_SPRING_CONTEXT = "refreshSpringContext";

	private String configMD5;
	private String propMD5;
	private Timer timer = new java.util.Timer(true);

	static java.security.MessageDigest message = null;
	// 用来将字节转换成 16 进制表示的字符
	static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	static {
		try {
			message = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logger.error("初始化java.security.MessageDigest失败:" + StackTraceUtil.getStackTrace(e), e);
		}
	}

	public void contextInitialized(final ServletContextEvent event) {
		// 从web.xml中加载指定文件名的日志配置文件
		final ServletContext servletContext = event.getServletContext();
		// 设置系统参数
		Properties properties = getPropertiesParam(servletContext);
		SystemPropertiesUtil.setSystemProperties(properties);

		// 载入logback配置
		String logbackConfigLocation = servletContext.getInitParameter(CONFIG_LOCATION);
		String fn = servletContext.getRealPath(logbackConfigLocation);

		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			loggerContext.reset();

			JoranConfigurator joranConfigurator = new JoranConfigurator();
			joranConfigurator.setContext(loggerContext);
			joranConfigurator.doConfigure(fn);

			if (logger.isDebugEnabled()) {
				logger.debug("loaded slf4j configure file from {}", fn);
			}

			long refreshInterval = 60000;
			String strInterval = servletContext.getInitParameter(CONFIG_REFRESH_INTERVAL);
			try {
				if (strInterval != null) {
					refreshInterval = Long.parseLong(servletContext.getInitParameter(CONFIG_REFRESH_INTERVAL));
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn(CONFIG_REFRESH_INTERVAL + " is not set. use default value " + refreshInterval);
					}
				}
			} catch (Exception e) {
				logger.error("parse " + CONFIG_REFRESH_INTERVAL + " failed. value " + strInterval + " is illegal." + e.getMessage(), e);
			}

			boolean refreshCtx = true;
			String strRefreshCtx = servletContext.getInitParameter(REFRESH_SPRING_CONTEXT);
			try {
				if (strRefreshCtx != null) {
					refreshCtx = Boolean.parseBoolean(strRefreshCtx);
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn(REFRESH_SPRING_CONTEXT + " is not set. use default value " + refreshCtx);
					}
				}
			} catch (Exception e) {
				logger.error("parse " + REFRESH_SPRING_CONTEXT + " failed.value " + strRefreshCtx + " is illegal." + e.getMessage(), e);
			}

			final boolean refreshCtxCopy = refreshCtx;

			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						String propLocation = getConfigLocation(servletContext);
						String newPropMD5 = getMD5(propLocation);

						String logbackConfigLocation = servletContext.getInitParameter(CONFIG_LOCATION);
						String newConfigMD5 = getMD5(servletContext.getRealPath(logbackConfigLocation));

						// MD5不一致，则重新载入
						if (!propMD5.equals(newPropMD5) || !configMD5.equals(newConfigMD5)) {
							// 清空
							contextDestroyed(event);
							// 载入
							contextInitialized(event);

							// 如果开户刷新Spring context，并且properties文件已变更
							if (refreshCtxCopy && !propMD5.equals(newPropMD5)) {
								// 刷新Spring config
								WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
								if (ctx != null && ctx instanceof AbstractApplicationContext) {
									((AbstractApplicationContext) ctx).refresh();
								}
							}
						}
					} catch (Exception e) {
						logger.error("reload config failed." + e.getMessage(), e);
					}
				}
			}, refreshInterval, refreshInterval);

		} catch (JoranException e) {
			logger.error("can loading slf4j configure file from " + fn, e);
		}
	}

	private String getConfigLocation(ServletContext servletContext) throws FileNotFoundException {
		String param = servletContext.getInitParameter(LOGBACK_PROPERITES_PARAM);
		String logbackConfigLocation = servletContext.getInitParameter(CONFIG_LOCATION);
		String location = (param != null ? param : logbackConfigLocation);

		// 支持Spring文件注入方式
		if (!ResourceUtils.isUrl(location)) {
			location = SystemPropertyUtils.resolvePlaceholders(location);
			location = WebUtils.getRealPath(servletContext, location);
		}

		return location;
	}

	private String getMD5(String location) throws IOException {
		File file;
		if (location.startsWith("classpath:")) {
			file = ResourceUtils.getFile(location);
		} else {
			file = new File(location);
		}

		FileInputStream inputStream = new FileInputStream(file);
		byte[] buffer;
		try {
			buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
		} finally {
			inputStream.close();
		}

		return getMD5Format(buffer);
	}

	/**
	 * @param servletContext
	 * @return
	 */
	private Properties getPropertiesParam(ServletContext servletContext) {
		String logbackConfigLocation = servletContext.getInitParameter(CONFIG_LOCATION);
		String param = servletContext.getInitParameter(LOGBACK_PROPERITES_PARAM);

		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			String location = getConfigLocation(servletContext);

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
			propMD5 = getMD5(location);
			configMD5 = getMD5(servletContext.getRealPath(logbackConfigLocation));
		} catch (FileNotFoundException ex) {
			throw new IllegalArgumentException("Invalid 'logbackProperties' parameter:" + ex.getMessage());
		} catch (IOException ex) {
			throw new IllegalArgumentException("Invalid 'logbackProperties' parameter:" + ex.getMessage());
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
		Properties properties = getPropertiesParam(event.getServletContext());
		SystemPropertiesUtil.removeSystemProperties(properties);
	}

	/***
	 * 
	 * @Title: getMD5Format
	 * @Description: 计算MD5并转换为32字节明文显示串
	 * @author wujl
	 * @param data
	 * @return String 返回类型
	 */
	private static String getMD5Format(byte[] data) {
		try {
			message.update(data);
			byte[] b = message.digest();

			String digestHexStr = "";
			for (int i = 0; i < 16; i++) {
				digestHexStr += byteHEX(b[i]);
			}

			return digestHexStr;
		} catch (Exception e) {
			logger.error("MD5格式化时发生异常:" + StackTraceUtil.getStackTrace(e), e);
			return null;
		}
	}

	private static String byteHEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = hexDigits[(ib >>> 4) & 0X0F];
		ob[1] = hexDigits[ib & 0X0F];
		String s = new String(ob);
		return s;
	}
}