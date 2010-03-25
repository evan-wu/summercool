package com.bdconsulting.summercool.web.servlet.view.freemarker;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.bdconsulting.summercool.view.freemarker.FreeMarker;
import com.bdconsulting.summercool.view.freemarker.FreeMarker.LocaleBean;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-11
 * 
 */
public class FreeMarkerViewResolver extends org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver {

	private Map<String, String> languageExtensionMap = new HashMap<String, String>();

	@Override
	protected Class<FreeMarkerView> requiredViewClass() {
		return FreeMarkerView.class;
	}

	public Map<String, String> getLanguageExtention() {
		return languageExtensionMap;
	}

	public void setLanguageExtention(Map<String, String> languageExtention) {
		this.languageExtensionMap = languageExtention;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		String viewNameWithLanguageExtension;

		// 判断当前viewName是否需要多国语言处理
		if (languageExtensionMap != null && languageExtensionMap.size() > 0) {
			String localeLanguage = locale.getLanguage();
			LocaleBean localeBean = FreeMarker.SUPPORTED_LOCALE_LANGUAGE_MAP.get(localeLanguage);
			if (localeBean != null) {
				String languageExtention = languageExtensionMap.get(localeBean.getLanguage());
				if (languageExtention != null || !("".equals(languageExtention))) {
					viewNameWithLanguageExtension = viewName + languageExtention;
					//
					AbstractUrlBasedView view = buildView(viewNameWithLanguageExtension);
					View result = (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
					//
					if(view.checkResource(locale)){
						return result;
					}
				}
			}
		}
		// 需要则加载多国语言viewName
		AbstractUrlBasedView view = buildView(viewName);
		View result = (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
		return (view.checkResource(locale) ? result : null);
	}
}
