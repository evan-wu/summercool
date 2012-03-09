package org.summercool.platform.web.module.petstore.config.cookie;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.summercool.web.module.cookie.CookieModule;

/**
 * @Title: CookieUtil.java
 * @Package com.gexin.platform.web.module.manager.config.cookie
 * @Description:
 * @author 简道
 * @date 2011-11-24 下午1:15:14
 * @version V1.0
 */
public class CookieUtils {

	static final String[] AUTHORITIES = new String[0];

	public static void writeCookie(HttpServletRequest request, UserDO userDO) {
		if (request == null || userDO == null) {
			throw new IllegalArgumentException();
		}

		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			throw new NullPointerException();
		}

		jar.remove(CookieConstants.MANAGER_ID_COOKIE);
		jar.remove(CookieConstants.MANAGER_PWD_COOKIE);
		jar.remove(CookieConstants.MANAGER_UNAME_COOKIE);

		jar.set(CookieConstants.MANAGER_ID_COOKIE, userDO.getId().toString());
		try {
			jar.set(CookieConstants.MANAGER_UNAME_COOKIE, URLEncoder.encode(userDO.getUserName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		jar.set(CookieConstants.MANAGER_PWD_COOKIE, userDO.getPassword());
	}

	public static void clearCookie(HttpServletRequest request) {
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);
		if (jar != null) {
			jar.remove(CookieConstants.MANAGER_ID_COOKIE);
			jar.remove(CookieConstants.MANAGER_UNAME_COOKIE);
			jar.remove(CookieConstants.MANAGER_PWD_COOKIE);
		}
	}

	public static Long getUserId(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}
		String idStr = jar.get(CookieConstants.MANAGER_ID_COOKIE);
		if (StringUtils.isNumeric(idStr)) {
			return Long.valueOf(idStr);
		}
		return null;
	}

	public static String getUserName(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}

		try {
			String uname = jar.get(CookieConstants.MANAGER_UNAME_COOKIE);
			if (uname == null) {
				return uname;
			}
			return URLDecoder.decode(jar.get(CookieConstants.MANAGER_UNAME_COOKIE), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String getUserPassword(HttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException();
		}
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}

		try {
			String upassword = jar.get(CookieConstants.MANAGER_UNAME_COOKIE);
			if (upassword == null) {
				return upassword;
			}
			return URLDecoder.decode(jar.get(CookieConstants.MANAGER_PWD_COOKIE), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static boolean isLogin(HttpServletRequest request) {
		Long userId = getUserId(request);
		String userName = getUserName(request);
		String userPassword = getUserPassword(request);
		if (userId != null && userName != null && userPassword != null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: getCurrentUserDO
	 * @Description: 获取当前用户
	 * @author 简道
	 * @param request
	 * @return UserDO 返回类型
	 * @throws
	 */
	public static UserDO getCurrentUser(HttpServletRequest request) {
		if (!isLogin(request)) {
			return null;
		}

		Long userId = getUserId(request);
		String userName = getUserName(request);
		String userPassword = getUserPassword(request);

		UserDO userDO = new UserDO();
		userDO.setId(userId);
		userDO.setUserName(userName);
		userDO.setPassword(userPassword);

		return userDO;
	}

}
