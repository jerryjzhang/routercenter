package com.qq.routercenter.client.route;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.domain.RouteParam;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;

public class ArgumentCombinationTest {
	private Router router = new ArgumentCombinationRouter();

	private Method mockMethod;
	{
		try{
			mockMethod = ArgumentCombinationTest.class.
					getMethod("mockInvokedMethod", String.class, Integer.class);
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * 
	 * This is purely a mocked method with a RouteParam argument
	 */
	public void mockInvokedMethod(@RouteParam("QQ")String qq, @RouteParam("ID")Integer id){
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
		rule.setType(RouteRuleType.METHOD_ARGS_COMB);
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
	 *   (QQ = '123456' && ID = 100) => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "123456", ID == 100
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   Route rule matches, "192.168.2.1" node is returned
	 */
	@Test
	public void testArgsRoute_1()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethod, 
				"QQ,ID", null, "$1 == '123456' && $2 == 100", "192.168.2.*", 
				new Object[]{"123456", 100}, new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   (QQ = '123456' && ID > 100) => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "123456", ID == 150
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   Route rule matches, "192.168.2.1" node is returned
	 */
	@Test
	public void testArgsRoute_2()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethod, 
				"QQ, ID", null, "$1 == '123456' && $2 > 100", "192.168.2.*", 
				new Object[]{"123456", 150}, new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   (QQ != '123456' && ID >= 50 && ID <= 100) => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "1234567", ID == 80
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   Route rule matches, "192.168.2.1" node is returned
	 */
	@Test
	public void testArgsRoute_3()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethod, 
				"QQ, ID", null, "$1 != '123456' && $2 >= 50 && $2 <= 100", "192.168.2.*", 
				new Object[]{"1234567", 80}, new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 1);
		Assert.assertTrue("192.168.2.1".equals(nodes.get(0).getHost()));
	}
	
	/**
	 * RouteRule:
	 *   (QQ != '123456' && ID < 100) => 192.168.2.*
	 * Scenario:
	 *   Method Args: QQ == "123456", ID == 80
	 *   RouteNodes: 192.168.2.1, 192.168.1.1
	 * Expected Result:
	 *   Route rule mismatches, both "192.168.2.1" and "192.168.1.1" nodes are returned
	 */
	@Test
	public void testArgsRoute_4()throws NoSuchMethodException{
		List<RouteNodeInfo> nodes = testRoute(mockMethod, 
				"QQ, ID", null, "$1 != '123456' && $2 < 100", "192.168.2.*", 
				new Object[]{"123456", 80}, new String[]{"192.168.2.1","192.168.1.1"});
		
		Assert.assertTrue(nodes.size() == 2);
	}
}
