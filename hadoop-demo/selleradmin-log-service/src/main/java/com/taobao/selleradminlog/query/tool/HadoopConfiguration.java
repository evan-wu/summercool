/**
 * 
 */
package com.taobao.selleradminlog.query.tool;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.hadoop.conf.Configuration;

/**
 * Hadoop集群环境配置，有namenode, jobtracker等master机器的信息。
 * @author guoyou
 */
public class HadoopConfiguration {

	private String namenodeHost;
	
	private int namenodePort;
	
	private String userName;
	
	private String rawFileRelativePath;
	
	private String baseFileRelativePath;
	
	private Configuration configuration;
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public String getNamenodeHost() {
		return namenodeHost;
	}

	public void setNamenodeHost(String namenodeHost) {
		this.namenodeHost = namenodeHost;
	}

	public int getNamenodePort() {
		return namenodePort;
	}

	public void setNamenodePort(int namenodePort) {
		this.namenodePort = namenodePort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getRawFileRelativePath() {
		return rawFileRelativePath;
	}
	
	public void setRawFileRelativePath(String rawFileRelativePath) {
		this.rawFileRelativePath = rawFileRelativePath;
	}
	
	public String getBaseFileRelativePath() {
		return baseFileRelativePath;
	}

	public void setBaseFileRelativePath(String baseFileRelativePath) {
		this.baseFileRelativePath = baseFileRelativePath;
	}

	private volatile static ReentrantLock lock = new ReentrantLock();
	
	public static HadoopConfiguration getHadoopConfiguration() {
		if (instance == null) {
			synchronized (HadoopConfiguration.class) {
				if (instance == null) {
					lock.lock();
					instance = new HadoopConfiguration();
					
					Configuration inner = new Configuration();
					inner.set("hadoop.job.ugi", "hadoop,hadoop");
					inner.set("mapred.system.dir", "/home/hadoop/hadoop-datastore/mapred/system");
					
//					仅测试用。用来修改需要上传的文件的replication值。
//					inner.setInt("dfs.replication", 1);
					inner.setInt("dfs.datanode.socket.write.timeout", 0);
					
					instance.setConfiguration(inner);
					
					instance.setNamenodeHost("ubuntu");
					instance.setNamenodePort( 9000 );
					instance.setUserName("hadoop");
					instance.setRawFileRelativePath("selleradmin/raw/");
					instance.setBaseFileRelativePath("selleradmin/base/");
					lock.unlock();
				}
			}
		}
		
		if (lock.isLocked()) {
//			如果不加锁，这个线程有可能得到没有初始化完的实例。很可能它的getConfiguration()是null。
			lock.lock();
			lock.unlock();
		}
		
		return instance;
	}
	
	private static volatile HadoopConfiguration instance = null;

	
}
