package org.summercool.web.beans.cookie;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.summercool.beans.crypto.CryptoBeanDefinition;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public class CookieConfigurer {
	private String name;

	private String clientName;

	private String path = "/";

	private int lifeTime = -1;

	// 字符编码，缺省为utf8
	private String encoding = "UTF-8";

	// 在值前面增加多少随机数字，如果 <= 0 ,则表示不增加
	private int randomChar = 0;

	private String domain;

	// 是否进行加密,如果不为null，则进行加密
	private CryptoBeanDefinition crypto;

	// 是否进行加密标志
	private boolean encrypted = Boolean.FALSE;

	private String charset;

	// 将客户端的cookie值翻译过来���
	public String getRealValue(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			String back = value;
			if (this.crypto != null) {
				back = crypto.dectypt(back, charset);
			}
			if (back == null) {
				return null;
			}
			if (this.randomChar > 0) {
				if (back.length() < this.randomChar) {
					return null;
				}
				back = back.substring(this.randomChar);
			}
			// back = URLDecoder.decode(back, encoding);
			return back;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 将真实值翻译成客户端cookie存储值ֵ
	public String getClientValue(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		try {
			String back = value;
			// back = URLEncoder.encode(back, encoding);
			if (back == null) {
				return "";
			}
			if (this.randomChar > 0) {
				back = RandomStringUtils.randomAlphanumeric(this.randomChar) + back;
			}
			if (this.crypto != null) {
				back = crypto.encrypt(back, charset);
			}
			return back;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Cookie getCookie(String value, String contextPath) {
		return this.getCookie(value, contextPath, this.lifeTime);
	}

	public Cookie getCookie(String value, String contextPath, int expiry) {
		Cookie c = new Cookie(getClientName(), getClientValue(value));
		c.setDomain(domain);
		c.setPath(contextPath + getPath());
		c.setMaxAge(expiry);
		return c;
	}

	// 得到删除一个cookie的cookie
	public Cookie getDeleteCookie(String contextPath) {
		return this.getCookie("", contextPath, 0);// 将过期时间设置为0即为删除一个cookie��cookie
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
		if (!this.path.startsWith("/")) {
			this.path = "/" + this.path;
		}
	}

	/**
	 * @return the clinetName
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * @param clinetName the clinetName to set
	 */
	public void setClientName(String clinetName) {
		this.clientName = clinetName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the lifeTime
	 */
	public int getLifeTime() {
		return lifeTime;
	}

	/**
	 * @param lifeTime the lifeTime to set
	 */
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getRandomChar() {
		return randomChar;
	}

	public void setRandomChar(int randomChar) {
		this.randomChar = randomChar;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public CryptoBeanDefinition getCrypto() {
		return crypto;
	}

	public void setCrypto(CryptoBeanDefinition crypto) {
		this.crypto = crypto;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
