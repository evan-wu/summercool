package org.summercool.web.servlet.view.freemarker;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Input;
import org.summercool.web.module.cookie.CookieModule;

public class CsrfToken {

	/** 生成html隐藏域的名称 */
	public static final String TOKEN_KEY = "csrf_token";

	/** 生成随机token值用 */
	private static final char[] DIGITS = "0123456789abcdefghijklmnopqrstuvwxyz_".toCharArray();
	private static final int DIGITS_LENGTH = DIGITS.length;
	private static final Random random = new Random();
	private static final String TOKEN_SEPERATOR = "#";
	private final HttpServletRequest request;

	public CsrfToken(HttpServletRequest request) {
		this.request = request;
	}

	public Input getHiddenField() {
		return new Input("hidden", TOKEN_KEY, getUniqueToken());
	}

	public String getUniqueToken() {
		String newToken = newToken();
		CookieModule jar = (CookieModule) request.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return null;
		}
		String token = jar.get(TOKEN_KEY);
		if (StringUtils.isBlank(token)) {
			token = newToken;
		} else {
			String[] tokenArray = StringUtils.split(token, TOKEN_SEPERATOR);
			if (tokenArray.length < 5) {
				token = new StringBuilder(token).append(TOKEN_SEPERATOR).append(newToken).toString();
			} else {
				token = new StringBuilder(token.substring(token.indexOf(TOKEN_SEPERATOR) + 1)).append(TOKEN_SEPERATOR)
						.append(newToken).toString();
			}
		}
		jar.set(TOKEN_KEY, token);
		return newToken;
	}

	/**
	 * 生成token
	 * 
	 * @author:chuanshuang.liucs
	 * @date:2010-8-23
	 * @return
	 */
	private static String newToken() {
		long longValue = Math.abs(System.currentTimeMillis() + random.nextLong());

		if (longValue == 0) {
			return String.valueOf(DIGITS[0]);
		}

		StringBuffer strValue = new StringBuffer();

		while (longValue != 0) {
			int digit = (int) (longValue % DIGITS_LENGTH);
			longValue = longValue / DIGITS_LENGTH;

			strValue.append(DIGITS[digit]);
		}

		return strValue.reverse().toString();
	}

	/**
	 * 验证csrf token
	 * 
	 * @author:chuanshuang.liucs
	 * @date:2010-8-23
	 * @param httpRequest
	 * @return
	 */
	public static boolean check(HttpServletRequest httpRequest) {
		String tokenFromRequest = httpRequest.getParameter(TOKEN_KEY);
		// 请求中不包含token
		if (StringUtils.isBlank(tokenFromRequest)) {
			return false;
		}
		CookieModule jar = (CookieModule) httpRequest.getAttribute(CookieModule.COOKIE);

		if (jar == null) {
			return false;
		}
		// cookie中的tokens
		String tokens = jar.get(TOKEN_KEY);
		if (StringUtils.isNotBlank(tokens)) {
			String[] tokenArray = StringUtils.split(tokens, TOKEN_SEPERATOR);
			if (tokenArray != null) {
				for (int i = 0; i < tokenArray.length; i++) {
					String token = tokenArray[i];
					if (tokenFromRequest.equals(token)) {
						// cookie中移除，使失效
						tokens = tokens.replace(tokenFromRequest, "");
						tokens = tokens.replaceAll(TOKEN_SEPERATOR + TOKEN_SEPERATOR, TOKEN_SEPERATOR);
						jar.set(TOKEN_KEY, tokens);
						return true;
					}
				}
			}
		}
		return false;
	}
}
