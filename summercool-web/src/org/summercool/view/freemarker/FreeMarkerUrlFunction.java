package org.summercool.view.freemarker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.summercool.beans.url.UrlBuilderBeanDefinition;
import org.summercool.util.UriTemplateUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-11
 * 
 */
public class FreeMarkerUrlFunction extends FreeMarker implements TemplateMethodModelEx {

	private Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap;

	public void setUrlBuilderBeanMap(Map<String, List<UrlBuilderBeanDefinition>> urlBuilderBeanMap) {
		this.urlBuilderBeanMap = urlBuilderBeanMap;
	}

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments == null || arguments.size() == 0) {
			throw new TemplateModelException(
					"url()函数有且只能有一到三个参数:(String uriTemplateModule, [Map<String, Object> params], [String seq])");
		}
		// 解包FreeMarker函数的参数
		List args = new ArrayList();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		arguments = args;
		//
		if (urlBuilderBeanMap == null || urlBuilderBeanMap.size() == 0) {
			throw new TemplateModelException(
					"属性Map<String, List<DefaultUrlBuilderBeanDefinition>> urlBuilderBeanMap为空!");
		}
		String uriTemplateModule = null;
		Map params = null;
		String seq = null;
		if (arguments.size() == 1) {
			Object arg0 = arguments.get(0);
			if (!(arg0 instanceof String)) {
				throw new TemplateModelException(
						"url()函数有且只能有一到三个参数:(String uriTemplateModule, [Map<String, Object> params], [String seq])");
			}
			uriTemplateModule = (String) arg0;
			return findUri(uriTemplateModule, null, null);
		} else if (arguments.size() == 2) {
			Object arg0 = arguments.get(0);
			Object arg1 = arguments.get(1);
			if (!(arg0 instanceof String) || !(arg1 instanceof Map || arg1 instanceof BigDecimal)) {
				throw new TemplateModelException(
						"url()函数有且只能有一到三个参数:(String uriTemplateModule, [Map<String, Object> params], [String seq])");
			}
			uriTemplateModule = (String) arg0;
			if (arg1 instanceof Map) {
				params = (Map) arg1;
				validateMapKeysForString(params);
			}
			if (arg1 instanceof BigDecimal) {
				seq = arg1.toString();
			}
			return findUri(uriTemplateModule, params, seq);
		} else if (arguments.size() == 3) {
			Object arg0 = arguments.get(0);
			Object arg1 = arguments.get(1);
			Object arg2 = arguments.get(2);
			if (!(arg0 instanceof String) || !(arg1 instanceof Map) || !(arg2 instanceof BigDecimal)) {
				throw new TemplateModelException(
						"url()函数有且只能有一到三个参数:(String uriTemplateModule, [Map<String, Object> params], [String seq])");
			}
			uriTemplateModule = (String) arg0;
			params = (Map) arg1;
			seq = arg2.toString();
			validateMapKeysForString(params);
			return findUri(uriTemplateModule, params, seq);
		} else {
			throw new TemplateModelException(
					"url()函数有且只能有一到三个参数:(String uriTemplateModule, [Map<String, Object> params], [String seq])");
		}
	}

	/**
	 * 
	 * @author:shaochuan.wangsc
	 * @date:2010-3-19
	 * @param uriTemplateModule
	 * @param params
	 * @param seq
	 * @return
	 */
	private String findUri(String uriTemplateModule, Map<String, Object> params, String seq)
			throws TemplateModelException {
		List<UrlBuilderBeanDefinition> duddList = this.urlBuilderBeanMap.get(uriTemplateModule);
		if (duddList == null || duddList.size() == 0) {
			throw new TemplateModelException("未找到[" + uriTemplateModule + "]对应的UrlBuilderBeanDefinition!");
		}
		if (params == null && seq == null) {
			UrlBuilderBeanDefinition urlBuilderBeanDefinition = duddList.get(0);
			return urlBuilderBeanDefinition.getUriTemplate();
		} else if (params != null && seq == null) {
			UrlBuilderBeanDefinition urlBuilderBeanDefinition = duddList.get(0);
			return UriTemplateUtils.makeUri(urlBuilderBeanDefinition.getUriTemplate(), params);
		} else {
			for (UrlBuilderBeanDefinition ubbd : duddList) {
				if (seq.equals(ubbd.getSeq())) {
					return UriTemplateUtils.makeUri(ubbd.getUriTemplate(), params);
				}
			}
		}
		throw new TemplateModelException("未找到[" + uriTemplateModule + "]对应的UrlBuilderBeanDefinition!");
	}

}
