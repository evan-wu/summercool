/**
 * 
 */
package com.taobao.selleradminlog.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 把value置为null，只输出key.
 * 
 * @author guoyou
 */
public class NullValueReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	Log log = LogFactory.getLog(NullValueReducer.class);
	
	protected void reduce(
			Text key,
			java.lang.Iterable<IntWritable> values,
			Context context)
			throws java.io.IOException, InterruptedException {
		
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		if (sum > 1) {
			log.warn("Some lines are duplicated. Keep only one. sum=" + sum + ", line=" + key.toString());
		}
		
		context.write(key, null);
		
	}

}
