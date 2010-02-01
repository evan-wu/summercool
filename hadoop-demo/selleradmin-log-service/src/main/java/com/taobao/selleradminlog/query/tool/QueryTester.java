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

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import com.taobao.selleradminlog.Constants;
import com.taobao.selleradminlog.query.MapReduceDriver;
import com.taobao.selleradminlog.query.QueryCondition;
import com.taobao.selleradminlog.query.QueryException;

/**
 * ≤‚ ‘¿‡
 * @author guoyou
 */
public class QueryTester {
	
	private static org.apache.commons.logging.Log log = LogFactory.getLog(QueryTester.class);
	
	public static void main(String[] args) throws QueryException {
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
