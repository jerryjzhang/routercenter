package com.qq.routercenter.client.bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.share.enums.FaultToleranceStrategy;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.RouterServices;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteInfoRequest;
import com.qq.routercenter.share.service.RouteInfoUpdate;
import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouteStrategyInfo;
import com.qq.routercenter.share.service.RouterServiceThrift;

/* 
 * This class should start a daemon thread, this thread will refresh the registered service router
 * information periodically.
 */
public class RemoteRouteInfoBus extends RouteInfoBus {
	private static final Logger LOG = Logger
			.getLogger(RemoteRouteInfoBus.class);
	
	private final List<RouterServiceThrift.Client> routerServiceClients = 
			new ArrayList<RouterServiceThrift.Client>();
	
	private Runnable discoveryThread;
	private Runnable heartbeatThread;
	
	public RemoteRouteInfoBus(String connString) {
		super();
		ConnectStringParser parser = new ConnectStringParser(connString);
		RouteInfo routerServiceInfo = parser.getRouteInfo();
		for(RouteNodeInfo node : routerServiceInfo.getNodes()){
			try{
				TTransport transport = new TSocket(node.getHost(), node.getPort());
				transport.open();
				TProtocol protocol = new TBinaryProtocol(transport);
				routerServiceClients.add(new RouterServiceThrift.Client(protocol));
			}catch(TTransportException e){
				LOG.error("Failed to connect to RouterCenter service at " + node.getServiceURL());
			}
		}
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
	public synchronized void discoverService(String sid) {		
		super.discoverService(sid);
//		if(discoveryThread == null){
//			discoveryThread = new DiscoveryThread();
//			String intialDelayConfig = System.getProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTIALDELAY_KEY);
//			int intialDelay = intialDelayConfig != null ? Integer.valueOf(intialDelayConfig)
//					: RouterConfigKeys.ROUTER_DISCOVERY_INTIALDELAY_DEFAULT;
//			String intervalConfig = System.getProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTERVAL_KEY);
//			int interval = intervalConfig != null ? Integer.valueOf(intervalConfig) 
//					: RouterConfigKeys.ROUTER_DISCOVERY_INTERVAL_DEFAULT;
//			Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
//					discoveryThread, intialDelay, interval, TimeUnit.SECONDS);
//		}
		RouteInfo route = RouteInfoCache.getRoute(sid);
		if (route != null)
			return;
		// invoke router center service to get service configs
		try {
			RouteInfoRequest request = new RouteInfoRequest(sid);
			for(RouterServiceThrift.Client client : routerServiceClients){
				try{
					RouteInfoUpdate update = client.pullRouteUpdate(request);
					if(update != null){
						route = update.getResult();
						break;
					}
					
				}catch(TException e){
					e.printStackTrace();
					LOG.error(e.getMessage());
				}
			}
			if(route != null){
				RouteInfoCache.loadRoute(sid, route);
			}
		} catch (RuntimeException e) {
			LOG.error("Failed to get RouteInfo from RouterCenter for service:"
					+ sid + "\nFailure reason:" + e.getMessage());
		}
		// if router center service is unavailable, read local cached file
		if (route == null) {
			route = RouteInfoCache.getRoute(sid, true);
		}
		
		if (route == null) {
			throw new RuntimeException("Failed to load RouteInfo for service:" + sid);
		}
	}
	
	static class ConnectStringParser {
		private RouteInfo routeInfo = new RouteInfo();
		
		public ConnectStringParser(String connString){
			routeInfo.setSid(RouterServices.ROUTER_SERVICE.getSid().toFullSID());
			String [] addrs = connString.split(",");
			for(String addr : addrs){
				String [] items = addr.split(":");
				if(items.length != 2){
					throw new RuntimeException("Invalid connection string: " + addr);
				} 
				RouteNodeInfo node = new RouteNodeInfo(items[0],Integer.valueOf(items[1]));
				node.setSid(RouterServices.ROUTER_SERVICE.getSid().toFullSID());
				routeInfo.addToNodes(node);
			}
			RouteStrategyInfo strategy = new RouteStrategyInfo();
			strategy.setType(RouteStrategyType.LOAD_BALANCE.toString());
			strategy.setOption(LoadBalanceStrategy.ROUND_ROBIN.toString());
			routeInfo.putToStrategies(strategy.getType(), strategy);
			strategy = new RouteStrategyInfo();
			strategy.setType(RouteStrategyType.FAULT_TOLERANCE.toString());
			strategy.setOption(FaultToleranceStrategy.FAILOVER.toString());
			routeInfo.putToStrategies(strategy.getType(), strategy);
		}

		public RouteInfo getRouteInfo() {
			return routeInfo;
		}
	}
	
	class DiscoveryThread implements Runnable{
		private Map<String, String> routeHashCode = new HashMap<String, String>();
		
		public void run(){
			LOG.debug("Pulling route info for registered services");
			List<RouteInfoRequest> requests = new ArrayList<RouteInfoRequest>();
			for (String sid : discoveredServices) {
				RouteInfoRequest request = new RouteInfoRequest(sid);
				request.setLastHashCode(routeHashCode.get(sid));
				requests.add(request);
			}
			List<RouteInfoUpdate> routeUpdates = null;
			try {
				for(RouterServiceThrift.Client client : routerServiceClients){
					try{
						routeUpdates = client.pullRouteUpdates(requests);
						if(routeUpdates != null){
							break;
						}
					}catch(TException e){
						LOG.error(e.getMessage());
					}
				}
			} catch (RuntimeException e) {
				LOG.error("Failed to get RouteInfo from RouterCenter for services: "
						+ discoveredServices + "\nFailure reason:" + e.getMessage());
			}
			if(routeUpdates != null){
				for(RouteInfoUpdate routeUpdate : routeUpdates){
					if(routeUpdate != null && routeUpdate.hasUpate){
						String sid = routeUpdate.getResult().getSid();
						RouteInfoCache.loadRoute(sid, routeUpdate.getResult());
						if(LOG.isDebugEnabled()){
							LOG.debug("Reloaded service configs for service=" + sid);
						}
						routeHashCode.put(sid, routeUpdate.getHasCode());
					}
				}
			}
		}
	}

	private class HeartbeatThread implements Runnable{
		public void run(){
			for(RouterServiceThrift.Client client : routerServiceClients){
				try{
					client.heartbeat(registeredNodes);
				}catch(TException e){
					LOG.error(e.getMessage());
				}
			}
			if(LOG.isDebugEnabled()){
				LOG.debug("Sent heartbeat for route nodes: " + registeredNodes);
			}
		}
	}
}
