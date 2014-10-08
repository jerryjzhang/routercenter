package com.qq.routercenter.service.registry;

import com.qq.routercenter.share.domain.RouteNode;

public class NodeLease {
	private RouteNode node;
	private long lastSeenTimestamp;
	private long duration;

	public NodeLease(RouteNode node, long duration){
		this.node = node;
		this.duration = duration;
		this.lastSeenTimestamp = System.currentTimeMillis();
	}
	
	public void renew(){
		lastSeenTimestamp = System.currentTimeMillis() + duration;
	}
	
	public boolean isExpired(){
		return System.currentTimeMillis() > lastSeenTimestamp + duration;
	}

	public RouteNode getNode() {
		return node;
	}
}
