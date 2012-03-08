package org.summercool.web.servlet.view.freemarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.context.Theme;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.summercool.view.freemarker.FreeMarker;
import org.summercool.view.freemarker.FreeMarker.LocaleBean;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 取得页面多国语言的theme信息函数
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerThemeFunction implements TemplateMethodModel {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		String result = "";
		if (arguments == null || arguments.isEmpty() || "".equals(arguments.get(0))) {
			return result;
		}
		//
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
		Locale locale = LocaleContextHolder.getLocale();
		Theme theme = RequestContextUtils.getTheme(servletAttrs.getRequest());
		String firstArgument = ((String) arguments.get(0)).trim();
		//
		if (FreeMarker.SUPPORTED_LANGUAGE_MAP.get(firstArgument) != null) {
			List<Object> args = new ArrayList<Object>();
			String secondArgument = "";
			//
			if (arguments.size() > 2) {
				secondArgument = ((String) arguments.get(1)).trim();
				for (int i = 2; i < arguments.size(); i++) {
					args.add(arguments.get(i));
				}
			}
			//
			if (arguments.size() == 2) {
				secondArgument = ((String) arguments.get(1)).trim();
			}
			//
			LocaleBean localeBean = FreeMarker.SUPPORTED_LANGUAGE_MAP.get(firstArgument);
			result = theme.getMessageSource().getMessage(secondArgument, args.toArray(), localeBean.getLocal());
		} else {
			List<Object> args = new ArrayList<Object>();
			if (arguments.size() > 1) {
				for (int i = 1; i < arguments.size(); i++) {
					args.add(arguments.get(i));
				}
			}
			result = theme.getMessageSource().getMessage(firstArgument, args.toArray(), locale);
		}
		return result;
	}
}
