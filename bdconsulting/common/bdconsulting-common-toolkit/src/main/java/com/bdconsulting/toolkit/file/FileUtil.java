package com.bdconsulting.toolkit.file;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 * 
 * Datetime   ： 2011-5-11 下午03:49:48<br>
 * Title      :  FileUtil.java<br>
 * Description:   <br>
 * Copyright  :  Copyright (c) 2011<br>
 * Company    :  大连尚嘉<br>
 * @author 简道
 *
 */
public class FileUtil {

	/**
	 * 通过File对象创建文件夹
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void mkdirs(File file) throws IOException {
		if (file.exists()) {
			// DO NOTHING!
		} else {
			File parent = file.getParentFile();
			if (file.getPath().indexOf(".") == -1) {
				parent = file;
			}
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file + "' could not be created");
				}
			}
		}
	}

}
