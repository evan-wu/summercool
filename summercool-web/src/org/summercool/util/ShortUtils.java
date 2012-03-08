package org.summercool.util;

import java.io.IOException;
import java.util.UUID;

public class ShortUtils {

	private static final String[] l = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A",
			"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };

	public static String tentoN(long value, int number) {
		if (number <= 1 || number > l.length) {
			throw new RuntimeException("Faild");
		}
		// 负数处理
		if (value < 0) {
			return "-" + tentoN(0 - value, number);
		}
		if (value < number) {
			return l[(int) value];
		} else {
			long n = value % (long) number;
			return (tentoN(value / number, number) + l[(int) n]);
		}
	}

	public static String build(long l) {
		return tentoN(l, 62);
	}

	public static String buildByUUID() {
		UUID uuid = UUID.randomUUID();
		long most = uuid.getMostSignificantBits();
		long least = uuid.getLeastSignificantBits();
		return build(Math.abs(most)) + build(Math.abs(least));
	}

	public static void main(String[] args) throws IOException {
		System.out.println(buildByUUID());
	}

}
