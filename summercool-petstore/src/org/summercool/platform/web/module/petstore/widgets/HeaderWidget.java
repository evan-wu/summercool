package org.summercool.platform.web.module.petstore.widgets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.summercool.web.servlet.view.freemarker.FreeMarkerWidget;

public class HeaderWidget implements FreeMarkerWidget {

	public void referenceData(HttpServletRequest arg0, Map<String, Object> model) {
		System.out.println("header excuting ~");
		//model.put("name", "孙伟");
	}

}
