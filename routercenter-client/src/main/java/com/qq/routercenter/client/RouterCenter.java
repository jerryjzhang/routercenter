package com.qq.routercenter.client;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.bus.LocalRouteInfoBus;
import com.qq.routercenter.client.bus.RemoteRouteInfoBus;
import com.qq.routercenter.client.bus.RouteInfoBus;
import com.qq.routercenter.client.cluster.ClusterInvoker;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.InvocationException;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.client.route.Router;
import com.qq.routercenter.share.service.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

public class RouterCenter {
	private static final Logger LOG = Logger.getLogger(RouterCenter.class);
			
	private final static ConcurrentMap<String, RemoteInvoker> INVOKERS = 
			new ConcurrentHashMap<String, RemoteInvoker>();
	private final static ConcurrentMap<String, RouteWatcher> HANDLERS = 
			new ConcurrentHashMap<String, RouteWatcher>();

	private RouteInfoBus routerInfoBus;
	
	public RouterCenter(InputStream is){
		routerInfoBus = new LocalRouteInfoBus(is);
	}
	
	public RouterCenter(String connString){
		routerInfoBus = new RemoteRouteInfoBus(connString);
	}
	
	public Object invokeService(String sid){
		return invokeService(sid, null);
	}
	
	public Object invokeService(String sid, InvocationContext ctx) {
		RemoteInvoker invoker = INVOKERS.get(sid);
		if(invoker == null){
			throw new IllegalStateException(
					"Service invoker not registered, sid=" + sid);
		}
		// 1. Retrieve route nodes from info bus
		RouteInfo route = routerInfoBus.get(sid);
		if (route == null || route.getNodes().isEmpty()) {
			throw new InvocationException(
					"No route nodes can be found for sid=" + sid, 999);
		}
		List<RouteNodeInfo> nodes = route.getNodes();
		// 2. Ask arbiter to examine nodes, removing invalid nodes
		Arbiter arbiter = BeanFactory.getArbiter(
				Arbiter.getStrategy(route.getStrategies().get(RouteStrategyType.ARBITER.toString())));
		nodes = arbiter.guard(route, nodes);
		// 3. Ask routers to match target nodes based on route rules
		for(RouteRuleInfo rule : route.getRules()){
			Router router = BeanFactory.getRouter(RouteRuleType.valueOf(rule.getType()));
			if(router != null){
				nodes = router.route(rule, nodes, ctx);
			}
		}
		// 4. Ask cluster invoker to execute invocation. 
		//    Let the cluster invokers encapsulate the cluster of nodes
		ClusterInvoker ftInvoker = BeanFactory.getClusterInvoker(
				ClusterInvoker.getStrategy(route.getStrategies().get(RouteStrategyType.FAULT_TOLERANCE)));
		ReturnResult result = ftInvoker.invoke(route, nodes, invoker, ctx);

		return result.getReturnValue();
	}

	public void discoverService(String sid, RemoteInvoker invoker) {
		discoverService(sid, invoker, null);
	}
	public synchronized void discoverService(String sid, RemoteInvoker invoker, 
			RouteWatcher handler) {
		INVOKERS.put(sid, invoker);
		if (handler != null) {
			HANDLERS.put(sid, handler);
		}
		routerInfoBus.discoverService(sid);
		LOG.info("Discovered new service: " + sid);
	}
	public void undiscoverService(String sid){
		routerInfoBus.undiscoveryService(sid);
	}
	
    public void registerService(String sid, String host, int port){
    	registerService(sid, host, port, host+":"+port);
    }
    public void registerService(String sid, String host, int port, 
    		String serviceURL){
    	registerService(sid, host, port, host+":"+port, 100);
    }
    public void registerService(String sid, String host, int port, 
    		String serviceURL, int weight){
    	RouteNodeInfo node = new RouteNodeInfo(host, port);
    	node.setSid(sid);
    	node.setServiceURL(serviceURL);
    	node.setWeight(weight);
    	routerInfoBus.registerService(node);
    	LOG.info("Registered new service: " + sid);
    }
    public void unregisterService(String sid){
    	routerInfoBus.undiscoveryService(sid);
    	LOG.info("Unregistered new service: " + sid);
    }

	public static RouteWatcher getEventHandler(String sid) {
		return HANDLERS.get(sid);
	}
}
