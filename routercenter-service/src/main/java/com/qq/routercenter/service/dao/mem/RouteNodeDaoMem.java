package com.qq.routercenter.service.dao.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNodeDaoMem implements RouteNodeDao{
	private Map<Integer, RouteNode> fakeDb = new HashMap<Integer, RouteNode>();
	private AtomicInteger id = new AtomicInteger(1);
	
	private RouteNode fakeNode = new RouteNode();
	
	public RouteNodeDaoMem(){
		Route route = new Route();
		route.setName(new ServiceIdentifier("fake", "service").toFullSID());
		route.getNodes().add(fakeNode);
		fakeNode.setHost("localhost");
		fakeNode.setPort(50030);
		fakeNode.setServiceURL("localhost:50030/fakeNode");
		fakeNode.setRoute(route);
		fakeNode.setType(RouteNodeType.DYNAMIC);
		fakeNode.setState(State.ACTIVE);
		insert(fakeNode);
	}
	
	public int insert(RouteNode node){
		if(queryByUrl(node.getServiceURL()) != null){
			throw new RuntimeException("Object already exists");
		}
		node.setId(id.getAndIncrement());
		fakeDb.put(node.getId(), node);
		
		return node.getId();		
	}
	
	public int delete(int id){
		fakeDb.remove(id);
		
		return 1;
	}
	
	public int[] deleteBatch(Set<RouteNode> nodes){
		for(RouteNode node : nodes){
			delete(node.getId());
		}
		
		return new int[]{1};
	}
	
	public int update(RouteNode node){
		fakeDb.put(node.getId(), node);
		
		return 1;
	}
	
	public int update(int id, State state){
		RouteNode node = fakeDb.get(id);
		if(node != null){
			node.setState(state);
			fakeDb.put(id, node);
			return 1;
		}
		return 0;
	}
	
	public QueryResult<RouteNode> queryByCriteira(RouteNodeQueryCriteria criteria, int limit, int offset){
		List<RouteNode> nodes = new ArrayList<RouteNode>(fakeDb.values());
		
		if(criteria != null){
			for(Map.Entry<Integer, RouteNode> entry : fakeDb.entrySet()){
				RouteNode node = entry.getValue();
				if(criteria.getId() > 0 && 
						criteria.getId() != entry.getKey()){
					nodes.remove(node);
				}		
				if(criteria.getState() != null &&
						!criteria.getState().equals(node.getState())){
					nodes.remove(node);
				}
				if(criteria.getType() != null &&
						!criteria.getType().equals(node.getType())){
					nodes.remove(node);
				}
				if(criteria.getPort() > 0 &&
						criteria.getPort() != node.getPort()){
					nodes.remove(node);
				}
				//模糊匹配
				if(criteria.getHost() != null &&
						!node.getHost().contains(criteria.getHost())){
					nodes.remove(node);
				}
				//模糊匹配
				if(criteria.getServiceURL() != null &&
						!node.getServiceURL().contains(criteria.getServiceURL())){
					nodes.remove(node);
				}
				//模糊匹配
				if(criteria.getSet() != null &&
						!node.getSet().contains(criteria.getSet())){
					nodes.remove(node);
				}
				//模糊匹配
				if(criteria.getRouteName() != null && node.getRoute() != null &&
						!node.getRoute().getName().contains(criteria.getRouteName())){
					nodes.remove(node);
				}
			}
		}
		
		return new QueryResult<RouteNode>(nodes, nodes.size());
	}
	
	public RouteNode queryById(int id){
		return fakeDb.get(id);
	}
	
	public RouteNode queryByUrl(String url){
		for(Map.Entry<Integer, RouteNode> entry : fakeDb.entrySet()){
			if(entry.getValue().getServiceURL().equals(url)){
				return entry.getValue();
			}
		}
		
		return null;
	}

	@Override
	public List<RouteNode> queryByRouteId(int routeId) {
		List<RouteNode> nodes = new ArrayList<RouteNode>();
		for(Map.Entry<Integer, RouteNode> entry : fakeDb.entrySet()){
			if(entry.getValue().getRoute().getId() == routeId){
				nodes.add(entry.getValue());
			}
		}
		return nodes;
	}

	public int deleteByRouteId(int routeId){
		List<RouteNode> nodes = queryByRouteId(routeId);
		
		for(RouteNode node : nodes){
			fakeDb.remove(node.getId());
		}
		
		return 1;
	}
}
