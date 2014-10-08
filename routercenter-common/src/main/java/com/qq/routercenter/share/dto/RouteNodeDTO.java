package com.qq.routercenter.share.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNodeDTO implements Serializable {
	private static final long serialVersionUID = 4312125449838725708L;
	
	private int id;
	private RouteNodeType type;
	private String host;
	private int port;
	private String serviceURL;
	private State state;
	private int weight = 100;
	private String routeName;
	private String routeIncharge;
	private String set = "default";
	private Timestamp createTime;
	private Timestamp lastUpdate;

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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getRouteIncharge() {
		return routeIncharge;
	}

	public void setRouteIncharge(String routeIncharge) {
		this.routeIncharge = routeIncharge;
	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
