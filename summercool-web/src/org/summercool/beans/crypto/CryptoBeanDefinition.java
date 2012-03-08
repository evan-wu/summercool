package org.summercool.beans.crypto;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-19
 *
 */
public interface CryptoBeanDefinition {
	/**
	 * 对string串进行加密
	 * 
	 * @param s 不能为null
	 * @return 加密后的string
	 */
	public String encrypt(String s);

	/**
	 * 对string串使用指定的编码进行加密
	 * 
	 * @param s 不能为null
	 * @param encoding 编码
	 * @return 加密后的string
	 */
	public String encrypt(String s, String encoding);

	/**
	 * 对string串进行解密
	 * 
	 * @param s 不能为null
	 * @return 解密后的string
	 */
	public String dectypt(String s);

	/**
	 * 对string串使用指定的编码进行解密
	 * 
	 * @param s 不能为null
	 * @param encoding 编码
	 * @return 解密后的string
	 */
	public String dectypt(String s, String encoding);

	public byte[] encrypt(byte[] bytes);

	public byte[] dectypt(byte[] bytes);

}
