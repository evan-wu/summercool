package org.summercool.util;

import org.summercool.beans.crypto.CryptixCryptoLoader;
import org.summercool.beans.crypto.DefaultCryptoBeanDefinition;

public class CryptoUtils {

	private static final String DEFAULT_CRYPTO_KEY = "0ajs!):1bkt@_2c#+3dmv$[4enw%]5fox^{6gpy&}7hqz*;8ir~('9";
	
	
	private static CryptoHandler CRYPTO_HANDLER = new CryptoHandler();
	
	public static class CryptoHandler{
		
		@SuppressWarnings("unused")
		private CryptixCryptoLoader cryptixCryptoLoader;
		
		private DefaultCryptoBeanDefinition blowfishCrypto;
		
		public CryptoHandler(){
			init();
		}
		
		public void init(){
			cryptixCryptoLoader = new CryptixCryptoLoader();
			//
			String cryptoKey = DEFAULT_CRYPTO_KEY;
			//
			blowfishCrypto = new DefaultCryptoBeanDefinition();
			blowfishCrypto.setAlgorithm("Blowfish");
			blowfishCrypto.setTransformation("Blowfish/CBC/NoPadding");
			blowfishCrypto.setKey(cryptoKey);
			blowfishCrypto.setProvider("CryptixCrypto");
			//
		}
		
		public String encrypt(String value){
			return blowfishCrypto.encrypt(value);
		}
		
		public String dectypt(String value){
			return blowfishCrypto.dectypt(value);
		}
		
	}
	
	public static String encrypt(String value){
		return CryptoUtils.CRYPTO_HANDLER.encrypt(value);
	}
	
	public static String dectypt(String value){
		return CryptoUtils.CRYPTO_HANDLER.dectypt(value);
	}
	
}
