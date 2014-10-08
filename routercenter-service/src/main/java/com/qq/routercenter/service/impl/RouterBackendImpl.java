package com.qq.routercenter.service.impl;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import com.qq.routercenter.service.config.BeanFactory;
import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteDTO;
import com.qq.routercenter.share.dto.RouteNodeDTO;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.dto.RouteQueryCriteria;
import com.qq.routercenter.share.dto.RouteRuleDTO;
import com.qq.routercenter.share.dto.RouteRuleQueryCriteria;
import com.qq.routercenter.share.dto.RouteStrategyDTO;
import com.qq.routercenter.share.dto.RouteStrategyQueryCriteria;
import com.qq.routercenter.share.enums.State;
import com.qq.routercenter.share.service.RouterBackend;

public class RouterBackendImpl implements RouterBackend {
    private RouteDao routeDao = BeanFactory.getBeanByType(RouteDao.class);
    private RouteNodeDao routeNodeDao = BeanFactory.getBeanByType(RouteNodeDao.class);
    private RouteRuleDao routeRuleDao = BeanFactory.getBeanByType(RouteRuleDao.class);
    private RouteStrategyDao routeStrategyDao = BeanFactory.getBeanByType(RouteStrategyDao.class);
    
    ModelMapper mapper = new ModelMapper();
    
    /**************************************************************
	 *   APIs used to manage Route
	 * 
	 **************************************************************/
	public int createRoute(RouteDTO routeDTO){
		Route route = mapper.map(routeDTO, Route.class);
		
		if(route.getState() == null)
			route.setState(State.DRAFT);
		return routeDao.insert(route);
	}
	
	public boolean deleteRoute(int id){
		routeNodeDao.deleteByRouteId(id);
		routeRuleDao.deleteByRouteId(id);
		routeStrategyDao.deleteByRouteId(id);
		return routeDao.delete(id) == 1 ? true : false;
	}
	
	public boolean updateRoute(RouteDTO routeDTO){
		Route route = mapper.map(routeDTO, Route.class);
		
		return routeDao.update(route) == 1 ? true : false;
	}
	
	public boolean updateRouteState(int[] ids, State state){
		//TODO: Use only one sql to do the same
		boolean ret = true;
		for(int id : ids){
			if(routeDao.update(id, state) != 1){
				ret = false;
			}
		}
		
		return ret;
	}
	
	Type routeDTOListType = new TypeToken<List<RouteDTO>>() {}.getType();
	public QueryResult<RouteDTO> queryRoute(RouteQueryCriteria criteria, int limit, int offset){
		QueryResult<Route> queryResult = routeDao.queryByCriteria(criteria, limit, offset);
	    
	    List<RouteDTO> routeDTOs = mapper.map(queryResult.getResultSet(), routeDTOListType);
		
		return new QueryResult<RouteDTO>(routeDTOs, queryResult.getTotalCount());
	}
	
	/**************************************************************
	 *   APIs used to manage RouteNode
	 * 
	 **************************************************************/
	public int createNode(RouteNodeDTO nodeDTO){
		RouteNode node = mapper.map(nodeDTO, RouteNode.class);
		
		if(node.getState() == null)
			node.setState(State.DRAFT);
		
		Route route = routeDao.queryByName(nodeDTO.getRouteName());
		node.setRoute(route);
		return routeNodeDao.insert(node) ;
	}
	
	public boolean deleteNode(int id){
		return routeNodeDao.delete(id) == 1 ? true : false;
	}
	
	public boolean updateNode(RouteNodeDTO nodeDTO){
		RouteNode node = mapper.map(nodeDTO, RouteNode.class);
		
		return routeNodeDao.update(node) == 1 ? true : false;
	}
	
	public boolean updateNodeState(int[] ids, State state){
		boolean ret = true;
		for(int id : ids){
			if(routeNodeDao.update(id, state) != 1){
				ret = false;
			}
		}
		
		return ret;
	}
	
