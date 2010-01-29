/**
 * 
 */
package com.taobao.selleradminlog.agent.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IOUtils;

/**
 * 模拟log4j，生成测试用的日志文件。
 * @author guoyou
 */
public class DummyLogGenerator {
	
	private static Log log = LogFactory.getLog(DummyLogGenerator.class);

	/**
	 * 生成模拟的日志文件。
	 * 格式： lineNum	yyyyMMddHHmmss.SSS	mainId	subId	operationType	object1 object2 memo
	 * <br>行数与文件大小关系的经验值：
	 * <br>行数		大小
	 * <br>5000		1M
	 * <br>10000	2M
	 * <br>50000	10M
	 * <br>100000	20M
	 * <br>500000	100MM
	 * <br>5000000	1G
	 * @param filePathName 文件名
	 * @param lineCount  文件行数
	 * @param mainIdCount 主账号数量
	 * @param subIdCount 每个主卖家的子账号数量
	 * @throws IOException
	 */
	public static void createLogFile(String filePathName, int lineCount, int mainIdCount, int subIdCount) throws IOException{
//		lineNum	yyyyMMddHHmmss.SSS	mainId	subId	operationType	object1 object2 memo
//		e.g. 1	20100127174514.235	10144	4	operation_type_4	object1-96	object2-6	Following is the testing memo: 先来个中文测试。 Tab sign: $$tt, change line sign: $$nn, Following is another line $$nnNewLineStart...
		
		long start = System.currentTimeMillis();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
		
		File f = new File(filePathName);
		PrintWriter wr = null;
		try {
			wr = new PrintWriter( new BufferedWriter(new FileWriter(f)) );
			for (int i = 0; i < lineCount; i++ ) {
				wr.print( String.valueOf(i) );  // lineNum
				wr.print( '\t' );
				
				wr.print( dateFormat.format(new Date(System.currentTimeMillis())) );  // time
				wr.print( '\t' );
				
				wr.print( getRandomNum(10000, 10000 + mainIdCount) );  // mainId，最小 ID是10000。
				wr.print( '\t' );
				
				wr.print( getRandomNum(1, 1 + subIdCount) );  // subId
				wr.print( '\t' );
				
				wr.print( "operation_type_" + getRandomNum(0, 10) );  // operationType
				wr.print( '\t' );
				
				wr.print( "object1-" + getRandomNum(0, 100) );
				wr.print( '\t' );
				
				wr.print( "object2-" + getRandomNum(0, 100) );
				wr.print( '\t' );
				
				wr.println( "Following is the testing memo: 先来个中文测试。 Tab sign: $$tt, change line sign: $$nn, Following is another line $$nnNewLineStart..." );
			}
			
			wr.flush();
		} catch (IOException e) {
			throw e;
		}finally {
			IOUtils.closeStream(wr);
		}
		
		log.debug("Generated file " + filePathName + ", size=" + f.length()/1024 + "KB, lineCount=" + lineCount + ", time cost=" + (System.currentTimeMillis() - start));
		
	}
	
	/**
	 * 生成start, end范围内的随机整数。
	 * @param start
	 * @param end
	 * @return
	 */
	private static long getRandomNum(long start, long end) {
		return (long)(Math.random() * (end - start)) + start;
	}
	
	
	public static void main(String[] args) {
//		test
		String file = "d:\\temp\\tt11.log";
		log.info("Create file " + file);
		try {
			createLogFile(file, 1000, 1000, 10);
		} catch (IOException e) {
			log.error("IOException.", e);
		}
	}
	
}
