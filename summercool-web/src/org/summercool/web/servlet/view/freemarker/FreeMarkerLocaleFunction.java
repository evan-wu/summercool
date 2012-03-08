package org.summercool.web.servlet.view.freemarker;

import java.util.List;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.summercool.view.freemarker.FreeMarker;
import org.summercool.view.freemarker.FreeMarker.LocaleBean;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 判断当前页面请求的某国语言函数
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerLocaleFunction implements TemplateMethodModel {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		Locale locale = LocaleContextHolder.getLocale();

		Boolean result = Boolean.FALSE;
		if (arguments == null || arguments.isEmpty() || "".equals(arguments.get(0))) {
			return result;
		}
		//
		if (arguments.size() != 1) {
			throw new TemplateModelException("locale()函数有且只能有一个参数:(String language)");
		}
		//
		String language = (String) arguments.get(0);
		String localeLanguage = locale.getLanguage();
		LocaleBean localeBean = FreeMarker.SUPPORTED_LANGUAGE_MAP.get(language);
		String supportedLanguage = localeBean != null ? localeBean.getLanguage() : null;

		if (supportedLanguage != null && supportedLanguage.equals(localeLanguage)) {
			result = Boolean.TRUE;
		}
		return result;
	}
}
