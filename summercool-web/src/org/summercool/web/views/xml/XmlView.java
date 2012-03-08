package org.summercool.web.views.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

public class XmlView extends AbstractView {

	private static final String CONTENT_TYPE = "text/xml";

	private static final String DEFAULT_CAHRSET = CharEncoding.UTF_8;

	private String xml;

	private String encoding = DEFAULT_CAHRSET;

	public XmlView(String xml) {
		super();
		this.xml = xml;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (xml == null) {
			xml = StringUtils.EMPTY;
		}

		byte[] bytes = xml.getBytes(encoding);
		response.setContentType(CONTENT_TYPE);
		response.setContentLength(bytes.length);
		response.setCharacterEncoding(encoding);
		response.getWriter().write(xml);
	}

}
