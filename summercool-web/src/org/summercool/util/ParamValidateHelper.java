package org.summercool.util;


/**
 * 参数验证帮助类
 * 
 * @Title: ParamValidateHelper.java
 * @Package com.gexin.platform.user.service.util
 * @Description: 参数验证帮助类
 * @author Yanjh
 * @date 2011-8-8 下午1:22:36
 * @version V1.0
 */
public class ParamValidateHelper {
	/**
	 * 验证参数类型是否合法
	 * 
	 * @Title: validate
	 * @Description: 验证参数类型是否合法
	 * @author Yanjh
	 * @param params
	 *        参数集合
	 * @param paramTypes
	 *        参数类型集合
	 * @return boolean 返回类型
	 */
	public static boolean validate(Object[] params, Class<?>... paramTypes) {
		if (params == null) {
			return paramTypes == null;
		} else if (paramTypes == null) {
			return false;
		}

		if (params.length == paramTypes.length) {
			for (int i = 0; i < params.length; i++) {
				if (!validate(params[i], paramTypes[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 验证参数类型是否合法
	 * 
	 * @Title: validate
	 * @Description: 验证参数类型是否合法
	 * @author Yanjh
	 * @param param
	 *        参数
	 * @param paramType
	 *        参数类型
	 * @return boolean 返回类型
	 */
	public static boolean validate(Object param, Class<?> paramType) {
		if (param == null) {
			if (paramType != null) {
				return !paramType.isPrimitive();
			}
			return true;
		}

		if (paramType == null) {
			throw new NullPointerException("paramType can't be null.");
		}

		boolean match = paramType.isInstance(param);
		if (!match) {
			if (param.getClass().isArray()) {
				return validate((Object[]) param, new Class<?>[] { paramType });
			}
		}
		return match;
	}

	public static void main(String[] args) {
		System.out.println(validate(new Object[] { "sd", null, 12 }, String.class, Integer.class, Integer.class));
		System.out.println(validate(new Object[] { 12 }, Integer.class));
	}
}
