package org.summercool.util;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtils {

	/**
	 * 查找Classpath下的相关资源
	 * 
	 * @param path
	 * @return
	 */
	public static Resource[] findClasspathReources(String path) {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		String resourcesPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path;
		Resource[] resources;
		try {
			resources = resourcePatternResolver.getResources(resourcesPath);
		} catch (IOException e) {
			throw new RuntimeException("查找 [" + path + "] 相关资源失败!", e);
		}
		return resources;
	}

}
