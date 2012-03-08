package org.summercool.web.views.csv;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

public class CsvView extends AbstractView {

	private static final String CONTENT_TYPE = "text/comma-separated-values";

	private static final String DEFAULT_CAHRSET = CharEncoding.UTF_8;

	private String csv;

	private String filename;

	private String encoding = DEFAULT_CAHRSET;

	public CsvView(String csv, String filename) {
		super();
		this.csv = csv;
		this.filename = filename;
	}

	public CsvView(String csv, String filename, String encoding) {
		super();
		this.csv = csv;
		this.filename = filename;
		this.encoding = encoding;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (csv == null) {
			csv = StringUtils.EMPTY;
		}

		byte[] bytes = csv.getBytes(encoding);
		response.setContentType(CONTENT_TYPE);
		response.setContentLength(bytes.length);
		response.setCharacterEncoding(encoding);

		response.getWriter().write(csv);
		if (StringUtils.isNotBlank(filename)) {
			// 需要缓存页面信息时则设置该值为"public，max-age"
			response.setHeader("Content-disposition", "attachment;filename=" + filename);
			response.setHeader("Cache-Control", "public,max-age=0");
			response.setHeader("Pragma", "Pragma");
		}

	}

}
