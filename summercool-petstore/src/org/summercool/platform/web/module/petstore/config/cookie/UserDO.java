package org.summercool.platform.web.module.petstore.config.cookie;

import java.util.Map;

/**
 * @Title: UserDO.java
 * @Package com.gexin.platform.web.module.manager.config.cookie
 * @Description: TODO(添加描述)
 * @author Administrator
 * @date 2011-11-24 下午1:19:46
 * @version V1.0
 */
public class UserDO {

	private Long id;

	private String userName;

	private String password;

	private long groupId;

	private String groupName;

	private Map<String, String> authorities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Map<String, String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Map<String, String> authorities) {
		this.authorities = authorities;
	}

}
