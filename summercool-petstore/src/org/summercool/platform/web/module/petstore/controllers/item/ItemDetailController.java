package org.summercool.platform.web.module.petstore.controllers.item;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.summercool.beans.url.UrlBuilderBeanDefinition;
import org.summercool.web.module.url.UrlBuilderModule;

public class ItemDetailController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UrlBuilderModule urlBuilderModule = (UrlBuilderModule) request.getAttribute(UrlBuilderModule.URL_BUILDER);
		UrlBuilderBeanDefinition urlBuilderBean = urlBuilderModule.matchUrlBuilderBean();
		// 如果在访问detail页面时，没有找查到相对应的url规则直接返回到/login.htm页面
		if (urlBuilderBean == null || !urlBuilderBean.isMatched()) {
			return new ModelAndView("redirect:/index.htm");
		}
		//
		Map<String, String> map = urlBuilderBean.getUriTemplateVariables();
		try {
			String id = map.get("id");
			// 在这里可以写一些业务逻辑，比如在DB中查找到item信息收直接显示detail页面
			Long.valueOf(id);
		} catch (Exception e) {
			return new ModelAndView("redirect:/index.htm");
		}
		//
		return new ModelAndView("/petstore/views/item/itemDetail", map);
	}

}
