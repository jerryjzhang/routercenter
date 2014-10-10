package com.qq.routercenter.client.loadbalance;

import java.util.HashMap;
import java.util.Map;

import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

public class TestUtils {
	static RouteInfo createMockRouteNodesWithSameWeight(int count){
		ServiceIdentifier sid = new ServiceIdentifier("business", "service");
		RouteInfo route = new RouteInfo();
		route.setServiceID(sid);
		for(int i=1; i<=count; i++){
			RouteNodeInfo node = new RouteNodeInfo(sid, "192.168.1." + i, 8080);
			route.getNodes().add(node);
		}
		return route;
	}
	
	static Map<String, Integer> runSelect(RouteInfo route, LoadBalancer lb, int runs){
		Map<String,Integer> runCounter = new HashMap<String,Integer>();
		for(int i=1;i<=runs;i++){
			RouteNodeInfo n = lb.select(route, route.getNodes());
			Integer c = runCounter.get(n.getServiceURL());
			if(c == null){
				runCounter.put(n.getServiceURL(), 1);
			}else{
				runCounter.put(n.getServiceURL(), c+1);
			}
		}
		
		return runCounter;
	}
}
