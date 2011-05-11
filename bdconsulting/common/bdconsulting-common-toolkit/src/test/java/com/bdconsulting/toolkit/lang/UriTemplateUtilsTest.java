/**
 * 
 */
package com.bdconsulting.toolkit.lang;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Datetime   ： 2011-5-11 下午05:24:34<br>
 * Title      :  UriTemplateUtilsTest.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class UriTemplateUtilsTest {

	/**
	* 测试用
	* 
	* @author:shaochuan.wangsc
	* @date:2010-3-11
	* @param args
	* @throws UnsupportedEncodingException
	*/
	public static void main(String[] args) {
		String uriTemplate = "/{tid}c{page}p0t0h0v{keyword}q-large.htm";
		String uri = "/2503c2p0t0h0v2vq-large.htm";
		//
		Map<String, String> uriTemplateVariables = new HashMap<String, String>();
		System.out.println(UriTemplateUtils.analyseUri(uriTemplate, uri, uriTemplateVariables));
		//
		for (Map.Entry<String, String> entry : uriTemplateVariables.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

}
