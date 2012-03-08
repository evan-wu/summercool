package org.summercool.view.freemarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * 组装参数为Map<String str, Object object>类型的函数
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerParamFunction implements TemplateMethodModelEx {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments == null || arguments.size() != 2) {
			throw new TemplateModelException("param()函数有且只能有两个参数:(String key, Object value)");
		}
		// 解包FreeMarker函数的参数
		List args = new ArrayList();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		arguments = args;
		//
		Object key = arguments.get(0);
		Object value = arguments.get(1);
		if (!(key instanceof String)) {
			throw new TemplateModelException("param()函数有且只能有两个参数:(String key, Object value)");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put((String) key, value);
		return model;
	}

}
