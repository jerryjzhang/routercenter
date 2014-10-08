package com.qq.routercenter.share.dto;

import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class RouteStrategyQueryCriteria {
	private int id;
	private RouteStrategyType type;
	private String option; // 模糊匹配
	private State state;
	private String orderField; 
	private String orderType; 
	private String searchKey; //
	private String routeName; // 模糊匹配

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RouteStrategyType getType() {
		return type;
	}

	public void setType(RouteStrategyType type) {
		this.type = type;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

}
