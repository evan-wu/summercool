package org.summercool.web.views.string;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

public class StringView extends AbstractView {

	private static final String CONTENT_TYPE = "text/html";

	private static final String DEFAULT_CAHRSET = CharEncoding.UTF_8;

	private String html;

	private String encoding = DEFAULT_CAHRSET;

	public StringView(String html) {
		super();
		this.html = html;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (html == null || StringUtils.EMPTY.equals(html)) {
			return;
		}

		byte[] bytes = html.getBytes(encoding);
		response.setContentType(CONTENT_TYPE);
		response.setContentLength(bytes.length);
		response.setCharacterEncoding(encoding);
		response.getWriter().write(html);

	}

}
