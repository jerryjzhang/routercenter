package com.qq.routercenter.share.dto;

import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNodeQueryCriteria {
	private int id;
	private RouteNodeType type;
	private State state;
	private String host; // 模糊匹配
	private int port;
	private String serviceURL; // 模糊匹配
	private String set; // 模糊匹配
	private String routeName; // 模糊匹配
	private String orderField; 
	private String orderType; 
	private String searchKey;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RouteNodeType getType() {
		return type;
	}

	public void setType(RouteNodeType type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
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
}
