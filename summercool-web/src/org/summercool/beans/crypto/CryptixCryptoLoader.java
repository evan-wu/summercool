package org.summercool.beans.crypto;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public class CryptixCryptoLoader {

	static {
		java.security.Security.addProvider(new cryptix.jce.provider.CryptixCrypto());
	}

}
