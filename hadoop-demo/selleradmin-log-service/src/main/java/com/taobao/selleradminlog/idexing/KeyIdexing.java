package log.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KeyIdexing {
	
	public static void main(String[] args){
		
		try {
		   run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void run() throws IOException, InterruptedException, ClassNotFoundException{	
		
		
		 Configuration conf = new Configuration();
		 Job job = new Job(conf, "key idexing");
		 conf.set("hadoop.job.ugi", "hadoop,hadoop");
		 job.setJarByClass(KeyIdexing.class);
		 job.setOutputKeyClass(Text.class);
		 job.setOutputValueClass(Text.class);
		 job.setReducerClass(RecordReducer.class);

		 //使用自定义输出格式
//		 job.setOutputFormatClass(MutipleRecordFileFormat.class);
			 
		 //使用时间戳生成输出目录
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		 String localDir = "";

		 String inputPath = "logs";//"selleradmin/raw";
		 String outputPath = new StringBuffer("selleradmin/base")
		                         .append(dateFormat.format(new Date()).toString())
		                         .toString();
		 
		 //可以指定多个输出目录，inputPath = "/input1,/input2"
		 //使用FileInputFormat.getPathStrings，可以得到当前所有的输入目录	 
		 FileInputFormat.addInputPath(job, new Path(inputPath));
		 
		 
		 //对应mapred.output.dir
		 FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		long start = System.currentTimeMillis(); 
		if(job.waitForCompletion(true)){
			//处理分析完成的目录
//			File file = new File(localDir);
//			String dest = "";
//			if(file.isDirectory()){
//				dest = file.getPath().lastIndexOf("/") == -1 ? "/" + localDir
//						: localDir;
//			    File destDir = new File(dest);
//			    if(!destDir.exists()){
//			    	destDir.mkdir();
//			    }
//			    
//				file.renameTo(destDir);
//			}else{
//				System.out.println("error local file");
//			}
		}
		System.out.println(System.currentTimeMillis()-start);
	}
	
	
	

}
