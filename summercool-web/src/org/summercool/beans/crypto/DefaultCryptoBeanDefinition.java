package org.summercool.beans.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.summercool.beans.encrypt.Base64BeanDefinition;

/**
 * 
 * @author:shaochuan.wangsc
 * @date:2010-3-28
 *
 */
public class DefaultCryptoBeanDefinition implements CryptoBeanDefinition {

	private String algorithm;

	private String transformation;

	private String key;

	private String provider;

	private ThreadLocal<JavaCrypto> local = new ThreadLocal<JavaCrypto>();

	private static String blank = "            ";

	private JavaCrypto getLocalCrypto() {
		JavaCrypto current = local.get();
		if (current == null) {
			current = new JavaCrypto(algorithm, transformation, key, provider);
			local.set(current);
		}
		return current;
	}

	private static class JavaCrypto {
		private Cipher enCipher;

		private Cipher deCipher;

		public JavaCrypto(String algorithm, String transformation, String key, String provider) {
			super();
			try {
				SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
				if (provider == null) {
					enCipher = Cipher.getInstance(transformation);
					deCipher = Cipher.getInstance(transformation);
				} else {
					enCipher = Cipher.getInstance(transformation, provider);
					deCipher = Cipher.getInstance(transformation, provider);
				}
				enCipher.init(Cipher.ENCRYPT_MODE, secretKey);
				deCipher.init(Cipher.DECRYPT_MODE, secretKey);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			} catch (NoSuchPaddingException e) {
				throw new RuntimeException(e);
			} catch (InvalidKeyException e) {
				throw new RuntimeException(e);
			} catch (NoSuchProviderException e) {
				throw new RuntimeException(e);
			}
		}

		public byte[] encrypt(byte[] bs) {
			try {
				return enCipher.doFinal(bs);
			} catch (IllegalBlockSizeException e) {
				throw new RuntimeException(e);
			} catch (BadPaddingException e) {
				throw new RuntimeException(e);
			}
		}

		public byte[] dectypt(byte[] bs) {
			try {
				return deCipher.doFinal(bs);
			} catch (IllegalBlockSizeException e) {
				throw new RuntimeException(e);
			} catch (BadPaddingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String dectypt(String s) {
		return dectypt(s, null);
	}

	public String dectypt(String s, String charset) {
		if (s == null) {
			throw new NullPointerException("dectypt string can't be null");
		}
		try {
			byte[] bs = Base64BeanDefinition.decode(s);
			bs = dectypt(bs);
			String back = null;
			if (charset != null) {
				back = new String(bs, charset);
			} else {
				back = new String(bs);
			}
			back = back.substring(8, back.length()).trim();
			return back;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] dectypt(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("dectypt bytes can't be null");
		}
		return this.getLocalCrypto().dectypt(bytes);
	}

	public String encrypt(String s) {
		return encrypt(s, null);
	}

	public String encrypt(String s, String charset) {
		if (s == null) {
			throw new NullPointerException("encrypt string can't be null");
		}
		s = blank + s + blank + blank.substring(0, (8 - s.length() % 8) % 8);
		try {
			byte[] bs;
			if (charset != null) {
				bs = this.encrypt(s.getBytes(charset));
			} else {
				bs = this.encrypt(s.getBytes());
			}

			return Base64BeanDefinition.encode(bs);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] encrypt(byte[] bytes) {
		if (bytes == null) {
			throw new NullPointerException("encrypt bytes can't be null");
		}
		return this.getLocalCrypto().encrypt(bytes);
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTransformation() {
		return transformation;
	}

	public void setTransformation(String transformation) {
		this.transformation = transformation;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
