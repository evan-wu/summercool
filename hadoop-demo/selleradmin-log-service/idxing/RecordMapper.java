package log.test;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;



/**
 * 默认输入格式TextInputFormat,拿到第一个token作为key即sellerId,行作为value
 *
 * 
 * @author wuhao
 * 
 *
 */
public class RecordMapper extends Mapper<Object, Text, Text, Text>{
	
	private Text id = new Text();

	@Override
	protected void map(Object key, Text value,Context context)
			throws IOException, InterruptedException {
		//one row one key, default partition
		StringTokenizer itr = new StringTokenizer(value.toString());
		if(itr.hasMoreTokens()){
			id.set(itr.nextToken());
			context.write(id, value);
		}
	}
	

}
