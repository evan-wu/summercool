/**
 * 
 */
package com.bdconsulting.toolkit.lang;

import com.bdconsulting.toolkit.zip.StringZip;

/**
 * Datetime   ： 2011-5-5 下午09:50:08<br>
 * Title      :  StringZipTest.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class StringZipTest {

	/**
	 * 有时候越压越大，建议要自己判断一下，^_^
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String str = "简道简道简道简道简道简道简道简道简道简道简道简道";
		System.out.println(StringZip.zipString(str));
		System.out.println(StringZip.unzipString("eNp7vq7hZePk5xSTAAIJMNk="));
	}

}
