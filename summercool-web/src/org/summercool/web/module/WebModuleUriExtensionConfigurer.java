package org.summercool.web.module;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO(添加描述)
 * 
 * @Title: WebModuleE.java
 * @Package org.summercool.web.module
 * @author Yanjh
 * @date 2012-2-13 下午10:07:09
 * @version V1.0
 */
public class WebModuleUriExtensionConfigurer {

	private List<String> uriExtensions = new ArrayList<String>();

	/**
	 * @return the uriExtensions
	 */
	public List<String> getUriExtensions() {
		return uriExtensions;
	}

	/**
	 * @param uriExtensions
	 *        the uriExtensions to set
	 */
	public void setUriExtensions(List<String> uriExtensions) {
		this.uriExtensions = uriExtensions;
	}

}
