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
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.service.RouterService;

public class RouterCenter {
	private static final Logger LOG = Logger.getLogger(RouterCenter.class);
			
	private final static ConcurrentMap<ServiceIdentifier, RemoteInvoker> INVOKERS = 
			new ConcurrentHashMap<ServiceIdentifier, RemoteInvoker>();
	private final static ConcurrentMap<ServiceIdentifier, RouteWatcher> HANDLERS = 
			new ConcurrentHashMap<ServiceIdentifier, RouteWatcher>();

	private RouteInfoBus routerInfoBus;
	private RouterCenterService routerCenterService;
	
	public RouterCenter(InputStream is){
		routerInfoBus = new LocalRouteInfoBus(is);
	}
	
	public RouterCenter(String connString){
		routerCenterService = new RouterCenterService(connString);
		routerInfoBus = new RemoteRouteInfoBus(routerCenterService.getService());
	}
	
	public RouterService getService(){
		return routerCenterService.getService();
	}
	
	public Object invokeService(String sid){
		return invokeService(sid, null);
	}
	
	public Object invokeService(String sid, InvocationContext ctx) {
		ServiceIdentifier serviceID = ServiceIdentifier.valueOf(sid);
		RemoteInvoker invoker = INVOKERS.get(serviceID);
		if(invoker == null){
			throw new IllegalStateException(
					"Service invoker not registered, serviceID=" + serviceID);
		}
		// 1. Retrieve route nodes from info bus
		RouteInfo route = routerInfoBus.get(serviceID);
		if (route == null || route.getNodes().isEmpty()) {
			throw new InvocationException(
					"No route nodes can be found for serviceID=" + serviceID, 999);
		}
		List<RouteNodeInfo> nodes = route.getNodes();
		// 2. Ask arbiter to examine nodes, removing invalid nodes
		Arbiter arbiter = BeanFactory.getArbiter(
				Arbiter.getStrategy(route.getStrategies().get(RouteStrategyType.ARBITER)));
		nodes = arbiter.guard(route, nodes);
		// 3. Ask routers to match target nodes based on route rules
		for(RouteRuleInfo rule : route.getRules()){
			Router router = BeanFactory.getRouter(rule.getType());
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
		ServiceIdentifier serviceID = ServiceIdentifier.valueOf(sid);
		INVOKERS.put(serviceID, invoker);
		if (handler != null) {
			HANDLERS.put(serviceID, handler);
		}
		routerInfoBus.discoverService(serviceID);
		LOG.info("Discovered new service: " + serviceID);
	}
	public void undiscoverService(String sid){
		routerInfoBus.undiscoveryService(ServiceIdentifier.valueOf(sid));
	}
	
    public void registerService(String serviceID, String host, int port){
    	registerService(serviceID, host, port, host+":"+port);
    }
    public void registerService(String serviceID, String host, int port, 
    		String serviceURL){
    	registerService(serviceID, host, port, host+":"+port, 100);
    }
    public void registerService(String sid, String host, int port, 
    		String serviceURL, int weight){
    	ServiceIdentifier serviceID = ServiceIdentifier.valueOf(sid);
    	routerInfoBus.registerService(new RouteNodeInfo(serviceID, host, port, serviceURL, weight));
    	LOG.info("Registered new service: " + serviceID);
    }
    public void unregisterService(String sid){
    	ServiceIdentifier serviceID = ServiceIdentifier.valueOf(sid);
    	routerInfoBus.undiscoveryService(serviceID);
    	LOG.info("Unregistered new service: " + serviceID);
    }

	public static RouteWatcher getEventHandler(ServiceIdentifier serviceID) {
		return HANDLERS.get(serviceID);
	}
}
