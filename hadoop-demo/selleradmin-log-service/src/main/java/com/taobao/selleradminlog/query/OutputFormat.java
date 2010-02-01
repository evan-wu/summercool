/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * 只用于只有一个reduce的情况。输出特定文件名的单一文件。
 * @author guoyou
 */
public class OutputFormat<K, V> extends
TextOutputFormat<K, V> {
	/**
	 * Get the default path and filename for the output format.
	 * @param context the task context
	 * @param extension an extension to add to the filename
	 * @return a full path $output/_temporary/$taskid/part-[mr]-$id
	 * @throws IOException
	 */
	@Override
	public Path getDefaultWorkFile(TaskAttemptContext context,
			String extension) throws IOException{
		FileOutputCommitter committer = 
			(FileOutputCommitter) getOutputCommitter(context);
//		return new Path(committer.getWorkPath(), getUniqueFile(context, "part", 
//				extension));
		return new Path(committer.getWorkPath(), getFileName());
	}
	
	public static String getFileName() {
		return "query-result";
	}
	
}
