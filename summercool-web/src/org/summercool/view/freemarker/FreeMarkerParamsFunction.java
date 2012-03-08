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
 * 组装参数多个Map<String str, Object object>为Map<String str, Object object>类型的函数
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-10
 * 
 */
public class FreeMarkerParamsFunction extends FreeMarker implements TemplateMethodModelEx {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments == null || arguments.size() == 0) {
			throw new TemplateModelException("params()函数有且只能有Map类型的参数:(Map<String, Object> map, ...)");
		}
		// 解包FreeMarker函数的参数
		List args = new ArrayList();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		//
		arguments = args;
		Map<String, Object> model = new HashMap<String, Object>();
		for (Object object : arguments) {
			if (!(object instanceof Map)) {
				throw new TemplateModelException("params()函数有且只能有Map类型的参数:(Map<String, Object> map, ...)");
			}
			Map map = (Map) object;
			validateMapKeysForString(map);
			model.putAll(map);
		}
		return model;
	}

}
