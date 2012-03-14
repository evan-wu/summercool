package org.summercool.platform.web.module.petstore.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;

/**
 * TODO(添加描述)
 *
 * @Title: LogoutController.java
 * @Package org.summercool.platform.web.module.demo.controllers
 * @author Yanjh
 * @date 2012-2-13 下午5:42:19
 * @version V1.0
 */
public class LogoutController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CookieUtils.clearCookie(request);
		return new ModelAndView("redirect:/index.htm");
	}

}