	Type nodeDTOListType = new TypeToken<List<RouteNodeDTO>>() {}.getType();
	public QueryResult<RouteNodeDTO> queryNode(RouteNodeQueryCriteria criteria, int limit, int offset){
		QueryResult<RouteNode> queryResult = routeNodeDao.queryByCriteira(criteria, limit, offset);
	    
	    List<RouteNodeDTO> nodeDTOs = mapper.map(queryResult.getResultSet(), nodeDTOListType);
		return new QueryResult<RouteNodeDTO>(nodeDTOs, queryResult.getTotalCount());
	}
	
	public Set<String> queryNodeHost(RouteNodeQueryCriteria criteria){
		List<RouteNode> nodes = routeNodeDao.queryByCriteira(criteria, 0, 0).getResultSet();
		Set<String> hosts = new HashSet<String>();
		for(RouteNode node : nodes){
			hosts.add(node.getHost());
		}
		return hosts;
	} 
	
	/**************************************************************
	 *   APIs used to manage RouteRule
	 * 
	 **************************************************************/
    public int createRule(RouteRuleDTO ruleDTO){
    	RouteRule rule = mapper.map(ruleDTO, RouteRule.class);
    	
		if(rule.getState() == null)
			rule.setState(State.ACTIVE);
		
		Route route = routeDao.queryByName(ruleDTO.getRouteName());
		rule.setRoute(route);
		
    	return routeRuleDao.insert(rule) ;
    }
	
	public boolean deleteRule(int id){
		return routeRuleDao.delete(id) == 1 ? true : false;
	}
	
	public boolean updateRule(RouteRuleDTO ruleDTO){
		RouteRule rule = mapper.map(ruleDTO, RouteRule.class);
		
		return routeRuleDao.update(rule) == 1 ? true : false;
	}
	
	public boolean updateRuleState(int[] ids, State state){
		boolean ret = true;
		for(int id : ids){
			if(routeRuleDao.update(id, state) != 1){
				ret = false;
			}
		}
		
		return ret;
	}
	
	Type ruleDTOListType = new TypeToken<List<RouteRuleDTO>>() {}.getType();
	public QueryResult<RouteRuleDTO> queryRule(RouteRuleQueryCriteria criteria, int limit, int offset){
		QueryResult<RouteRule> queryResult = routeRuleDao.queryByCriteira(criteria, limit, offset);
	    
	    List<RouteRuleDTO> ruleDTOs = mapper.map(queryResult.getResultSet(), ruleDTOListType);
		return new QueryResult<RouteRuleDTO>(ruleDTOs, queryResult.getTotalCount());
	}
	
	/**************************************************************
	 *   APIs used to manage RouteStrategy
	 * 
	 **************************************************************/
    public int createStrategy(RouteStrategyDTO strategyDTO){
    	RouteStrategy strategy = mapper.map(strategyDTO, RouteStrategy.class);
    	
		if(strategy.getState() == null)
			strategy.setState(State.ACTIVE);
		
		Route route = routeDao.queryByName(strategyDTO.getRouteName());
		strategy.setRoute(route);
		
    	return routeStrategyDao.insert(strategy);
    }
	
	public boolean deleteStrategy(int id){
		return routeStrategyDao.delete(id) == 1 ? true : false;
	}
	
	public boolean updateStrategy(RouteStrategyDTO strategyDTO){	
		RouteStrategy strategy = mapper.map(strategyDTO, RouteStrategy.class);
		
		return routeStrategyDao.update(strategy) == 1 ? true : false;
	}
	
	public boolean updateStrategyState(int[] ids, State state){
		boolean ret = true;
		for(int id : ids){
			if(routeStrategyDao.update(id, state) != 1){
				ret = false;
			}
		}
		
		return ret;
	}
	
	Type strategyDTOListType = new TypeToken<List<RouteStrategyDTO>>() {}.getType();
	public QueryResult<RouteStrategyDTO> queryStrategy(RouteStrategyQueryCriteria criteria, int limit, int offset){
		QueryResult<RouteStrategy> queryResult = routeStrategyDao.queryByCriteira(criteria, limit, offset);
	    
	    List<RouteStrategyDTO> strategyDTOs = mapper.map(queryResult.getResultSet(), strategyDTOListType);
		return new QueryResult<RouteStrategyDTO>(strategyDTOs, queryResult.getTotalCount());
	}
}
