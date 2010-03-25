package com.bdconsulting.summercool.web.servlet.view.freemarker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bdconsulting.summercool.view.freemarker.FreeMarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 用来渲染FreeMarker页面模版的子页面模版函数,与SummerCool框架帮定
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerWidgetFunction implements TemplateMethodModelEx {

	// Summer Cool框架的FreeMarkerViewResolver
	private FreeMarkerViewResolver freemarkerViewResolver;

	// Widgets Map集合
	private Map<String, FreeMarkerWidget> widgetsMap = new HashMap<String, FreeMarkerWidget>();

	public Map<String, FreeMarkerWidget> getWidgetsMap() {
		return widgetsMap;
	}

	public void setFreemarkerViewResolver(FreeMarkerViewResolver freemarkerViewResolver) {
		this.freemarkerViewResolver = freemarkerViewResolver;
	}

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments == null || arguments.size() == 0) {
			return "";
		}
		if (freemarkerViewResolver == null) {
			throw new TemplateModelException("com.bdconsulting.summercool.web.servlet.view.freemarker.FreeMarkerViewResolver实例不能为空!");
		}
		if (arguments.size() == 1) {
			Object obj0 = arguments.get(0);
			if (!(obj0 instanceof String)) {
				throw new TemplateModelException("widget()函数有且只能有一到两个参数:(String viewName, [Map<String, Object> params])");
			}
			String viewName = (String) obj0;
			Map<String, Object> model = new HashMap<String, Object>();
			return render(viewName, model);
		} else if (arguments.size() == 2) {
			Object obj0 = arguments.get(0);
			Object obj1 = arguments.get(1);
			if (!(obj0 instanceof String) || !(obj1 instanceof Map)) {
				throw new TemplateModelException("widget()函数有且只能有一到两个参数:(String viewName, [Map<String, Object> params])");
			}
			String viewName = (String) obj0;
			Map model = (Map) obj1;
			FreeMarker.validateMapKeysForString(model);
			return render(viewName, model);
		} else {
			throw new TemplateModelException("widget()函数有且只能有一到两个参数:(String viewName, [Map<String, Object> params])");
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-19
	 * @param viewName
	 * @param model
	 * @return
	 */
	private String render(String viewName, Map<String, Object> model) throws TemplateModelException {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		Locale locale = LocaleContextHolder.getLocale();
		HttpServletRequest request = requestAttributes.getRequest();
		//
		try {
			FreeMarkerView view = (FreeMarkerView) freemarkerViewResolver.resolveViewName(viewName, locale);
			if (view == null) {
				throw new TemplateModelException("[" + viewName + "]不存在!");
			}
			FreeMarkerWidget widget = widgetsMap.get(viewName);
			if (widget != null) {
				widget.referenceData(request, model);
			}
			return view.renderForWidget(request, model);
		} catch (Exception e) {
			throw new TemplateModelException("加载[" + viewName + "]时出错:" + e);
		}
	}

}
