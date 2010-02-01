/**
 * 
 */
package com.taobao.selleradminlog.query;

import java.util.Date;

/**
 * 查询条件。字段为null，表示不查询该条件。
 * @author guoyou
 */
public class QueryCondition {

	private Long mainId;
	
	private Long[] subIds;
	
	private Date startTime;
	
	private Date endTime;
	
	private String operationType;
	
	/**
	 * 排序的字段
	 */
	private String sortField;
	
	/**
	 * 是否用降序。
	 */
	private boolean descending = false;

	public Long getMainId() {
		return mainId;
	}

	public void setMainId(Long mainId) {
		this.mainId = mainId;
	}

	public Long[] getSubIds() {
		return subIds;
	}

	public void setSubIds(Long[] subIds) {
		this.subIds = subIds;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean desc) {
		this.descending = desc;
	}
	
	
	
}
