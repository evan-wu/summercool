package org.summercool.web.servlet.view.freemarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.summercool.view.freemarker.FreeMarker;
import org.summercool.web.context.ResponseContextHolder;
import org.summercool.web.servlet.http.StringBufferedResponse;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * 用来渲染FreeMarker页面模版的子页面模版函数,与SummerCool框架帮定
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerWidgetFunction extends FreeMarker implements TemplateMethodModelEx {

	// FreeMarkerViewResolver
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
		// 解包FreeMarker函数的参数
		List args = new ArrayList();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		arguments = args;
		//
		if (freemarkerViewResolver == null) {
			throw new TemplateModelException(FreeMarkerViewResolver.class.getName() + "实例不能为空!");
		}
		if (arguments.size() == 1) {
			Object obj0 = arguments.get(0);
			if (!(obj0 instanceof String)) {
				throw new TemplateModelException("widget()函数有且只能参数:(String viewName, [Map<String, Object> params]..)");
			}
			String viewName = (String) obj0;
			Map<String, Object> model = new HashMap<String, Object>();
			return render(viewName, model);
		} else {
			String viewName = null;
			Object obj0 = arguments.get(0);
			//
			if (!(obj0 instanceof String)) {
				throw new TemplateModelException("widget()函数有且只能参数:(String viewName, [Map<String, Object> params]..)");
			}
			viewName = (String) obj0;
			//
			Map<String, Object> model = new HashMap<String, Object>();
			for (int i = 1; i < arguments.size(); i++) {
				Object obj = arguments.get(i);
				if (!(obj instanceof Map)) {
					throw new TemplateModelException(
							"widget()函数有且只能有参数:(String viewName, [Map<String, Object> params]..)");
				}
				Map map = (Map) obj;
				validateMapKeysForString(map);
				model.putAll(map);
			}
			return render(viewName, model);
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
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		Locale locale = LocaleContextHolder.getLocale();
		HttpServletRequest request = requestAttributes.getRequest();
		HttpServletResponse response = ResponseContextHolder.getResponse();
		//
		try {
			//
			FreeMarkerView view = (FreeMarkerView) freemarkerViewResolver.resolveViewName(viewName, locale);
			if (view == null) {
				throw new TemplateModelException("[" + viewName + "]不存在!");
			}
			//
			FreeMarkerWidget widget = widgetsMap.get(viewName);
			if (widget != null) {
				widget.referenceData(request, model);
			}
			//
			StringBufferedResponse bufferedResponse = new StringBufferedResponse(response);
			view.render(model, request, bufferedResponse);
			//
			return bufferedResponse.getResponseContent();
		} catch (Exception e) {
			throw new TemplateModelException("加载[" + viewName + "]时出错:" + e);
		}
	}
}
