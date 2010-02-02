/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.taobao.selleradminlog.query.Line.LineFormatException;

/**
 * 根据查询条件，过滤日志。排序不在Mapper里做。
 * 
 * @author guoyou
 */
public class FilterMapper extends Mapper<Object, Text, Text, IntWritable> {

	private static Log log = LogFactory.getLog(FilterMapper.class);
	
	private final static IntWritable one = new IntWritable(1);

	/**
	 *  查询条件。根据该条件过滤日志行。
	 */
	private QueryCondition condition = null;
	
	private List<Long> subIdsList = null;

	protected void setup(
			Context context)
			throws java.io.IOException, InterruptedException {
		
		log.debug("Setup in FilterMapper. condition=" + context.getConfiguration().get("taobao.query.condition"));
		
		condition = DistributedQueryCondition.getQueryCondition(context.getConfiguration());
		if (condition.getSubIds() != null) {
			subIdsList = Arrays.asList(condition.getSubIds());
		}
	};

	protected void map(Object key, Text value, Context context)
			throws java.io.IOException, InterruptedException {
		if (match(value, condition)) {
			context.write(value, one);
		}
	}

	private boolean match(Text value, QueryCondition condition) {
//		log.error("match, condition=" + DistributedQueryCondition.conditionToString(condition));
		
//		debug
		if (true) return true;
		
		Line line = null;
		try {
			line = new Line(value.toString());
		} catch (LineFormatException e) {
			log.error("LineFormatException", e);
			return false;
		}
		
		if (condition.getStartTime() != null || condition.getEndTime() != null) {
			Date time = line.getTime();
			
			log.error("time in line: " + time );
			
			if (condition.getStartTime() != null 
					&& time.before( condition.getStartTime())) {
				log.error("start time");
				return false;
			}
			if (condition.getEndTime() != null 
					&& time.after( condition.getEndTime())) {
				log.error("end time");
				return false;
			}
		}
		if (condition.getMainId() != null &&
				!condition.getMainId().equals( line.getMainId().equals(condition) )){
			log.error("mainId, " + line.getMainId());
			return false;
		}
		if (condition.getOperationType() != null 
				&& !condition.getOperationType().equals(line.getOperationType())) {
			log.error("operation type");
			return false;
		}
		
		if (condition.getSubIds() != null 
				&& !subIdsList.contains( line.getSubId())) {
			log.error("subIds");
			return false;
		}
		return true;
	};

}
