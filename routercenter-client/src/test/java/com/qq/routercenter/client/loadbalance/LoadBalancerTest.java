package com.qq.routercenter.client.loadbalance;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.qq.routercenter.client.BeanFactory;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;

public class LoadBalancerTest {
	private RouteInfo route;
	
	@Before
	public void setUp(){
		route = TestUtils.createMockRouteNodesWithSameWeight(1000);
	}

	@Test
	public void testReselectRandom(){
		testReselect(BeanFactory.getLoadBalancer(
				LoadBalanceStrategy.RANDOM), route);
	}
	
	@Test
	public void testReselectRoundRobin(){
		testReselect(BeanFactory.getLoadBalancer(
				LoadBalanceStrategy.ROUND_ROBIN), route);
	}
	
	@Test
	public void testReselectWeigthedRandom(){
		testReselect(BeanFactory.getLoadBalancer(
				LoadBalanceStrategy.WEIGHT_RANDOM), route);
	}
	
	@Test
	public void testReselectWeigthedRR(){
		testReselect(BeanFactory.getLoadBalancer(
				LoadBalanceStrategy.WEIGHT_ROUNDROBIN), route);
	}
	
	private void testReselect(LoadBalancer lb, RouteInfo route){
		List<RouteNodeInfo> invokedNodes =  new ArrayList<RouteNodeInfo>();
		RouteNodeInfo node = null;
		int retries = 0;
		while((node = lb.reselect(route, route.getNodes(), invokedNodes)) != null){
			retries++;
			Assert.assertTrue(!invokedNodes.contains(node));
			invokedNodes.add(node);
		}
		// maximum retry times should be less than node size
		Assert.assertTrue(retries <= route.getNodes().size());
	}
}
