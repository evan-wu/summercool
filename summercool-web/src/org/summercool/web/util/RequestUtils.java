package org.summercool.web.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * @Title: RequestUtils.java
 * @Package org.summercool.web.util
 * @Description: HttpServletRequest工具类
 * @author 简道
 * @date 2011-10-11 下午2:27:30
 * @version V1.0
 */
public class RequestUtils {

	/**
	 * 
	 * @Title: getRequestInputStream
	 * @Description: 获取HttpServletRequest中的流信息
	 * @author Administrator
	 * @param request
	 * @throws IOException
	 * @return byte[] 返回类型
	 */
	public static byte[] getRequestInputStream(HttpServletRequest request) throws IOException {
		ServletInputStream input = request.getInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int position = 0;

		while (true) {
			position = input.read(buffer);
			if (position == -1) {
				break;
			}
			output.write(buffer, 0, position);
		}

		return output.toByteArray();
	}
}
