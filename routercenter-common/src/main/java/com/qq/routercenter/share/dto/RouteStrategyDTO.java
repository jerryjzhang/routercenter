package com.qq.routercenter.share.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class RouteStrategyDTO implements Serializable{
	private static final long serialVersionUID = 6955536267767393560L;
	
	private int id;
	private RouteStrategyType type;
	private String option;
	private String config;
	private State state;
	private String routeName;
	private String routeIncharge;
	private Timestamp createTime;
	private Timestamp lastUpdate;
	
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
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
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
