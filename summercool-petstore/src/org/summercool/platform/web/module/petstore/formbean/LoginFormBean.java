package org.summercool.platform.web.module.petstore.formbean;

/**
 * 登录页面FormBean
 * 
 * @Title: LoginFormBean.java
 * @Package org.summercool.platform.web.module.demo.formbean
 * @author Yanjh
 * @date 2012-2-13 下午2:08:12
 * @version V1.0
 */
public class LoginFormBean {
	private String userName;
	private String password;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *        the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *        the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
