package com.qq.routercenter.share.dto;

import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.State;

public class RouteRuleQueryCriteria {
	private int id;
	private State state;
	private RouteRuleType type;
	private String srcProp; // 模糊匹配
	private RouteRuleOp srcOp;
	private String srcValue; // 模糊匹配
	private String destination; // 模糊匹配
	private String routeName; // 模糊匹配
	private String searchKey; // 模糊匹配
	private String orderField; 
	private String orderType; 

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public RouteRuleType getType() {
		return type;
	}

	public void setType(RouteRuleType type) {
		this.type = type;
	}

	public String getSrcProp() {
		return srcProp;
	}

	public void setSrcProp(String srcProp) {
		this.srcProp = srcProp;
	}

	public RouteRuleOp getSrcOp() {
		return srcOp;
	}

	public void setSrcOp(RouteRuleOp srcOp) {
		this.srcOp = srcOp;
	}

	public String getSrcValue() {
		return srcValue;
	}

	public void setSrcValue(String srcValue) {
		this.srcValue = srcValue;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
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

}
