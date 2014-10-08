package com.qq.routercenter.service.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.qq.routercenter.share.domain.RouteNode;

public abstract class RouteRegistry {
	public abstract void addRouteNode(RouteNode node, NodeLease lease);
	public abstract void removeRouteNode(RouteNode node);
	public abstract NodeLease getLease(RouteNode node);
	public abstract Collection<NodeLease> getAllLeases();

	public Set<RouteNode> expireRouteNodes(){
		Set<RouteNode> expiredNodes = new HashSet<RouteNode>();
		for(NodeLease lease : getAllLeases()){
			if(lease.isExpired()){
				RouteNode expiredNode = lease.getNode();
				removeRouteNode(expiredNode);
				expiredNodes.add(expiredNode);
			}
		}
		
		return expiredNodes;
	}
}
