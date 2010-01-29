/**
 * 
 */
package com.taobao.selleradminlog.agent.tool;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.selleradminlog.agent.HDFSUploader;

/**
 * 用来测试raw文件上传的性能。
 * @author guoyou
 */
public class HDFSUploadTester {

	private static final Log log = LogFactory.getLog(HDFSUploadTester.class);
	
	private static String TMP_DIR = "d:/temp/";
	
	/**
	 * 上传文件。指定并发线程数和每个日志文件的大小。
	 * @param threadCount 并发线程数。
	 * @param sizeInMb 每个线程上传的测试文件的大小。单位M。
	 * @return 上传文件用的时间（毫秒）。去掉了生成测试文件所用的时间。
	 * @throws IOException
	 */
	public static long testUpload(int threadCount, double sizeInMb) throws IOException {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); // 实际中可能不会精确到秒。
		
		List<FutureTask<Exception>> threadList = new ArrayList<FutureTask<Exception>>();
		List<String> fileNameList = new ArrayList<String>();
		
//		先创建好所有线程用的测试文件，再启动这些线程 。
		for (int i = 0; i < threadCount; i++) {
			final String fileName = "agent_test_host" + i  + "-" + dateFormat.format(new Date()) + ".log";
			final String filePathName = TMP_DIR + fileName;
			int lineCount = (int) Math.round( sizeInMb * 5000 );
			DummyLogGenerator.createLogFile(filePathName, lineCount, 5000, 10);
			fileNameList.add(fileName);
			
			FutureTask<Exception> f = new FutureTask<Exception>(new Callable<Exception>() {
				@Override
				public Exception call() throws Exception {
					try {
						log.info("Uploading file " + filePathName);
						long start = System.currentTimeMillis();
						HDFSUploader.uploadRawFile(filePathName, fileName);
						log.info("Finished upload file " + filePathName + ", cost=" + (System.currentTimeMillis() - start));
					} catch (IOException e) {
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
		
//		把测试文件移到bak/目录，手动删除。
		for (String fileName : fileNameList) {
			File f = new File(TMP_DIR, fileName);
			
			File bakFile = new File(TMP_DIR + "bak/", fileName) ;
			if ( !bakFile.getParentFile().exists() ) {
				bakFile.getParentFile().mkdir();
			}
			f.renameTo(bakFile);
			
		}
		
		return cost;
		
	}
	
	/**
	 * 周期执行上传任务。指定周期执行的次数。
	 * @param threadCount 并发线程数。
	 * @param sizeInMb  每个线程上传的测试文件的大小。单位M。
	 * @param periodInMinute 周期。单位分钟。
	 * @param times 周期执行的次数。
	 * @throws IOException
	 */
	public static void testUploadPeriodly(int threadCount, double sizeInMb, long periodInMinute, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			log.info("start a period: " + i + ", periodInMinute=" + periodInMinute + ", times=" + times);
			long start = System.currentTimeMillis();
			testUpload(threadCount, sizeInMb);
			long cost = System.currentTimeMillis()-start;
			log.info("start a period: " + i + ", periodInMinute=" + periodInMinute + ", times=" + times + ", cost=" + cost );
			while ( (System.currentTimeMillis() - start) < periodInMinute*60000  ) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					log.error("InterruptedException.", e);
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		if (args == null || args.length<1) {
			showUsage();
			return;
		}
		
		String cmd = args[0];
		if ("upload".equalsIgnoreCase(cmd)) {
			try {
				int threadCount = 0;
				double sizeInMb = 1;
				long periodInMinute = 0;
				int times = 0;
				
				int cmdArgsCount = args.length - 1;
				
				if (cmdArgsCount == 1 ) {
					threadCount = Integer.valueOf( args[1] );
				}else if (cmdArgsCount == 2) {
					threadCount = Integer.valueOf( args[1] );
					sizeInMb = Double.valueOf( args[2] );
				}else if ( cmdArgsCount == 4) {
					threadCount = Integer.valueOf( args[1] );
					sizeInMb = Double.valueOf( args[2] );
					periodInMinute = Long.valueOf(args[3]);
					times = Integer.valueOf( args[4] );
				}else {
					showUsage();
					return;
				}
				
				log.info("Start upload. args=" + Arrays.toString(args) );
				
				long start = System.currentTimeMillis();
				long uploadCost = 0;
				if (periodInMinute >0) {
					testUploadPeriodly(threadCount, sizeInMb, periodInMinute, times);
				}else {
					uploadCost = testUpload(threadCount, sizeInMb);
				}
				
				long end = System.currentTimeMillis();
				
				showStatistics(args, start, end, periodInMinute>0 ? (end-start) : uploadCost);
				
				
			}catch (NumberFormatException e) {
				showUsage();
				return;
			} catch (IOException e) {
				log.error("IOException.", e);
			}
		}
		
	}
	
	private static void showStatistics(String[] args, long start, long end, long uploadCost) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		log.info("Upload finished. args=" + Arrays.toString(args) + ",start=" + dateFormat.format(new Date(start)) 
				+ ", end=" + dateFormat.format(new Date(end))
				+ ", cost=" + uploadCost);
		
	}

	private static void showUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage: \n upload threadCount \n upload threadCount sizeInMb \n upload threadCount sizeInMb periodInMinute times \n");
		System.out.println(sb.toString());
	}
	

}
