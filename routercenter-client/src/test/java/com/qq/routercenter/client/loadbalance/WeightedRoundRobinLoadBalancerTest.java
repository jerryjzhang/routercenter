package com.qq.routercenter.client.loadbalance;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.qq.routercenter.share.dto.RouteInfo;

public class WeightedRoundRobinLoadBalancerTest {
	private RouteInfo route;
	
	@Before
	public void setUp(){
		route = TestUtils.createMockRouteNodesWithSameWeight(2);
		route.getNodes().get(0).setWeight(10);
		route.getNodes().get(1).setWeight(20);
	}
	
	@Test
	public void testSelect(){
		int runs = 100000;
		Map<String, Integer> runCounter = TestUtils.runSelect(route, new WeightedRoundRobinLoadBalancer(), runs);
		int counter1 = runCounter.get(route.getNodes().get(0).getServiceURL());
		int counter2 = runCounter.get(route.getNodes().get(1).getServiceURL());
		Assert.assertTrue((Math.abs(counter2 - counter1 * 2) < runs * 0.001f));
	}
}
