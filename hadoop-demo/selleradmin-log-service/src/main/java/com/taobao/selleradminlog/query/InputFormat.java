/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.InvalidInputException;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * 
 * @author guoyou
 */
public class InputFormat extends TextInputFormat {
	
	private static final Log LOG = LogFactory.getLog(InputFormat.class);

	  private static final PathFilter hiddenFileFilter = new PathFilter(){
	      public boolean accept(Path p){
	        String name = p.getName(); 
	        return !name.startsWith("_") && !name.startsWith("."); 
	      }
	    };
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.lib.input.FileInputFormat#listStatus(org.apache.hadoop.mapreduce.JobContext)
	 * 修改FileInputFormat, <br>
	 * 1. 输入路径如果找不到，不抛异常，直接跳过。
	 * 2. 在遍历dir时，不应用自定义的filter，这个filter只过滤dir下面的文件。
	 */
	protected List<FileStatus> listStatus(JobContext job
	) throws IOException {
		List<FileStatus> result = new ArrayList<FileStatus>();
		Path[] dirs = getInputPaths(job);
		if (dirs.length == 0) {
			throw new IOException("No input paths specified in job");
		}

		List<IOException> errors = new ArrayList<IOException>();

		// creates a MultiPathFilter with the hiddenFileFilter and the
		// user provided one (if any).
		List<PathFilter> filters = new ArrayList<PathFilter>();
		filters.add(hiddenFileFilter);
		PathFilter jobFilter = getInputPathFilter(job);
		if (jobFilter != null) {
			filters.add(jobFilter);
		}
		PathFilter inputFilter = new MultiPathFilter(filters);

		for (int i=0; i < dirs.length; ++i) {
			Path p = dirs[i];
			FileSystem fs = p.getFileSystem(job.getConfiguration()); 
//			修改代码，不用自定义的过滤器过滤父文件夹。
//			FileStatus[] matches = fs.globStatus(p, inputFilter);
			FileStatus[] matches = fs.globStatus(p, hiddenFileFilter);
			if (matches == null) {
//				修改代码，文件找不到时，不抛异常，直接跳过。
//				errors.add(new IOException("Input path does not exist: " + p));
				LOG.warn("Input path does not exist: " + p);
			} else if (matches.length == 0) {
				errors.add(new IOException("Input Pattern " + p + " matches 0 files"));
			} else {
				for (FileStatus globStat: matches) {
					if (globStat.isDir()) {
						for(FileStatus stat: fs.listStatus(globStat.getPath(),
								inputFilter)) {
							result.add(stat);
						}          
					} else {
						result.add(globStat);
					}
				}
			}
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
		LOG.info("Total input paths to process : " + result.size()); 
		return result;
	}

	
	  /**
	   * Proxy PathFilter that accepts a path only if all filters given in the
	   * constructor do. Used by the listPaths() to apply the built-in
	   * hiddenFileFilter together with a user provided one (if any).
	   */
	  private static class MultiPathFilter implements PathFilter {
	    private List<PathFilter> filters;

	    public MultiPathFilter(List<PathFilter> filters) {
	      this.filters = filters;
	    }

	    public boolean accept(Path path) {
	      for (PathFilter filter : filters) {
	        if (!filter.accept(path)) {
	          return false;
	        }
	      }
	      return true;
	    }
	  }
	  
}
