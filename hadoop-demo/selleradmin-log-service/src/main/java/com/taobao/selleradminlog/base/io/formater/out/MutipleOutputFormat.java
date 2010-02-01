package log.test.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import log.test.constancts.HadoopConstants;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 用新API重写多文件输出格式
 * @author wuhao
 *
 * @param <K>
 * @param <V>
 */
public abstract class MutipleOutputFormat<K, V> extends FileOutputFormat<K, V>{

	
	
	/**
	 * 创建一个复合的recordwriter来输出key/vale的值到不同的文件里
	 * @return a composite record writer
	 * @throws IOException
	 */
    
	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext job) throws IOException {

//	    final String myName = generateLeafFileName(name);
	    //任务的上下文
	    final TaskAttemptContext myJob = job;

	    return new RecordWriter<K, V>() {
	    	
	        //缓存对于path的字符输出流
			TreeMap<String, RecordWriter<K, V>> recordWriters = new TreeMap<String, RecordWriter<K, V>>();

			/* 
			 * 调用多次定制的写
			 */
			public void write(K key, V value) throws IOException,
					InterruptedException {

				// 默认按key命名
				String keyBasedPath = generateFileNameForKeyValue(key, value);

				// 定义输出文件名
				String finalPath = getOutFileBasedOutputFileName(myJob,
						keyBasedPath);

				K actualKey = generateActualKey(key, value);
				V actualValue = generateActualValue(key, value);
                //得到某一路径上文件的字符输出流
				RecordWriter<K, V> rw = this.recordWriters.get(finalPath);
				if (rw == null) {
					//如果没有得到，重新取一个
					rw = getBaseRecordWriter(myJob, finalPath);
					this.recordWriters.put(finalPath, rw);
					System.out.println(finalPath);
					System.out.println(actualKey+":"+actualValue);
				}
				System.out.println("rw" + rw);
				rw.write(actualKey, actualValue);
			};

		@Override
		public void close(TaskAttemptContext context) throws IOException,
				InterruptedException {
			Iterator<String> keys = this.recordWriters.keySet().iterator();
	        while (keys.hasNext()) {
	          RecordWriter<K, V> rw = this.recordWriters.get(keys.next());
	          rw.close(context);
	        }
	        this.recordWriters.clear();	   	
		};
	    };
	  }

	  /**
	   * Generate the leaf name for the output file name. The default behavior does
	   * not change the leaf file name (such as part-00000)
	   * 
	   * @param name
	   *          the leaf file name for the output file
	   * @return the given leaf file name
	   */
	  protected String generateLeafFileName(String name) {
	    return name;
	  }

	  /**
	   * Generate the file output file name based on the given key and the leaf file
	   * name. The default behavior is that the file name does not depend on the
	   * key.
	   * 
	   * @param key
	   *          the key of the output data
	   * @param name
	   *          the leaf file name
	   * @return generated file name
	   */
	  protected String generateFileNameForKeyValue(K key, V value) {
	    return key.toString();
	  }

	  /**
	   * Generate the actual key from the given key/value. The default behavior is that
	   * the actual key is equal to the given key
	   * 
	   * @param key
	   *          the key of the output data
	   * @param value
	   *          the value of the output data
	   * @return the actual key derived from the given key/value
	   */
	  protected K generateActualKey(K key, V value) {
	    return key;
	  }
	  
	  /**
	   * Generate the actual value from the given key and value. The default behavior is that
	   * the actual value is equal to the given value
	   * 
	   * @param key
	   *          the key of the output data
	   * @param value
	   *          the value of the output data
	   * @return the actual value derived from the given key/value
	   */
	  protected V generateActualValue(K key, V value) {
	    return value;
	  }
	  

	  /**
	   * 根据输出文件的路径，获取父路径，生成输出文件的最终名字，提供给不同的writer
	   * 如果map的输入路径不存在，返回默认文件名
	   * 
	   * @param job
	   *          the job config
	   * @param keyBasedPath
	   *          the output file name
	   * @return the outfile name based on a given name and the input file name.
	   */
	  protected String getOutFileBasedOutputFileName(TaskAttemptContext job, String keyBasedPath) {
	    StringBuffer outfilepath = new StringBuffer(job.getConfiguration().get(HadoopConstants.MAPRED_OUTPUT_DIR));
	    if (outfilepath == null) {
	      // if the map input file does not exists, then return the given name
	      return keyBasedPath;
	    }
	    //相当于selleradmin/$timestamp$
	    
	    String hashdir = new Long(keyBasedPath.hashCode()
				% (HadoopConstants.DEFAULT_PARTITION)).toString();
	    
	    
	    outfilepath = outfilepath.append("/")
	                             .append(hashdir);
	    
	   
	    
	    System.out.println("outfilepath:"+outfilepath);
	    Path outfile = null;                     
	   
//	    try {
//			DFSClient dfsClient = new DFSClient(job.getConfiguration());
//			FileStatus fst = dfsClient.getFileInfo(outfilepath.toString());
//			 if(fst == null || !fst.isDir()){
//				 dfsClient.mkdirs(outfilepath.toString());	 
//			 }
//			 else{
				 outfile = new Path(outfilepath.append("/").append(keyBasedPath)
						.toString());
				
//			 }
//		} catch (IOException e) {
//			System.out.println(e);
//		}  
		//该路径最后的名字
		String midName = outfile.getName();
		Path outPath = new Path(midName);
	    return outPath.toString();
	  }

	  /**
	   * 
	   * @param fs
	   *          the file system to use
	   * @param job
	   *          a job conf object
	   * @param name
	   *          the name of the file over which a record writer object will be
	   *          constructed
	   * @param arg3
	   *          a progressable object
	   * @return A RecordWriter object over the given file
	   * @throws IOException
	   */
	  abstract protected RecordWriter<K, V> getBaseRecordWriter(
			  TaskAttemptContext job, String name) throws IOException;

 
}
