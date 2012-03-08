package org.summercool.web.module.cookie;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.summercool.web.beans.cookie.CookieConfigurer;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public class DefaultCookieModule implements CookieModule {

	protected final Log logger = LogFactory.getLog(getClass());

	private Map<String, CookieConfigurer> clientName2CfgMap;

	private Map<String, CookieConfigurer> name2CfgMap;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private Map<String, String> cookieMap;

	public DefaultCookieModule(Map<String, CookieConfigurer> clientName2CfgMap,
			Map<String, CookieConfigurer> name2CfgMap, HttpServletRequest request, HttpServletResponse response) {
		this.clientName2CfgMap = clientName2CfgMap;
		this.name2CfgMap = name2CfgMap;
		this.request = request;
		this.response = response;
		this.cookieMap = initCookieMap();
	}

	public Map<String, String> initCookieMap() {
		Map<String, String> map = new HashMap<String, String>();
		Cookie[] cookies = this.request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie ck : cookies) {
				String name = ck.getName();
				CookieConfigurer cfg = clientName2CfgMap.get(name);
				if (cfg == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("get a unknow cookie name[" + name + "]value[" + ck.getValue() + "]");
					}
					continue;
				}
				String value = ck.getValue();
				value = cfg.getRealValue(value);
				map.put(cfg.getName(), value);
			}
		}
		return map;
	}

	public void clean() {
		Set<String> cookieMapKeys = new HashSet<String>(this.cookieMap.keySet());
		for (String key : cookieMapKeys) {
			this.remove(key);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("remove all cookies end.");
		}
	}

	public String get(String key) {
		if (key == null) {
			throw new NullPointerException("cookie name key can't be null.");
		}
		return this.cookieMap.get(key);
	}

	public Map<String, String> getAll() {
		return new HashMap<String, String>(this.cookieMap);
	}

	public String remove(String key) {
		if (key == null) {
			throw new NullPointerException("cookie name key can't be null.");
		}
		String value = this.cookieMap.remove(key);
		CookieConfigurer cfg = name2CfgMap.get(key);
		if (cfg != null) {
			Cookie c = cfg.getDeleteCookie(this.request.getContextPath());
			this.response.addCookie(c);
			if (logger.isDebugEnabled()) {
				logger.debug("remove a cookies" + getCookieString(c));
			}
		}
		return value;
	}

	public void set(String key, String value) {
		if (key == null) {
			throw new NullPointerException("cookie name key can't be null.");
		}
		if (value == null) {
			remove(key);
			return;
		}
		CookieConfigurer cfg = name2CfgMap.get(key);
		if (cfg == null) {// 配过的才能写入
			if (logger.isDebugEnabled()) {
				logger.debug("set cookie name[" + key + "] value[" + value + "],but this name not configured.");
			}
		} else {
			this.cookieMap.put(key, value);
			Cookie c = cfg.getCookie(value, this.request.getContextPath());
			this.response.addCookie(c);
			if (logger.isDebugEnabled()) {
				logger.debug("add a new cookie " + getCookieString(c));
			}
		}
	}

	public void set(String key, String value, int expiry) {
		if (key == null) {
			throw new NullPointerException("cookie name key can't be null.");
		}
		if (value == null) {
			remove(key);
			return;
		}
		CookieConfigurer cfg = name2CfgMap.get(key);
		if (cfg == null) {// 配过的才能写入
			if (logger.isDebugEnabled()) {
				logger.debug("set cookie name[" + key + "] value[" + value + "],but this name not configured.");
			}
		} else {
			this.cookieMap.put(key, value);
			Cookie c = cfg.getCookie(value, this.request.getContextPath(), expiry);
			this.response.addCookie(c);
			if (logger.isDebugEnabled()) {
				logger.debug("add a new cookie " + getCookieString(c));
			}
		}
	}

	public void set(String key, Long value, int expiry) {
		if (value == null) {
			this.remove(key);
			return;
		}
		this.set(key, Long.toString(value), expiry);
	}

	public void set(String key, Long value) {
		if (value == null) {
			this.remove(key);
			return;
		}
		this.set(key, Long.toString(value));
	}

	private static final String getCookieString(Cookie c) {
		if (c == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(c.toString());
		sb.append(" name[").append(c.getName()).append("] value[").append(c.getValue()).append("] domain[");
		sb.append(c.getDomain()).append("] path[").append(c.getPath()).append("] maxAge[").append(c.getMaxAge());
		sb.append("]");
		return sb.toString();
	}

}
