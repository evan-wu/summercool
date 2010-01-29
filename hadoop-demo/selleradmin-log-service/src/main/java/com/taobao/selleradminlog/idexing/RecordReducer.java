package log.test;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class RecordReducer extends Reducer<Text, Text, Text, Text>{

	
	private Text result = new Text(); 
	
	protected void reduce(Text key, Iterable<Text> values,Context context)
			throws IOException, InterruptedException {

		StringBuffer tmp = new StringBuffer();
		for(Text val : values){
			tmp.append(val.toString())
			   .append("\n");	
		}
		
		result.set(tmp.toString());
	    context.write(key, result);	
		
	}
	
	

}
