package com.qq.routercenter.share.domain;

import java.sql.Timestamp;

import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.State;

public class RouteRule {
	private int id;
	private State state;
	private RouteRuleType type;
	private String srcProp;
	private RouteRuleOp srcOp;
	private String srcValue;
	private String destination;
	private Route route;
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

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
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

	@Override
	public String toString() {
		return "RouteRule [id=" + id + ", state=" + state + ", srcProp="
				+ srcProp + ", srcOp=" + srcOp + ", srcValue=" + srcValue
				+ ", destination=" + destination + ", createTime=" + createTime
				+ ", lastUpdate=" + lastUpdate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((srcOp == null) ? 0 : srcOp.hashCode());
		result = prime * result + ((srcProp == null) ? 0 : srcProp.hashCode());
		result = prime * result
				+ ((srcValue == null) ? 0 : srcValue.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RouteRule other = (RouteRule) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (id != other.id)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (srcOp != other.srcOp)
			return false;
		if (srcProp == null) {
			if (other.srcProp != null)
				return false;
		} else if (!srcProp.equals(other.srcProp))
			return false;
		if (srcValue == null) {
			if (other.srcValue != null)
				return false;
		} else if (!srcValue.equals(other.srcValue))
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
