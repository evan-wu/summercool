package log.test;

import java.io.IOException;

import log.test.util.MutipleOutputFormat;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Progressable;

import org.apache.hadoop.io.Writable;  

/**
 * @author wuhao
 *
 * @param <K>
 * @param <V>
 */
public class MutipleRecordFileFormat<K extends Writable, V extends Writable> extends MutipleOutputFormat<K,V>{
	
	private TextOutputFormat<K,V> theTextOutputFormat;

	@Override
	protected RecordWriter getBaseRecordWriter(FileSystem fs, Job job,
			String name, Progressable arg3) throws IOException {
		 if (theTextOutputFormat == null) {  
		      theTextOutputFormat = new TextOutputFormat<K, V>();  
		    }  
//		    return theTextOutputFormat.getRecordWriter(job);  
		return null;  
	}

	@Override
	public RecordWriter getRecordWriter(TaskAttemptContext job)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected String generateFileNameForKeyValue(K key, V value, String name) {  
        return name + "_" + value.toString();  
    }  

}
