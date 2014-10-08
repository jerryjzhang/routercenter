package com.qq.routercenter.client.loadbalance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;

/**
 * The class that implements RoundRobin load balancing algorithm.
 * 
 * @author jerryjzhang
 *
 */
public class RoundRobinLoadBalancer extends LoadBalancer {
	private final ConcurrentMap<ServiceIdentifier, AtomicInteger> currentPositions = 
			new ConcurrentHashMap<ServiceIdentifier, AtomicInteger>();
	
	@Override
	public RouteNodeInfo doSelect(RouteInfo route, List<RouteNodeInfo> nodes) {
		AtomicInteger pos = currentPositions.get(route.getServiceID());
        if (pos == null) {
            currentPositions.putIfAbsent(route.getServiceID(), new AtomicInteger(0));
            pos = currentPositions.get(route.getServiceID());
        }
        int current = pos.getAndIncrement();
        resetOnMax(pos);
        return nodes.get(current % nodes.size());
	}
	
	private void resetOnMax(AtomicInteger ai){
        int current = ai.get();
        if(current == Integer.MAX_VALUE){
        	ai.compareAndSet(current, 0);
        }
	}

}
