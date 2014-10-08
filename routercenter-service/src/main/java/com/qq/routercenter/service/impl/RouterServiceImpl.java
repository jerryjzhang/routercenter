package com.qq.routercenter.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.qq.routercenter.service.config.BeanFactory;
import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.service.registry.NodeLease;
import com.qq.routercenter.service.registry.RouteRegistry;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteDTO;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteInfoRequest;
import com.qq.routercenter.share.dto.RouteInfoUpdate;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteNodeInfoList;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.ServiceError;
import com.qq.routercenter.share.enums.State;
import com.qq.routercenter.share.service.RouterService;
import com.qq.taserver.common.exception.BusinessException;

public class RouterServiceImpl implements RouterService {
	private static final Logger LOG = Logger
			.getLogger(RouterServiceImpl.class);

    private RouteDao routeDao = BeanFactory.getBeanByType(RouteDao.class);
    private RouteNodeDao routeNodeDao = BeanFactory.getBeanByType(RouteNodeDao.class);
    private RouteRuleDao routeRuleDao = BeanFactory.getBeanByType(RouteRuleDao.class);
    private RouteStrategyDao routeStrategyDao = BeanFactory.getBeanByType(RouteStrategyDao.class);

	static long ROUTENODE_EXPIRY_INTERVAL = 3 * 60 * 1000;
	static long ROUTENODE_CHECK_INTERVAL  = 1 * 60 * 1000;

	private RouteRegistry nodeRegistry = BeanFactory.getBeanByType(RouteRegistry.class);
	
	ModelMapper mapper = new ModelMapper();

	public RouterServiceImpl() {
		//get the existing route nodes and fill the registry
		initRouteNodes();
		//schedule a regular task to expire abnormal route nodes
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable(){
			public void run(){
				expireRouteNodes();
			}
		}, ROUTENODE_CHECK_INTERVAL, ROUTENODE_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	public String ping(){
		return "RouterService OK";
	}
	
	public int createRoute(ServiceIdentifier sid, RouteDTO routeDTO){
		Route route = routeDao.queryByName(sid.toFullSID());
		if(route != null){
			throw new BusinessException(ServiceError.OBJECT_EXISTED.getCode(), 
					"Route with sid=" + sid + " already exists");
		}
		route = mapper.map(routeDTO, Route.class);
		
		if(route.getState() == null)
			route.setState(State.DRAFT);
		return routeDao.insert(route);
	}

	public RouteDTO queryRoute(ServiceIdentifier sid){
		Route route = routeDao.queryByName(sid.toFullSID());
		
		if(route != null){
			return mapper.map(route, RouteDTO.class);
		}else{
			return null;
		}
	}
	
	public RouteDTO [] queryRoutes(ServiceIdentifier [] sids){
		RouteDTO[] routeDTOs = new RouteDTO[sids.length];
		for (int i = 0; i < sids.length; i++) {
			routeDTOs[i] = queryRoute(sids[i]);
		}
		
		return routeDTOs;
	}

	public boolean updateRouteState(ServiceIdentifier sid, State state) {
		Route route = routeDao.queryByName(sid.toFullSID());
		if (route == null) {
			throw new BusinessException(ServiceError.OBJECT_NOT_FOUND.getCode(),
					"No route found with sid=" + sid);
		}
		
		routeDao.update(route.getId(), state);
		return true;
	}

	public boolean deleteRoute(ServiceIdentifier sid) {
		Route route = routeDao.queryByName(sid.toFullSID());
		if (route == null) {
			throw new BusinessException(ServiceError.OBJECT_NOT_FOUND.getCode(),
					"No route found with sid=" + sid);
		}
		routeNodeDao.deleteByRouteId(route.getId());
		routeRuleDao.deleteByRouteId(route.getId());
		routeStrategyDao.deleteByRouteId(route.getId());
		routeDao.delete(route.getId());
		return true;
	}

	public RouteInfo pullRoute(ServiceIdentifier sid) {
		Route route = getActiveRoute(sid);
		if(route == null){ return null; }
		
		return RouteInfo.valueOf(route);
	}
	
	public RouteInfo pullRouteByGroup(ServiceIdentifier sid, String group){
		Route route = getActiveRoute(sid);
		
		if(route != null && group != null){
			Iterator<RouteNode> itr = route.getNodes().iterator();
			while(itr.hasNext()){
				RouteNode node = itr.next();
				if(!node.getSet().equals(group)){
					itr.remove();
				}
			}
		}
		
		return RouteInfo.valueOf(route);
	}
		
	public RouteInfoUpdate[] pullRouteUpdates(RouteInfoRequest[] requests){
		RouteInfoUpdate [] updates = new RouteInfoUpdate[requests.length];
		for(int i=0;i<requests.length;i++){
			updates[i] = pullRouteUpdate(requests[i]);
		}
		return updates;
	}
	
	public RouteInfoUpdate pullRouteUpdate(RouteInfoRequest request){
		Route route = getActiveRoute(request.getSid());
		if(route == null){ return null; }
		
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
	        md.update(route.toString().getBytes("UTF-8"));
	        byte[] res = md.digest();
	        String updateHashCode = new String(Hex.encodeHex(res));
	        if(updateHashCode.equals(request.getLastHashCode())){
	        	return new RouteInfoUpdate(false, null, null);
	        }
			RouteInfo routeInfo = RouteInfo.valueOf(route);
			return new RouteInfoUpdate(true, routeInfo, updateHashCode);
		}catch(NoSuchAlgorithmException e){
			LOG.fatal("SHA-1 algorithm cannot be found");
			return null;
		}catch(UnsupportedEncodingException e){
			LOG.fatal("UTF-8 encoding cannot be found");
			return null;
		}
	}
		
