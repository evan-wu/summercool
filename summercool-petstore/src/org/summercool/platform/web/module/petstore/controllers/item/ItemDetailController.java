package org.summercool.platform.web.module.petstore.controllers.item;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
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
		if (urlBuilderBean == null) {
			return new ModelAndView("redirect:/index.htm");
		}
		if (!urlBuilderBean.isMatched()) {
			return new ModelAndView("redirect:/index.htm");
		}
		//
		Map<String, String> map = urlBuilderBean.getUriTemplateVariables();
		String id = map.get("1");
		if (!StringUtils.hasText(id)) {
			return new ModelAndView("redirect:/index.htm");
		}
		//
		return new ModelAndView("/petstore/views/item/itemDetail", map);
	}

}
