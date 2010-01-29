package log.test.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import log.test.constancts.HadoopConstants;

import org.apache.hadoop.fs.Path;
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
	    	
	      // a cache storing the record writers for different output files.
			TreeMap<String, RecordWriter<K, V>> recordWriters = new TreeMap<String, RecordWriter<K, V>>();

			public void write(K key, V value) throws IOException,
					InterruptedException {

				// 默认按key命名
				String keyBasedPath = generateFileNameForKeyValue(key, value);

				// get the file name based on the input file name
				String finalPath = getInputFileBasedOutputFileName(myJob,
						keyBasedPath);

				// get the actual key
				K actualKey = generateActualKey(key, value);
				V actualValue = generateActualValue(key, value);
                //得到某一路径上文件的字符输出流
				RecordWriter<K, V> rw = this.recordWriters.get(finalPath);
				if (rw == null) {
					//如果没有得到，重新取一个
					rw = getBaseRecordWriter(myJob, finalPath);
					this.recordWriters.put(finalPath, rw);
				}
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
	   * Generate the outfile name based on a given name and the input file name. If
	   * the map input file does not exists (i.e. this is not for a map only job),
	   * the given name is returned unchanged. If the config value for
	   * "num.of.trailing.legs.to.use" is not set, or set 0 or negative, the given
	   * name is returned unchanged. Otherwise, return a file name consisting of the
	   * N trailing legs of the input file name where N is the config value for
	   * "num.of.trailing.legs.to.use".
	   * 
	   * @param job
	   *          the job config
	   * @param name
	   *          the output file name
	   * @return the outfile name based on a given anme and the input file name.
	   */
	  protected String getInputFileBasedOutputFileName(TaskAttemptContext job, String keyBasedPath) {
	    String infilepath = job.getConfiguration().get(HadoopConstants.MAPRED_INPUT_DIR);
	    if (infilepath == null) {
	      // if the map input file does not exists, then return the given name
	      return keyBasedPath;
	    }
	    int numOfTrailingLegsToUse = job.getConfiguration().getInt("mapred.outputformat.numOfTrailingLegs", 0);
	    if (numOfTrailingLegsToUse <= 0) {
	      return keyBasedPath;
	    }
	    
	    //TODO: 生成到不同路径
	    
	    
	    Path infile = new Path(infilepath);
	    Path parent = infile.getParent();
	    String midName = infile.getName();
	    Path outPath = new Path(midName);
	    for (int i = 1; i < numOfTrailingLegsToUse; i++) {
	      if (parent == null) break;
	      midName = parent.getName();
	      if (midName.length() == 0) break;
	      parent = parent.getParent();
	      outPath = new Path(midName, outPath);
	    }
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
