package org.summercool.platform.web.module.petstore.config.freemarker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class FreeMarkerErrorFunction implements TemplateMethodModelEx {

	public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
		// 解包FreeMarker函数的参数
		List<Object> args = new ArrayList<Object>();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		// 
		BindStatus bindStatus;
		String errorCode;
		//
		if (args.size() != 2) {
			throw new TemplateModelException("error()函数只支持参数:(BindStatus status, String errorCode)");
		}
		if (!(args.get(0) instanceof BindStatus) || !((args.get(1) instanceof String))) {
			throw new TemplateModelException("error()函数只支持参数:(BindStatus status, String errorCode)");
		}
		//
		bindStatus = (BindStatus) args.get(0);
		errorCode = (String) args.get(1);
		//
		for (ObjectError error : bindStatus.getErrors().getAllErrors()) {
			String[] codes = error.getCodes();
			if (codes == null) { continue; }
			for (String code : codes) {
				if (code.equals(errorCode)) {
					return error.getDefaultMessage();
				}
			}
		}
		//
		return null;
	}
}
