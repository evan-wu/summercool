package org.summercool.web.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.summercool.beans.crypto.CryptixCryptoLoader;
import org.summercool.beans.crypto.DefaultCryptoBeanDefinition;
import org.summercool.web.beans.cookie.CookieConfigurer;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 * 
 */
public class CookieModuleConfigurer implements InitializingBean {

	private static final String DEFAULT_CRYPTO_KEY = "0ajs!):1bkt@_2clu#+3dmv$[4enw%]5fox^{6gpy&}7hqz*;8ir~('9";

	@SuppressWarnings("unused")
	private CryptixCryptoLoader cryptixCryptoLoader;

	// CryptoKey
	private String cryptoKey;

	// CookieConfigurerList
	private List<CookieConfigurer> cookieConfigurerList;

	private Map<String, CookieConfigurer> clientName2CfgMap;

	private Map<String, CookieConfigurer> name2CfgMap;

	private DefaultCryptoBeanDefinition blowfishCrypto;

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}

	public List<CookieConfigurer> getCookieConfigurerList() {
		return cookieConfigurerList;
	}

	public void setCookieConfigurerList(List<CookieConfigurer> cookieConfigurerList) {
		this.cookieConfigurerList = cookieConfigurerList;
	}

	public Map<String, CookieConfigurer> getClientName2CfgMap() {
		return clientName2CfgMap;
	}

	public Map<String, CookieConfigurer> getName2CfgMap() {
		return name2CfgMap;
	}

	public void afterPropertiesSet() throws Exception {
		cryptixCryptoLoader = new CryptixCryptoLoader();
		// 设置blowfishCrypto开始
		if (!StringUtils.hasText(cryptoKey)) {
			cryptoKey = DEFAULT_CRYPTO_KEY;
		}
		blowfishCrypto = new DefaultCryptoBeanDefinition();
		blowfishCrypto.setAlgorithm("Blowfish");
		blowfishCrypto.setTransformation("Blowfish/CBC/NoPadding");
		blowfishCrypto.setKey(cryptoKey);
		blowfishCrypto.setProvider("CryptixCrypto");
		// 设置blowfishCrypto结束
		if (this.cookieConfigurerList != null) {
			for (CookieConfigurer cookieConfigurer : this.cookieConfigurerList) {
				if (cookieConfigurer.isEncrypted()) {
					cookieConfigurer.setCrypto(blowfishCrypto);
				}
			}
		} else {
			return;
		}
		// 设置name2CfgMap和clientName2CfgMap开始
		name2CfgMap = new HashMap<String, CookieConfigurer>(this.cookieConfigurerList.size());
		clientName2CfgMap = new HashMap<String, CookieConfigurer>(this.cookieConfigurerList.size());
		for (CookieConfigurer cfg : this.cookieConfigurerList) {
			if (cfg.getName() == null) {
				throw new NullPointerException("CookieConfigurer's name can't be null.");
			}
			name2CfgMap.put(cfg.getName(), cfg);
			if (cfg.getClientName() == null) {
				throw new NullPointerException("CookieConfigurer's client name can't be null.");
			}

			clientName2CfgMap.put(cfg.getClientName(), cfg);
		}
		name2CfgMap = Collections.unmodifiableMap(name2CfgMap);
		clientName2CfgMap = Collections.unmodifiableMap(clientName2CfgMap);
		// 设置name2CfgMap和clientName2CfgMap结束
	}

}
