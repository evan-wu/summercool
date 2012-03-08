package org.summercool.web.servlet.view.freemarker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 生成csrf隐藏域
 * 
 * @author:chuanshuang.liucs
 * @date:2010-8-9
 * 
 */
public class FreeMarkerCsrfFunction implements TemplateMethodModel {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes servletAttrs = (ServletRequestAttributes) attrs;
		HttpServletRequest request = servletAttrs.getRequest();
		CsrfToken token = new CsrfToken(request);
		return token.getHiddenField().toString();
	}

}
