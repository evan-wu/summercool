/**
 * 
 */
package com.bdconsulting.toolkit.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bdconsulting.toolkit.encrypt.Base64BeanDefinition;
import com.bdconsulting.toolkit.encrypt.WebmacroBase64BeanDefinition;

/**
 * Datetime   ： 2011-5-11 下午05:06:42<br>
 * Title      :  EncryptionUtils.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class EncryptionUtils {

	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);

	public static String createMD5(String source) {
		if (source == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes());
			String ReturnPassword = WebmacroBase64BeanDefinition.encode(md.digest());
			return Base64BeanDefinition.encode(ReturnPassword.getBytes());
		} catch (NoSuchAlgorithmException e) {
			logger.error("创建MD5错误", e);
			return source;
		}
	}
}