	public String lookupNodes(ServiceIdentifier sid){
		return lookupNodesByGroup(sid, null);
	}
	
	public String lookupNodesByGroup(ServiceIdentifier sid, String group){
		Route route = routeDao.queryRouteInfoByName(sid.toFullSID());
		if (route == null || route.getState() != State.ACTIVE) {
			return null;
		}
		StringBuilder urls = new StringBuilder();
		for(RouteNode node : route.getNodes()){
			if(node.getState() == State.ACTIVE){
				if(group == null || node.getSet().equals(group)){
					urls.append(node.getServiceURL());
					urls.append(",");
				}
			}
		}
		return urls.substring(0, urls.length() - 1);
	}
	
	public void heartbeat(RouteNodeInfoList nodeList) {
		for (RouteNodeInfo node : nodeList.getNodeInfos()) {
			if(LOG.isDebugEnabled()){
				LOG.debug("Received node heartbeat: " + node);
			}
			// reported route node must at least have associated
			// routeName and serviceURL
			if (node.getServiceID() == null || node.getHost() == null
					|| node.getPort() == 0)
				continue;
			
			// routeName is the same as service identifier, 
			// a.k.a so-called SID
			String sid = node.getServiceID().toFullSID();
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
	
	private void expireRouteNodes(){
		LOG.debug("Running the expire task");
		Set<RouteNode> expiredNodes = nodeRegistry.expireRouteNodes();
		if(!expiredNodes.isEmpty()){
			routeNodeDao.deleteBatch(expiredNodes);
			if(LOG.isDebugEnabled()){
				LOG.debug("Expired route nodes: " + expiredNodes);
			}

		}
	}
	
	private void initRouteNodes(){
		LOG.debug("Registering existing route nodes");
		RouteNodeQueryCriteria criteria = new RouteNodeQueryCriteria();
		criteria.setType(RouteNodeType.DYNAMIC);
		criteria.setState(State.ACTIVE);
		
		List<RouteNode> nodes = routeNodeDao.queryByCriteira(criteria, 0, 0).getResultSet();
		for(RouteNode node : nodes){
			nodeRegistry.addRouteNode(node, new NodeLease(node, ROUTENODE_EXPIRY_INTERVAL));
			if(LOG.isDebugEnabled()){
				LOG.debug("Registered existing node: " + node);
			}
		}
	}
	
	private Route getActiveRoute(ServiceIdentifier sid){
		Route route = routeDao.queryRouteInfoByName(sid.toFullSID());
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
