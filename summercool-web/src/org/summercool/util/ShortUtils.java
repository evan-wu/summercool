package org.summercool.util;

import java.io.IOException;
import java.util.UUID;

public class ShortUtils {

	private static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String encoding(long num) {
		if (num < 1) {
			throw new RuntimeException("num must be greater than 0.");
		}
		StringBuilder sb = new StringBuilder();
		for (; num > 0; num /= 62) {
			sb.append(ALPHABET.charAt((int) (num % 62)));
		}
		return sb.toString();
	}

	public static long decoding(String str) {
		str = str.trim();
		if (str.length() < 1) {
			throw new RuntimeException("str must not be empty.");
		}
		long result = 0;
		for (int i = 0; i < str.length(); i++) {
			result += (long) (ALPHABET.indexOf(str.charAt(i)) * Math.pow(62, i));
		}
		return result;
	}

	public static String buildByUUID() {
		UUID uuid = UUID.randomUUID();
		long most = uuid.getMostSignificantBits();
		long least = uuid.getLeastSignificantBits();
		return encoding(Math.abs(most)) + encoding(Math.abs(least));
	}

	public static void main(String[] args) throws IOException {
		System.out.println(buildByUUID());
	}

}
