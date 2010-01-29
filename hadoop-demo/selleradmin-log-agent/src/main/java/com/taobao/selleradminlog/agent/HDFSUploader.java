/**
 * 
 */
package com.taobao.selleradminlog.agent;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * upload file from local to HDFS.
 * @author guoyou
 */
public class HDFSUploader {

	/**
	 * Upload local file to hdfs's raw directory.
	 * @param localPathName absolute path and name of local file.
	 * @param dstFileName name of destination file. 
	 * @throws IOException
	 */
	public static void uploadRawFile(String localPathName, String dstFileName) throws IOException{
		HadoopConfiguration conf = HadoopConfiguration.getHadoopConfiguration();
		
//		e.g. "hdfs://ubuntu:9000/user/hadoop/selleradmin/raw/a.log";
		String dstUri = "hdfs://" + conf.getNamenodeHost() + ":" + conf.getNamenodePort() 
		+ "/user/" + conf.getUserName() + "/" + conf.getRawFileRelativePath()
		+ dstFileName;
		
        FileSystem fs = FileSystem.get(URI.create(dstUri),conf.getConfiguration() );
        fs.copyFromLocalFile(new Path(localPathName) , new Path(dstUri) );
	}
	
}
