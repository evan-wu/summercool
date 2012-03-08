package org.summercool.view.freemarker;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public abstract class FreeMarker {

	public static Map<String, LocaleBean> SUPPORTED_LANGUAGE_MAP = new HashMap<String, LocaleBean>();
	
	public static Map<String, LocaleBean> SUPPORTED_LOCALE_LANGUAGE_MAP = new HashMap<String, LocaleBean>();

	static {
		SUPPORTED_LANGUAGE_MAP.put("us", new LocaleBean("en", Locale.ENGLISH));
		SUPPORTED_LANGUAGE_MAP.put("jp", new LocaleBean("ja", Locale.JAPANESE));
		SUPPORTED_LANGUAGE_MAP.put("cn", new LocaleBean("zh", Locale.CHINESE));
		//
		SUPPORTED_LOCALE_LANGUAGE_MAP.put("en", new LocaleBean("us", Locale.ENGLISH));
		SUPPORTED_LOCALE_LANGUAGE_MAP.put("ja", new LocaleBean("jp", Locale.JAPANESE));
		SUPPORTED_LOCALE_LANGUAGE_MAP.put("zh", new LocaleBean("cn", Locale.CHINESE));
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * @param map
	 */
	public void validateMapKeysForString(Map<Object, Object> map) {
		for (Entry<Object, Object> entry : map.entrySet()) {
			Object key = entry.getKey();
			if (!(key instanceof String)) {
				throw new RuntimeException("FreeMarker模版只接受Key值为字符串类型的Map<String str, Object object>:" + map + "#" + key);
			}
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-10
	 * 
	 */
	public static class LocaleBean {

		private String language;

		private Locale local;

		public LocaleBean(String language, Locale local) {
			super();
			this.language = language;
			this.local = local;
		}

		public String getLanguage() {
			return language;
		}

		public Locale getLocal() {
			return local;
		}
	}
}
