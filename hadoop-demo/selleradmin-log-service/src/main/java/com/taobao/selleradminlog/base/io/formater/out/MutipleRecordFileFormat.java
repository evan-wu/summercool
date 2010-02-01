package com.taobao.selleradminlog.base.io.formater.out;

import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * 
 * @author wuhao
 *
 * @param <K>
 * @param <V>
 */
public class MutipleRecordFileFormat<K extends Writable, V extends Writable> extends MutipleOutputFormat<K,V>{
	
	/**
	 * 默认采取TextOutputFormat的recordwriter方式
	 * 每行key+/t+value
	 */
	private TextOutputFormat<K,V> theTextOutputFormat;


	@Override
	protected RecordWriter<K, V> getBaseRecordWriter(TaskAttemptContext job,
			String name){
		if (null == theTextOutputFormat) {
			this.theTextOutputFormat = new TextOutputFormat<K, V>();
		}
		try {
			return this.theTextOutputFormat.getRecordWriter(job);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
		return null;
	}
	
	
	/* 
	 * 重写生成文件的名字
	 *
	 */
	@Override
	protected String generateFileNameForKeyValue(K key, V value){
		return super.generateFileNameForKeyValue(key, value);	
	}
	

}
