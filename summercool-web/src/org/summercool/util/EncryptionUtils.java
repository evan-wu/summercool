package org.summercool.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.summercool.beans.encrypt.Base64BeanDefinition;
import org.summercool.beans.encrypt.WebmacroBase64BeanDefinition;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public abstract class EncryptionUtils {

	protected static Log logger = LogFactory.getLog(EncryptionUtils.class);

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
			logger.error("error then create MD5", e);
			return source;
		}
	}
}
