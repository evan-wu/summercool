package org.summercool.platform.web.module.petstore.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;
import org.summercool.web.servlet.pipeline.PreProcessPipeline;

/**
 * @Title: LoginSecurity.java
 * @Package com.gexin.platform.web.module.manager.config.security
 * @Description:
 * @author 简道
 * @date 2011-11-24 下午1:29:27
 * @version V1.0
 */
public class LoginSecurity extends AbstractSecurity implements PreProcessPipeline {

	private int order;

	// set 方法
	public void setOrder(int order) {
		this.order = order;
	}

	public ModelAndView handleProcessInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return new ModelAndView("redirect:/" + "login.htm");
	}

	public boolean isPermitted(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (match(request)) {
			return true;
		} else {
			if (CookieUtils.isLogin(request)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public int getOrder() {
		return order;
	}

}