package org.summercool.web.module.cookie;

import java.util.Map;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public interface CookieModule {

	String COOKIE = CookieModule.class.getName() + ".COOKIE";

	/**
	 * 设置一个值,如果已存在,则覆盖,如果value=null,则相当于 remove(key)
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value);

	public void set(String key, Long value);

	/**
	 * 设置一个值,如果已存在,则覆盖,如果value=null,则相当于 remove(key)
	 * 
	 * @param key
	 * @param value
	 * @param expiry an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie is
	 *            not stored; if zero, deletes the cookie
	 */
	public void set(String key, String value, int expiry);

	public void set(String key, Long value, int expiry);

	/**
	 * 得到一个值
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key);

	/**
	 * 得到所有的值对
	 * 
	 * @return
	 */
	public Map<String, String> getAll();

	/**
	 * 移除一个键值对
	 * 
	 * @param key
	 * @return
	 */
	public String remove(String key);

	/**
	 * 将所有的cookie值都清除
	 */
	public void clean();

}
