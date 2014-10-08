package com.qq.routercenter.share.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.State;

public class RouteRuleDTO implements Serializable{
	private static final long serialVersionUID = -918821995552588939L;
	
	private int id;
	private State state;
	private RouteRuleType type;
	private String srcProp;
	private RouteRuleOp srcOp;
	private String srcValue;
	private String destination;
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
