package com.taobao.selleradminlog.idexing;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RecordReducer extends Reducer<Text, Text, Text, Text>{

	
	private Text result = new Text(); 
	
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {

		StringBuffer tmp = new StringBuffer();
		for(Text val : values){
			tmp.append(val.toString());
		}
		
		result.set(tmp.toString());
		System.out.println("recordReducer:"+ "key="+key +": value="+ result);
	    context.write(key, result);	
		
	}
	
	

}
