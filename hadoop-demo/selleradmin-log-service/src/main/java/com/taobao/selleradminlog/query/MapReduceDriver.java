/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.taobao.selleradminlog.Constants;

/**
 * 执行查询的map-reduce任务。
 * @author guoyou
 */
public class MapReduceDriver {

	private static org.apache.commons.logging.Log log = LogFactory.getLog(MapReduceDriver.class);
	
	/**
	 * 根据条件，查询日志。返回查询结果文件的PathName。
	 * @param condition
	 * @return output path and name.
	 * @throws Exception
	 */
	public static String query(QueryCondition condition) throws QueryException {
		try {

			Configuration conf = new Configuration();
			conf.set("hadoop.job.ugi", "hadoop,hadoop");
			conf.set("mapred.system.dir", "/home/hadoop/hadoop-datastore/mapred/system");
			conf.setInt("dfs.datanode.socket.write.timeout", 0);
			conf.set("mapred.child.java.opts", "-Xmx200m -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n");
			
			//		设置查询条件。
			DistributedQueryCondition.setQueryCondition(condition, conf);

			Job job = new Job(conf, "log query");
			job.setJarByClass(MapReduceDriver.class);

			//		 根据查询条件过滤。
			job.setMapperClass(FilterMapper.class);
			job.setReducerClass(NullValueReducer.class);
			job.setNumReduceTasks(1);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);  // null

			job.setInputFormatClass(InputFormat.class);
			FileInputFormat.setInputPathFilter(job, FileFilter.class );  // 
			FileInputFormat.setInputPaths(job, getInputPaths(condition) );

			job.setOutputFormatClass(OutputFormat.class);
			Path outputPath =  getOutputPath(condition);
			FileOutputFormat.setOutputPath(job, outputPath );

			//		 根据查询条件排序。
			job.setSortComparatorClass(Comparator.class); 

			job.waitForCompletion(true);

			return new Path(outputPath, OutputFormat.getFileName()).toString();
		}catch (IOException e) {
			throw new QueryException("IOException", e);
		} catch (InterruptedException e) {
			throw new QueryException("InterruptedException", e);
		} catch (ClassNotFoundException e) {
			throw new QueryException("ClassNotFoundException", e);
		}
	}
	
	private static Path getOutputPath(QueryCondition condition) {
		return new Path("selleradmin/workspace/query/result/" + 
				condition.getMainId() + 
				"-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format( new Date(System.currentTimeMillis())) + 
				"-" + (int)(Math.random() * 1000) );
	}


	/**
	 * Get input paths for the the map-reduce job by QueryCondition. 
	 * @param condition
	 * @return
	 */
	private static Path[] getInputPaths(QueryCondition condition) {
		Long mainId = condition.getMainId();
		Date startTime = condition.getStartTime();
		Date endTime = condition.getEndTime();
		
		if (endTime == null) {
//			默认截止到当前时间。
			endTime = new Date( System.currentTimeMillis() );
		}
		
		long oneday = 24*60*60*1000;
		
//		默认提供30天的。
		int days = startTime == null ? 30 : (int)( ( endTime.getTime() - startTime.getTime() )/oneday + 1);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		Path[] paths = new Path[days];
		
		int hash = hash(mainId) % 128;
		
		long time = endTime.getTime();
		for (int i = 0; i < days; i++) {
			paths[i] = new Path("selleradmin/base/" + dateFormat.format(new Date(time )) + "/" + hash);
			time -= oneday;
		}
		
		return paths;
	}
	
	public static int hash(Long id) {
		return id.hashCode();
	}


	/**
	 * 过滤<hash(id)%128>/里面的文件，只接受查询条件里指定的mainId的日志。
	 * 这个filter在FileInputFormat的会实现中，会同时过滤file和file的parent dir。
	 * 而本项目中自定义的InputFormat只会用这个filter过滤dir下面的file。因此这个实现不会把dir过滤掉。
	 * @author guoyou
	 */
	public static class FileFilter extends Configured implements PathFilter {
		@Override
		public boolean accept(Path path) {
			QueryCondition c = DistributedQueryCondition.getQueryCondition( getConf() );
			return path.getName().contains( String.valueOf( c.getMainId() ) );
		}
	}
	
	public static void main(String[] args) throws Exception {
		QueryCondition c = new QueryCondition();
		c.setMainId(1026L);
		try {
			c.setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").parse("20100120183059"));
			c.setEndTime(new SimpleDateFormat("yyyyMMddHHmmss").parse("20100130183059"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setSortField(Constants.COLUMN_NAME_TIME);
		c.setDescending(true);
		String result = query(c);
		log.info("Query success, result=" + result);
	}
	
}
