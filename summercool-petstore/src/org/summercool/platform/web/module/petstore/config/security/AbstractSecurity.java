package org.summercool.platform.web.module.petstore.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

public abstract class AbstractSecurity {

	private List<String> filterPaths = new ArrayList<String>();

	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	public AbstractSecurity() {
		urlPathHelper.setUrlDecode(false);
	}

	public List<String> getFilterPaths() {
		return filterPaths;
	}

	public void setFilterPaths(List<String> filterPaths) {
		this.filterPaths = filterPaths;
	}

	public boolean match(HttpServletRequest request) {
		for (String path : filterPaths) {
			if (matchHelp(path, request)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(String url) {
		for (String path : filterPaths) {
			if (antPathMatcher.match(path, url)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(HttpServletRequest request, List<String> filterPaths) {
		for (String path : filterPaths) {
			if (matchHelp(path, request)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchHelp(String pattern, HttpServletRequest request) {
		return antPathMatcher.match(pattern, urlPathHelper.getLookupPathForRequest(request));
	}
}
