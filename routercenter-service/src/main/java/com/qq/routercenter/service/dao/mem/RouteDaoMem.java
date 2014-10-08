package com.qq.routercenter.service.dao.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.service.config.BeanFactory;
import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteQueryCriteria;
import com.qq.routercenter.share.enums.State;

public class RouteDaoMem implements RouteDao {
	private Map<Integer, Route> fakeDb = new HashMap<Integer, Route>();
	private AtomicInteger id = new AtomicInteger(1);
	
	public int insert(Route route) {
		if(queryByName(route.getName()) != null){
			throw new RuntimeException("Object already exists");
		}
		route.setId(id.getAndIncrement());
		fakeDb.put(route.getId(), route);
		
		return route.getId();
	}
	
	public int delete(int id) {
	    fakeDb.remove(id);	
	    
	    return 1;
	}
	
	public int update(Route route) {
		fakeDb.put(route.getId(), route);
		
		return 1;
	}
	
	public int update(int id, State state){
		Route route = fakeDb.get(id);
		if(route != null){
			route.setState(state);
			fakeDb.put(id, route);
			return 1;
		}
		return 0;
	}
	
	public Route queryById(int id) {
		return fakeDb.get(id);
	}
	
	public Route queryByName(String name) {
		for(Map.Entry<Integer, Route> entry : fakeDb.entrySet()){
			if(entry.getValue().getName().equals(name)){
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public Route queryRouteInfoByName(String name) {
		RouteNodeDao routeNodeDao = BeanFactory.getBeanByType(RouteNodeDao.class);
		Route route = null;
		for(Map.Entry<Integer, Route> entry : fakeDb.entrySet()){
			if(entry.getValue().getName().equals(name)){
				route = entry.getValue();
				//TODO: retrieve RouteRule, RouteStrategy for this Route
				List<RouteNode> nodes = routeNodeDao.queryByRouteId(route.getId());
				route.setNodes(nodes);
			}
		}
		return route;
	}
	
	public QueryResult<Route> queryByCriteria(RouteQueryCriteria criteria, int limit, int offset) {
		List<Route> routes = new ArrayList<Route>(fakeDb.values());
		
		if(criteria != null){
			for(Map.Entry<Integer, Route> entry : fakeDb.entrySet()){
				Route route = entry.getValue();
				if(criteria.getId() > 0 && 
						criteria.getId() != entry.getKey()){
					routes.remove(route);
				}		
				if(criteria.getState() != null &&
						!criteria.getState().equals(route.getState())){
					routes.remove(route);
				}
				//模糊匹配
				if(criteria.getName() != null &&
						!route.getName().contains(criteria.getName())){
					routes.remove(route);
				}
				//模糊匹配
				if(criteria.getIncharge() != null &&
						!route.getIncharge().contains(criteria.getIncharge())){
					routes.remove(route);
				}
			}
		}
		
		return new QueryResult<Route>(routes, routes.size());
	}	
}
