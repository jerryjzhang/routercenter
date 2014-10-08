package com.qq.routercenter.client.bus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteInfoRequest;
import com.qq.routercenter.share.dto.RouteInfoUpdate;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteNodeInfoList;
import com.qq.routercenter.share.service.RouterService;

/* 
 * This class should start a daemon thread, this thread will refresh the registered service router
 * information periodically.
 */
public class RemoteRouteInfoBus extends RouteInfoBus {
	private static final Logger LOG = Logger
			.getLogger(RemoteRouteInfoBus.class);
	
	private final RouterService routerService;
	
	private Runnable discoveryThread;
	private Runnable heartbeatThread;
	
	public RemoteRouteInfoBus(RouterService routerService) {
		super();
		this.routerService = routerService;
	}
	
	@Override
	public synchronized void registerService(RouteNodeInfo node){
		super.registerService(node);
		if(heartbeatThread == null){
			heartbeatThread = new HeartbeatThread();
			String intialDelayConfig = System.getProperty(RouterConfigKeys.ROUTER_HEARTBEAT_INTIALDELAY_KEY);
			int intialDelay = intialDelayConfig != null ? Integer.valueOf(intialDelayConfig)
					: RouterConfigKeys.ROUTER_HEARTBEAT_INTIALDELAY_DEFAULT;
			String intervalConfig = System.getProperty(RouterConfigKeys.ROUTER_HEARTBEAT_INTERVAL_KEY);
			int interval = intervalConfig != null ? Integer.valueOf(intervalConfig) 
					: RouterConfigKeys.ROUTER_HEARTBEAT_INTERVAL_DEFAULT;
			Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
					heartbeatThread, intialDelay, interval, TimeUnit.SECONDS);
		}
	}

	/**
	 * Register a service, default behavior will request route information from
	 * route center.
	 */
	@Override
	public synchronized void discoverService(ServiceIdentifier serviceID) {		
		super.discoverService(serviceID);
		if(discoveryThread == null){
			discoveryThread = new DiscoveryThread();
			String intialDelayConfig = System.getProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTIALDELAY_KEY);
			int intialDelay = intialDelayConfig != null ? Integer.valueOf(intialDelayConfig)
					: RouterConfigKeys.ROUTER_DISCOVERY_INTIALDELAY_DEFAULT;
			String intervalConfig = System.getProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTERVAL_KEY);
			int interval = intervalConfig != null ? Integer.valueOf(intervalConfig) 
					: RouterConfigKeys.ROUTER_DISCOVERY_INTERVAL_DEFAULT;
			Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
					discoveryThread, intialDelay, interval, TimeUnit.SECONDS);
		}
		RouteInfo route = RouteInfoCache.getRoute(serviceID);
		if (route != null)
			return;
		// invoke router center service to get service configs
		try {
			route = routerService.pullRoute(new ServiceIdentifier(
					serviceID.getBusiness(), serviceID.getService()));
			RouteInfoCache.loadRoute(serviceID, route);
		} catch (RuntimeException e) {
			LOG.error("Failed to get RouteInfo from RouterCenter for service:"
					+ serviceID + "\nFailure reason:" + e.getMessage());
		}
		// if router center service is unavailable, read local cached file
		if (route == null) {
			route = RouteInfoCache.getRoute(serviceID, true);
		}
		
		if (route == null) {
			throw new RuntimeException("Failed to load RouteInfo for service:" + serviceID);
		}
	}
	
	class DiscoveryThread implements Runnable{
		private Map<ServiceIdentifier, String> routeHashCode = new HashMap<ServiceIdentifier, String>();
		
		public void run(){
			LOG.debug("Pulling route info for registered services");
			RouteInfoRequest [] requests = new RouteInfoRequest[discoveredServices.size()];
			int pos = 0;
			for (ServiceIdentifier serviceID : discoveredServices) {
				requests[pos++] = new RouteInfoRequest(serviceID, routeHashCode.get(serviceID));
			}
			RouteInfoUpdate [] routeUpdates = null;
			try {
				routeUpdates = routerService.pullRouteUpdates(requests);
			} catch (RuntimeException e) {
				LOG.error("Failed to get RouteInfo from RouterCenter for services: "
						+ discoveredServices + "\nFailure reason:" + e.getMessage());
			}
			if(routeUpdates != null){
				for(RouteInfoUpdate routeUpdate : routeUpdates){
					if(routeUpdate != null && routeUpdate.isHasUpdate()){
						ServiceIdentifier serviceID = routeUpdate.getResult().getServiceID();
						RouteInfoCache.loadRoute(serviceID, routeUpdate.getResult());
						if(LOG.isDebugEnabled()){
							LOG.debug("Reloaded service configs for service=" + serviceID);
						}
						routeHashCode.put(serviceID, routeUpdate.getHasCode());
					}
				}
			}
		}
	}

	private class HeartbeatThread implements Runnable{
		private RouteNodeInfoList nodes = new RouteNodeInfoList();
		
		public void run(){
			nodes.clear();
			nodes.addAll(registeredNodes);
			routerService.heartbeat(nodes);
			if(LOG.isDebugEnabled()){
				LOG.debug("Sent heartbeat for route nodes: " + nodes.getNodeInfos());
			}
		}
	}
}
