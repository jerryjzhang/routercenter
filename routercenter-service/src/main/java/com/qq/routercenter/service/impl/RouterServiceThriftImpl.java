package com.qq.routercenter.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.qq.routercenter.service.config.BeanFactory;
import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.service.registry.NodeLease;
import com.qq.routercenter.service.registry.RouteRegistry;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;
import com.qq.routercenter.share.service.DomainConverter;
import com.qq.routercenter.share.service.RouteInfoRequest;
import com.qq.routercenter.share.service.RouteInfoUpdate;
import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouterServiceThrift;

public class RouterServiceThriftImpl implements RouterServiceThrift.Iface {
	private static final Logger LOG = Logger
			.getLogger(RouterServiceImpl.class);

    private RouteDao routeDao = BeanFactory.getBeanByType(RouteDao.class);
    private RouteNodeDao routeNodeDao = BeanFactory.getBeanByType(RouteNodeDao.class);

	static long ROUTENODE_EXPIRY_INTERVAL = 3 * 60 * 1000;
	static long ROUTENODE_CHECK_INTERVAL  = 1 * 60 * 1000;

	private RouteRegistry nodeRegistry = BeanFactory.getBeanByType(RouteRegistry.class);
	
	ModelMapper mapper = new ModelMapper();

	
	public void heartbeat(List<RouteNodeInfo> nodes) throws org.apache.thrift.TException{
		System.out.println("Received heartbeat " + nodes.size());
		for (RouteNodeInfo node : nodes) {
			if(LOG.isDebugEnabled()){
				LOG.debug("Received node heartbeat: " + node);
			}
			// reported route node must at least have associated
			// routeName and serviceURL
			if (node.getSid() == null || node.getHost() == null
					|| node.getPort() == 0)
				continue;
			
			// routeName is the same as service identifier, 
			// a.k.a so-called SID
			String sid = node.getSid();
			Route route = null;
			// in case multiple nodes of the same route 
			// send heartbeat simultaneously
			synchronized(this){
			    route = routeDao.queryRouteInfoByName(sid);
				if (route == null) {
					route = new Route();
					route.setName(sid);
					route.setState(State.ACTIVE);
					int routeId = routeDao.insert(route);
					route.setId(routeId);
					LOG.info("Inserted new route=" + route);
					System.out.println("Inserted new route=" + route);
				}
			}
			// check if a RouteNode entry exists for current node
			// if not, insert a new one
			RouteNode existingNode = null;
			for (RouteNode n : route.getNodes()) {
				if (n.getServiceURL().equals(node.getServiceURL())) {
					existingNode = n;
					break;
				}
			}
			if (existingNode == null) {
				RouteNode newNode = new RouteNode();
				newNode.setRoute(route);
				newNode.setHost(node.getHost());
				newNode.setPort(node.getPort());
				newNode.setServiceURL(node.getServiceURL());
				newNode.setType(RouteNodeType.DYNAMIC);
				newNode.setState(State.ACTIVE);
				newNode.setSet("default");
				int nodeId = routeNodeDao.insert(newNode);
				newNode.setId(nodeId);
				existingNode = newNode;
				LOG.info("Inserted new routeNode=" + newNode);
				System.out.println("Inserted new routeNode=" + newNode);
			}
			
			if(existingNode.getType() == RouteNodeType.DYNAMIC){
				// check if node lease already exists, if not create a new one
				NodeLease lease = nodeRegistry.getLease(existingNode);
				if (lease == null) {
					nodeRegistry.addRouteNode(existingNode, new NodeLease(existingNode, ROUTENODE_EXPIRY_INTERVAL));
				} else {
					lease.renew();
				}
			}
		}
	}
	
	 public RouteInfoUpdate pullRouteUpdate(RouteInfoRequest request) 
			 throws org.apache.thrift.TException{
		 Route route = getActiveRoute(request.getSid());
		if(route == null){ return new RouteInfoUpdate(); }
		
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
	        md.update(route.toString().getBytes("UTF-8"));
	        byte[] res = md.digest();
	        String updateHashCode = new String(Hex.encodeHex(res));
	        if(updateHashCode.equals(request.getLastHashCode())){
	        	RouteInfoUpdate update = new RouteInfoUpdate();
	        	update.setHasUpate(false);
	        	return update;
	        }
			RouteInfo routeInfo = DomainConverter.convertRouteToInfo(route);
			RouteInfoUpdate update = new RouteInfoUpdate();
        	update.setHasUpate(true);
        	update.setHasCode(updateHashCode);
        	update.setResult(routeInfo);
			return update;
		}catch(NoSuchAlgorithmException e){
			LOG.fatal("SHA-1 algorithm cannot be found");
			return new RouteInfoUpdate();
		}catch(UnsupportedEncodingException e){
			LOG.fatal("UTF-8 encoding cannot be found");
			return new RouteInfoUpdate();
		}
	 }

	 public List<RouteInfoUpdate> pullRouteUpdates(List<RouteInfoRequest> requests) 
	    		throws org.apache.thrift.TException{
    	List<RouteInfoUpdate> updates = new ArrayList<RouteInfoUpdate>();
    	for(RouteInfoRequest request : requests){
    		updates.add(pullRouteUpdate(request));
    	}
    	
    	return updates;
	 }
	 
	 private Route getActiveRoute(String sid){
		Route route = routeDao.queryRouteInfoByName(sid);
		if (route == null || route.getState() != State.ACTIVE) {
			return null;
		}
		Iterator<RouteNode> itrNode = route.getNodes().iterator();
		while (itrNode.hasNext()) {
			RouteNode node = itrNode.next();
			if (node.getState() != State.ACTIVE) {
				itrNode.remove();
			}
		}
		Iterator<RouteRule> itrRule = route.getRules().iterator();
		while (itrRule.hasNext()) {
			RouteRule rule = itrRule.next();
			if (rule.getState() != State.ACTIVE) {
				itrRule.remove();
			}
		}
		Iterator<RouteStrategy> itrStrategy = route.getStrategies().iterator();
		while (itrStrategy.hasNext()) {
			RouteStrategy strategy = itrStrategy.next();
			if (strategy.getState() != State.ACTIVE) {
				itrStrategy.remove();
			}
		}
		
		return route;
	 }
}
