/**
 * 
 */
package com.taobao.selleradminlog.query.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.taobao.selleradminlog.Constants;
import com.taobao.selleradminlog.query.MapReduceDriver;
import com.taobao.selleradminlog.query.QueryCondition;
import com.taobao.selleradminlog.query.QueryException;

/**
 * 测试类
 * @author guoyou
 */
public class QueryTester {
	
	private static final org.apache.commons.logging.Log log = LogFactory.getLog(QueryTester.class);
	
	public static void main(String[] args) throws QueryException {
		int threadCount = 1;
		final QueryCondition c = new QueryCondition();
		c.setMainId(1026L);
		try {
//			日期对应文件的大小：
//			2010-01-10 to 2010-01-19:	10M
//			2010-01-20 to 2010-01-29:	0.1M
//			2010-01-01 to 2010-01-09:	1M
//			2010-02-01 :	100M
//			2010-02-02 :	0.01M
			
			c.setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").parse("20100101183059"));
			c.setEndTime(new SimpleDateFormat("yyyyMMddHHmmss").parse("20100101183059"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setSortField(Constants.COLUMN_NAME_TIME);
		c.setDescending(true);
		
		List<FutureTask<Exception>> threadList = new ArrayList<FutureTask<Exception>>();
		for (int i = 0; i < threadCount; i++) {
			FutureTask<Exception> f = new FutureTask<Exception>(new Callable<Exception>() {
				@Override
				public Exception call() throws Exception {
					try {
						queryInOneThread(c);
					} catch (QueryException e) {
						return e;
					}
					return null;
				}
				
			});
			
			threadList.add(f);
		}
		
//		启动线程
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		long start = System.currentTimeMillis();
		for (FutureTask<Exception> f : threadList) {
			executor.execute(f);
		}
		
//		等待所有线程结束。
		for (FutureTask<Exception> f : threadList){
			try {
				Exception e = f.get();
				if (e != null) {
					log.error("Exception.", e);
				}
			} catch (InterruptedException e) {
				log.error("InterruptedException.", e);
			} catch (ExecutionException e) {
				log.error("ExecutionException.", e);
			}
		}
		
		long cost = System.currentTimeMillis() - start;
		
		executor.shutdown();
		
		log.info("ThreadCount=" + threadCount + ", total time span=" + cost);
		
	}
	
	public static void queryInOneThread(QueryCondition c) throws QueryException {
		long start = System.currentTimeMillis();
		String result = MapReduceDriver.query(c);
		long end = System.currentTimeMillis();
		log.info("Query result file generated. result=" + result + ", cost=" + (end - start));
		
		HadoopConfiguration conf = HadoopConfiguration.getHadoopConfiguration();
		
//		e.g. "hdfs://ubuntu:9000/user/hadoop/selleradmin/base/20100128/2/1026-20100128.log";
		String dstUri = "hdfs://" + conf.getNamenodeHost() + ":" + conf.getNamenodePort() 
		+ "/user/" + conf.getUserName() + "/" + result;
		
        long linecount = 0; 
        BufferedReader d = null;
        try {
        	FileSystem fs = FileSystem.get(URI.create(dstUri),conf.getConfiguration() );
        	d = new BufferedReader(new InputStreamReader(fs.open(new Path(result))));
        	String line = null;
        	while ( (line = d.readLine()) != null ) {
        		linecount++;
        	}
        }catch (IOException e) {
			IOUtils.closeStream(d);
		}
        
        log.info("Query success. lineCount=" + linecount + ", cost=" + (System.currentTimeMillis() - start));
	}
	
	
}
