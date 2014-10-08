package com.qq.routercenter.client.pojo;

import com.qq.routercenter.share.dto.RouteNodeInfo;

public class RouteEvent {
	private EventType type;
	private RouteNodeInfo node;
	private long when;

	public RouteEvent(EventType type, RouteNodeInfo node) {
		this(type, node, System.currentTimeMillis());
	}
	
	public RouteEvent(EventType type, RouteNodeInfo node, long when) {
		this.node = node;
		this.when = when;
	}
	
	public EventType getType() {
		return type;
	}

	public RouteNodeInfo getNode() {
		return node;
	}

	public long getWhen() {
		return when;
	}
	
	public enum EventType{
		NodeBlacklisted, NodeWhitelisted;
	}
}
