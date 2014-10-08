package com.qq.routercenter.client.route;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.domain.RouteParam;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;

public class ArgumentRouterTest {
	private Router router = new ArgumentRouter();

	private Method mockMethodString;
	private Method mockMethodInt;
	{
		try{
			mockMethodString = ArgumentRouterTest.class.
					getMethod("mockInvokedMethodString", String.class);
			mockMethodInt    = ArgumentRouterTest.class.
					getMethod("mockInvokedMethodInt", int.class);
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * 
	 * This is purely a mocked method with a RouteParam argument
	 */
	public void mockInvokedMethodString(@RouteParam("QQ")String qq){
	}
	/**
	 * 
	 * This is purely a mocked method with a RouteParam argument
	 */
	public void mockInvokedMethodInt(@RouteParam("QQ")int qq){
	}
	
	/**
	 * This is a test executor method that take test paramters and execute test 
	 * @param mockMethod
	 * @param ruleParamName
	 * @param ruleOp
	 * @param ruleParamValue
	 * @param destination
	 * @param srcParams
	 * @param availHosts
	 * @return
	 */
	private List<RouteNodeInfo> testRoute(Method mockMethod, 
			String ruleParamName, RouteRuleOp ruleOp, String ruleParamValue, String destination, 
			Object[] srcParams  , String[] availHosts){
		RouteInfo route = new RouteInfo();
		RouteRuleInfo rule = new RouteRuleInfo();
		rule.setType(RouteRuleType.METHOD_ARGS);
		rule.setSrcProp(ruleParamName);
		rule.setSrcOp(ruleOp);
		rule.setSrcValue(ruleParamValue);
		rule.setDestination(destination);
		route.getRules().add(rule);

		for(String host : availHosts){
			RouteNodeInfo node = new RouteNodeInfo();
			node.setHost(host);
			route.getNodes().add(node);
		}
		
		RpcInvocationContext ctx = RpcInvocationContext.Builder.newBuilder()
				.methodArgs(srcParams)
				.methodObj(mockMethod)
				.build();
		
		return router.route(rule, route.getNodes(), ctx);
	}
	
	/**
	 * RouteRule:
	 *   QQ == "123456" => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "123456"
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" node is returned
	 */
	@Test
	public void testArgsRoute_1()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodString, 
				"QQ", RouteRuleOp.EQUAL, "123456", "192.168.2.*", 
				new Object[]{"123456"}, new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   QQ == "123456" => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "123456"
	 *  RouteNodes: 192.168.1.1
	 * Expected Result:
	 *   No nodes is returned
	 */
	@Test
	public void testArgsRoute_2()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodString, 
				"QQ", RouteRuleOp.EQUAL, "123456", "192.168.2.*", 
				new Object[]{"123456"}, new String[]{"192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 0);
	}
	
	/**
	 * Scenario:
	 *   Method Args: QQ == "1234567"
	 *   RouteRule QQ != "123456" => 192.168.2.*
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" node is returned
	 */
	@Test
	public void testArgsRoute_3()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodString, 
				"QQ", RouteRuleOp.INEQUAL, "123456", "192.168.2.*", 
				new Object[]{"1234567"}, new String[]{"192.168.2.1", "192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * Scenario:
	 *   Method Args: QQ == "123456"
	 *   RouteRule QQ != "123456" => 192.168.2.*
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1", "192.168.1.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_4()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodString, 
				"QQ", RouteRuleOp.INEQUAL, "123456", "192.168.2.*", 
				new Object[]{"123456"}, new String[]{"192.168.2.1", "192.168.1.1"});

		Assert.assertTrue(nodes.size() == 2);
	}
	
	/**
	 * RouteRule:
	 *   QQ == 123456 => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == 123456
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_5()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodInt, 
				"QQ", RouteRuleOp.EQUAL, "123456", "192.168.2.*", 
				new Object[]{123456}, new String[]{"192.168.2.1", "192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   QQ != 123456 => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == 1234567
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_6()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodInt, 
				"QQ", RouteRuleOp.INEQUAL, "123456", "192.168.2.*", 
				new Object[]{1234567}, new String[]{"192.168.2.1", "192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   QQ != 123456 => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == 123456
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.1.1", "192.168.2.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_7()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodInt, 
				"QQ", RouteRuleOp.INEQUAL, "123456", "192.168.2.*", 
				new Object[]{123456}, new String[]{"192.168.2.1", "192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 2);
	}
	
	/**
	 * RouteRule:
	 *   QQ between 100-200 => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == 150
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   "192.168.2.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_8()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethodInt, 
				"QQ", RouteRuleOp.BETWEEN, "100-200", "192.168.2.*", 
				new Object[]{150}, new String[]{"192.168.2.1", "192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
}
