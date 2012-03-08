package org.summercool.web.servlet.view.freemarker;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.summercool.view.freemarker.FreeMarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * 将字符串按指定长度截短并在其末尾追加“...” 计算方法：半角字符的长度为1，全角的长度为2。
 * 
 * @author:wb_changcheng.jia
 * @date:2010-7-30
 * @param string
 * @param length
 * @retrun 如果string的长度<=length返回string,否则返回截短后的str+"..."，str+"..."的总长度为length。
 */
public class FreeMarkerAbbreviateFunction extends FreeMarker implements TemplateMethodModelEx {

	@SuppressWarnings("unchecked")
	public Object exec(List arguments) throws TemplateModelException {
		// 参数合法性校验
		if (null == arguments || arguments.size() < 2) {
			throw new TemplateModelException(
					"abbreviate function need two argument, one is string another is int. And the second argument must be greate than 3.");
		}
		// 解包FreeMarker函数的参数
		List args = new ArrayList();
		for (Object object : arguments) {
			args.add((object instanceof TemplateModel) ? DeepUnwrap.unwrap((TemplateModel) object) : object);
		}
		arguments = args;
		// 参数是否为指定类型
		Object strObject = arguments.get(0);
		Object lenObject = arguments.get(1);
		if (!(strObject instanceof String) || !(lenObject instanceof BigDecimal)) {
			throw new TemplateModelException(
					"abbreviate function need two argument, one is string another is int. And the second argument must be greate than 3.");
		}
		String string = (String) strObject;
		Integer length = ((BigDecimal) lenObject).intValue();
		// 指定长度是为<=3，因为截短后的字符串需要追加“...”。
		if (length <= 3) {
			throw new TemplateModelException(
					"abbreviate function need two argument, one is string another is int. And the second argument must be greate than 3.");
		}
		// 字符串长度是否超过指定长度，Java中的全角字符长度为3，这里我们需要以2计算。
		int strLen = string.length();
		int bytesLen = string.getBytes().length;
		if ((bytesLen - (bytesLen - strLen) / 2) <= length) {
			return string;
		}
		// 字符串超过指定长度时，返回（截短后字符串+“...”），返回的字符串长度为参数指定长度。
		StringBuilder resultStr = new StringBuilder("");
		int currentLength = 3;// 初始化为“...”的长度
		int beginIndex = 0;
		int endIndex = 1;
		while (currentLength < length) {
			String subStr = string.substring(beginIndex, endIndex);
			try {
				if (subStr.getBytes("UTF-8").length == 1) {
					currentLength++;
				} else {
					currentLength += 2;
				}
			} catch (UnsupportedEncodingException e) {
			}
			//
			if (currentLength > length) {
				break;
			}
			resultStr.append(subStr);
			//
			beginIndex++;
			endIndex++;
		}
		return resultStr.append("...").toString();
	}
}
