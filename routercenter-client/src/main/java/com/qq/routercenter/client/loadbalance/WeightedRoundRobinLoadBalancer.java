package com.qq.routercenter.client.loadbalance;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;

/**
 * The class that implements weight-based RoundRobin 
 * load balancing algorithm.
 * 
 * @author jerryjzhang
 *
 */
public class WeightedRoundRobinLoadBalancer extends LoadBalancer {
	private final ConcurrentMap<ServiceIdentifier, AtomicInteger> currentPositions = 
			new ConcurrentHashMap<ServiceIdentifier, AtomicInteger>();
    private final ConcurrentMap<ServiceIdentifier, AtomicInteger> currentWeights =
    		new ConcurrentHashMap<ServiceIdentifier, AtomicInteger>();
	
    @Override
	public RouteNodeInfo doSelect(RouteInfo route, List<RouteNodeInfo> nodes) {
		ServiceIdentifier serviceID = route.getServiceID();
		AtomicInteger pos = currentPositions.get(serviceID);
        if (pos == null) {
            currentPositions.putIfAbsent(serviceID, new AtomicInteger(-1));
            pos = currentPositions.get(serviceID);
        }
        AtomicInteger weight = currentWeights.get(serviceID);
        if (weight == null) {
        	currentWeights.putIfAbsent(serviceID, new AtomicInteger(0));
        	weight = currentWeights.get(serviceID);
        }
        
        int currentIndex = pos.get();
        int currentWeight = weight.get();
		int gcdWeight = getGCD(nodes);
		int maxWeight = getMax(nodes);
		while(true){
			currentIndex = (currentIndex + 1) % nodes.size();
			if(currentIndex == 0){
				currentWeight = currentWeight - gcdWeight;
				if(currentWeight <= 0){
					currentWeight = maxWeight;
					if(currentWeight == 0){
						return null;
					}
				}
			}
			if(nodes.get(currentIndex).getWeight() >= currentWeight){
				currentPositions.get(serviceID).set(currentIndex);
				currentWeights.get(serviceID).set(currentWeight);
				return nodes.get(currentIndex);
			}
		}
    }
    
	private static int getMax(List<RouteNodeInfo> nodes){
		int maxWeight = 0;
		for(RouteNodeInfo node : nodes){
			maxWeight = Math.max(maxWeight, node.getWeight());
		}
		return maxWeight;
	}
	
	private static int getGCD(List<RouteNodeInfo> nodes){
		int w = 0;
		for(int i = 0, len = nodes.size(); i < len - 1; i++){
			if(w == 0){
				w = gcd(nodes.get(i).getWeight(), nodes.get(i+1).getWeight());
			}else{
				w = gcd(w, nodes.get(i+1).getWeight());
			}
		}
		
		return w;
	}
	
	private static int gcd(int a, int b) {
		BigInteger b1 = new BigInteger(String.valueOf(a));
		BigInteger b2 = new BigInteger(String.valueOf(b));
		BigInteger gcd = b1.gcd(b2);
		return gcd.intValue();
	}
}
