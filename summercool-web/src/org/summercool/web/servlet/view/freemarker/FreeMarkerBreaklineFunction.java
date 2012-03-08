package org.summercool.web.servlet.view.freemarker;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.summercool.view.freemarker.FreeMarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * FreeMarker函数，将HTML后的换行符反转
 * 
 * @author li.dapeng
 * @date 2010-08-11
 * 
 */
public class FreeMarkerBreaklineFunction extends FreeMarker implements TemplateMethodModelEx {

	private static final Pattern BREAKLINE_PATTERN = Pattern.compile(
			"&lt;(\\p{javaWhitespace})*(\\p{javaWhitespace})*(br)(\\p{javaWhitespace})*(/)?(\\p{javaWhitespace})*&gt;",
			Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		if (arguments == null || arguments.isEmpty()) {
			return StringUtils.EMPTY;
		}

		if (arguments.size() != 1) {
			throw new TemplateModelException("The method must have one argument.");
		}

		Object argument = arguments.get(0);

		if (!(argument instanceof TemplateModel)) {
			throw new TemplateModelException("Illegal Argument.");
		}

		argument = DeepUnwrap.unwrap((TemplateModel) argument);

		if (!(argument instanceof CharSequence)) {
			throw new TemplateModelException("The method's argument must be CharSequence.");
		}

		return BREAKLINE_PATTERN.matcher((CharSequence) argument).replaceAll("<br/>").replace(" ", "&nbsp;");
	}
}
