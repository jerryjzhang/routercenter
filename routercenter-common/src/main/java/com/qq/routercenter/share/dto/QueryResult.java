package com.qq.routercenter.share.dto;

import java.util.List;

public class QueryResult<T> {
	private List<T> resultSet;
	private int totalCount;
	
	public QueryResult(){
	}
	
	public QueryResult(List<T> resultSet, int totalCount){
		this.resultSet = resultSet;
		this.totalCount = totalCount;
	}
	
	public List<T> getResultSet() {
		return resultSet;
	}
	public void setResultSet(List<T> resultSet) {
		this.resultSet = resultSet;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
