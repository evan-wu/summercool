package org.summercool.platform.web.module.petstore.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.summercool.platform.web.module.petstore.config.cookie.CookieUtils;
import org.summercool.platform.web.module.petstore.config.cookie.UserDO;
import org.summercool.platform.web.module.petstore.formbean.LoginFormBean;

@SuppressWarnings("deprecation")
public class LoginController extends SimpleFormController {

	public LoginController() {
		setBindOnNewForm(true);
		setCommandName("loginFormBean");
		setCommandClass(LoginFormBean.class);
	}

	@Override
	protected void onBindOnNewForm(HttpServletRequest request, Object command) throws Exception {
		LoginFormBean loginFormBean = (LoginFormBean) command;
		loginFormBean.setUserName("请输入用户名和密码");
		loginFormBean.setPassword("");
	}

	@Override
	protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
		LoginFormBean loginFormBean = (LoginFormBean) command;
		//
		if (StringUtils.isEmpty(loginFormBean.getUserName()) || StringUtils.isEmpty(loginFormBean.getPassword())) {
			errors.reject("-1", "用户名或密码不能为空");
			return;
		}
		if ("admin".equals(loginFormBean.getUserName()) && "111111".equals(loginFormBean.getPassword())) {
			UserDO userInfo = new UserDO();
			userInfo.setId(0L);
			userInfo.setUserName("admin");
			userInfo.setPassword("111111");

			CookieUtils.clearCookie(request);
			CookieUtils.writeCookie(request, userInfo);
		} else {
			errors.reject("-2", "用户名或密码错误");
		}
	}

	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		return new ModelAndView("redirect:/index.htm");
	}

}
