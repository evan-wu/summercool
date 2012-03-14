package org.summercool.platform.web.module.petstore.widgets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;
import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;

public class HeaderWidget implements FreeMarkerWidget {

	public void referenceData(HttpServletRequest request, Map<String, Object> model) {
		model.put("userName", CookieUtils.getUserName(request));
	}

}
