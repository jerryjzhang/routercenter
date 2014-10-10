package com.qq.routercenter.client.loadbalance;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

public class RandomLoadBalancerTest {
	private RouteInfo route;
	
	@Before
	public void setUp(){
		route = TestUtils.createMockRouteNodesWithSameWeight(1000);
	}
	
	@Test
	public void testSelect(){
		int runs = 100000;
		Map<String, Integer> runCounter = TestUtils.runSelect(route, new RandomLoadBalancer(), runs);
		int avgRuns = runs/route.getNodes().size();
		for(RouteNodeInfo node : route.getNodes()){
			int counter = runCounter.get(node.getServiceURL());
			Assert.assertTrue(Math.abs(counter - avgRuns) < avgRuns);
		}
	}
}
