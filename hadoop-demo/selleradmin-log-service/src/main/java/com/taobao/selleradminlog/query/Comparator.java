/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;

import com.taobao.selleradminlog.Constants;
import com.taobao.selleradminlog.query.Line.LineFormatException;

/**
 * 根据查询条件中的排序条件，比较日志行。
 * @author guoyou
 */
public class Comparator extends Text.Comparator implements Configurable{
    
    private static Log LOG = LogFactory.getLog(Comparator.class);
    
    private Configuration conf;
    
    private QueryCondition condition = null;

    public void setConf(Configuration conf) {
    	this.conf = conf;
    	condition = DistributedQueryCondition.getQueryCondition(conf);
    }
    
    public int compare(byte[] b1, int s1, int l1,
    		byte[] b2, int s2, int l2) {

    	String sortField = condition.getSortField();
    	if (sortField == null) {
    		return super.compare(b1, s1, l1, b2, s2, l2);
    	}

    	try {
    		Text logline1 = new Text(); 
    		logline1.readFields(new DataInputStream(new ByteArrayInputStream(b1, s1, l1)));

    		Text logline2 = new Text(); 
    		logline2.readFields(new DataInputStream(new ByteArrayInputStream(b2, s2, l2)));
    		Line line1 = null;
    		Line line2 = null;
    		try {
    			line1 = new Line( logline1.toString() );
    			line2 = new Line( logline2.toString() );
    		}catch (LineFormatException e) {
				
			}

    		if (line1 == null || line2 == null) {
    			return super.compare(b1, s1, l1, b2, s2, l2);
    		}

    		int comparision = compareLines(line1, line2, sortField);
    		
    		return condition.isDescending() ? -comparision : comparision;

    	} catch (IOException ioe) {
    		LOG.fatal("Caught " + ioe);
    		return 0;
    	} catch (Exception e) {
    		LOG.fatal("Caught " + e);
    		return 0;
		}

    }
    
    /**
     * 比较日志行。
     * @param line1
     * @param line2
     * @param sortField
     * @return
     * @throws Exception 
     */
    private int compareLines(Line line1, Line line2, String sortField) throws Exception {
    	
    	int comparision = 0;
    	
    	if (Constants.COLUMN_NAME_TIME.equals(sortField)) {
    		comparision = line1.getTime().compareTo(line2.getTime());
		}else if (Constants.COLUMN_NAME_SUB_ID.equals(sortField)) {
			comparision = line1.getSubId().compareTo(line2.getSubId());
		}else if (Constants.COLUMN_NAME_OPERATION_TYPE.equals(sortField)) {
			comparision = line1.getOperationType().compareTo(line2.getOperationType());
		}else if (Constants.COLUMN_NAME_MEMO.equals(sortField)) {
			comparision = line1.getMemo().compareTo(line2.getMemo());
		}else {
			throw new Exception("Do not support the sort field: " + sortField);
		}
    	
    	if (comparision == 0) {
//    		当sortField字段相等时，如果直接返回相等，这两行会被合并，只保留一行的值，导致丢数据。
//    		因此，要再判断一下整行，只有当如果整行完全相等时，才返回0，这样也可以达到去重的效果了。
    		comparision = line1.getLine().compareTo(line2.getLine());
    	}
    	
    	return comparision;
    	
    }
    
//    不能注册。
//    static {                                        
//      // register this comparator
//      WritableComparator.define(Text.class, new Comparator());
//    }

	@Override
	public Configuration getConf() {
		return conf;
	}
  }
