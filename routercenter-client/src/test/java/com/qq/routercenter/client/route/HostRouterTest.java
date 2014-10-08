package com.qq.routercenter.client.route;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;

import static com.qq.routercenter.client.route.Router.*;

public class HostRouterTest {
	private Router router = new HostRouter();
	private final String localIPs;
	
	public HostRouterTest(){
		localIPs = (String)HostRouter.getLocalHostAddresses().toArray()[0];
	}

	@Test
	public void testIsMatchPattern() {
		Assert.assertTrue(isMatchPattern("192.168.1.12", "192.168.1.12"));
		Assert.assertTrue(isMatchPattern("192.168.1.*", "192.168.1.12"));
		Assert.assertTrue(isMatchPattern("192.168.*.12", "192.168.19.12"));
		Assert.assertTrue(isMatchPattern("*.168.19.12", "192.168.19.12"));

		Assert.assertFalse(isMatchPattern("192.168.19.12", "192.168.19.11"));
		Assert.assertFalse(isMatchPattern("192.168.19.*", "192.168.18.11"));
		Assert.assertFalse(isMatchPattern("192.168.*.11", "192.168.18.12"));
		Assert.assertFalse(isMatchPattern("*.168.18.11", "192.169.18.12"));
	}

	@Test
	public void testHasMatchingPattern() {
		String[] patterns = { "192.168.1.*", "192.168.*.11" };
		Assert.assertTrue(hasMatchingPattern(patterns, "192.168.1.12"));
		Assert.assertTrue(hasMatchingPattern(patterns, "192.168.19.11"));

		Assert.assertFalse(hasMatchingPattern(patterns, "192.168.19.12"));
		Assert.assertFalse(hasMatchingPattern(patterns, "192.169.1.11"));
	}
	
	private List<RouteNodeInfo> testRoute(RouteRuleOp ruleOp, String ruleParamValue, String destination, 
			String[] availHosts){
		RouteInfo route = new RouteInfo();
		RouteRuleInfo rule = new RouteRuleInfo();
		rule.setType(RouteRuleType.HOST);
		rule.setSrcOp(ruleOp);
		rule.setSrcValue(ruleParamValue);
		rule.setDestination(destination);
		route.getRules().add(rule);

		for(String host : availHosts){
			RouteNodeInfo node = new RouteNodeInfo();
			node.setHost(host);
			route.getNodes().add(node);
		}
		
		return router.route(rule, route.getNodes(), null);
	}

	/**
	 * RouteRule:
	 *   HOST == "localhost" => 192.168.2.*,192.168.3.*
	 * Scenario:
	 *   SourceHost: "localhost"
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" node is returned
	 */
	@Test
	public void testHostRoute_1() {
		List<RouteNodeInfo> nodes = testRoute(RouteRuleOp.EQUAL, localIPs, "192.168.2.*,192.168.3.*", 
				new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}

	/**
	 * RouteRule:
	 *   HOST == "*" => 192.168.2.*,192.168.3.*
	 * Scenario:
	 *   SourceHost: "localhost"
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" node is returned
	 */
	@Test
	public void testHostRoute_2() {
		List<RouteNodeInfo> nodes = testRoute(RouteRuleOp.EQUAL, "*", "192.168.2.*,192.168.3.*", 
				new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}

	/**
	 * RouteRule:
	 *   HOST != "localhost" => 192.168.1.*
	 * Scenario:
	 *   SourceHost: "localhost"
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.1.1", "192.168.2.1" node are returned
	 */
	@Test
	public void testHostRoute_3() {
		List<RouteNodeInfo> nodes = testRoute(RouteRuleOp.INEQUAL, localIPs, "192.168.1.*", 
				new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 2);
	}

	/**
	 * RouteRule:
	 *   HOST != "*" => *
	 * Scenario:
	 *   SourceHost: "localhost"
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.1.1", "192.168.2.1" node are returned
	 */
	@Test
	public void testHostRoute_4() {
		List<RouteNodeInfo> nodes = testRoute(RouteRuleOp.INEQUAL, "*", "*", 
				new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 2);
	}
}
