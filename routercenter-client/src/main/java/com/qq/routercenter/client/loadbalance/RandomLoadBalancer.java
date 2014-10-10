package com.qq.routercenter.client.loadbalance;

import java.util.List;
import java.util.Random;

import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

/**
 * The class that implements Random load balancing algorithm.
 * 
 * @author jerryjzhang
 *
 */
public class RandomLoadBalancer extends LoadBalancer {
	private final Random random = new Random();

	@Override
	public RouteNodeInfo doSelect(RouteInfo route, List<RouteNodeInfo> nodes) {
    	return nodes.get(random.nextInt(nodes.size()));
    }
}
