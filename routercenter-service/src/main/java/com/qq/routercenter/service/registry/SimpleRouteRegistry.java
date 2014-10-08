package com.qq.routercenter.service.registry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.qq.routercenter.share.domain.RouteNode;

public class SimpleRouteRegistry extends RouteRegistry {
	private ConcurrentMap<RouteNode, NodeLease> registry = new ConcurrentHashMap<RouteNode, NodeLease>();
	
	public void addRouteNode(RouteNode node, NodeLease lease){
		registry.putIfAbsent(node, lease);
	}
	
	public void removeRouteNode(RouteNode node){
		registry.remove(node);
	}
	
	public NodeLease getLease(RouteNode node){
		return registry.get(node);
	}
	
	public Collection<NodeLease> getAllLeases(){
		return registry.values();
	}
}
