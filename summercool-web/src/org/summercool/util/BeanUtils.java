package org.summercool.util;

import java.util.Collection;

import org.springframework.beans.FatalBeanException;

public class BeanUtils extends org.springframework.beans.BeanUtils {

	@SuppressWarnings("unchecked")
	public static <T> void copyCollection(Collection<?> sources, Collection<T> targets, Class<? extends Object> T){
		try {
			for (Object source : sources) {
				T target = (T) T.newInstance();
				copyProperties(source, target);
				targets.add(target);
			}
		} catch (Exception e) {
			throw new FatalBeanException("Could not copy properties from source to target", e);
		}
		
	}
	
}
