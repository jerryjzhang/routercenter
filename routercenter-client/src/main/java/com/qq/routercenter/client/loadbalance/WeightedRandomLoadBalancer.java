package com.qq.routercenter.client.loadbalance;

import java.util.List;
import java.util.Random;

import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;

/**
 * The class that implements weight-based Random 
 * load balancing algorithm.
 * 
 * @author jerryjzhang
 *
 */
public class WeightedRandomLoadBalancer extends LoadBalancer {
	private final Random random = new Random();
	
	@Override
	public RouteNodeInfo doSelect(RouteInfo route, List<RouteNodeInfo> nodes) {
		int totalWeight = 0;
		for(RouteNodeInfo node : nodes){
			totalWeight += node.getWeight();
		}
		
		int offset = random.nextInt(totalWeight);
		
		for(RouteNodeInfo node: nodes){
			offset -= node.getWeight();
			if(offset < 0){
				return node;
			}
		}
		
		return null;
	}
}
