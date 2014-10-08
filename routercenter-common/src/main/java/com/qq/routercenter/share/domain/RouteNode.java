package com.qq.routercenter.share.domain;

import java.sql.Timestamp;

import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNode {
	private int id;
	private RouteNodeType type;
	private String host;
	private int port;
	private String serviceURL;
	private State state;
	private int weight = 100; //default to 100
	private Route route;
	private String set;
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

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
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

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp date) {
		this.createTime = date;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result
				+ ((serviceURL == null) ? 0 : serviceURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteNode other = (RouteNode) obj;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (serviceURL == null) {
			if (other.serviceURL != null)
				return false;
		} else if (!serviceURL.equals(other.serviceURL))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RouteNode [id=" + id + ", type=" + type + ", host=" + host
				+ ", port=" + port + ", serviceURL=" + serviceURL + ", state="
				+ state + ", weight=" + weight + ", set=" + set
				+ ", createTime=" + createTime + ", lastUpdate=" + lastUpdate
				+ "]";
	}
}