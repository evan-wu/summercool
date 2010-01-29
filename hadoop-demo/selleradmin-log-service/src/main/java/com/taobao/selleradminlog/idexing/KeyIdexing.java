package log.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

public class KeyIdexing {
	
	
	
//	private void setPath(){
//		
//		FileSystem fileSys;
//		fileSys.
//		
//	}
	
	
	public static int run() throws IOException, InterruptedException, ClassNotFoundException{	
		
		 Configuration conf = new Configuration();
		 Job job = new Job(conf, "key idexing");
		 job.setJarByClass(KeyIdexing.class);
		 job.setOutputKeyClass(Text.class);
		 job.setOutputValueClass(Text.class);
		 job.setReducerClass(RecordReducer.class);
//		 job.setOutputFormatClass(MultipleOutputFormat.class);
		 //default
	
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		 String inputPath = "selleradmin/raw";
		 String outputPath = new   StringBuffer("sellleradmin/")
		                           .append(dateFormat.format(new Date()).toString())
		                           .toString();
		 FileInputFormat.addInputPath(job, new Path(inputPath));
		
		 
		 DFSClient dfsClient = new DFSClient(conf);
		 FileStatus fst = dfsClient.getFileInfo(outputPath);
		 if(fst == null || !fst.isDir()){
			 dfsClient.mkdirs(outputPath);	 
		 }
//		 job.setOutputFormatClass(SequenceFileOutputFormat.class);
		//TODO: set output files
		 FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		 
		 return job.waitForCompletion(true)?1:0; 		 	
	}
	
	

}
