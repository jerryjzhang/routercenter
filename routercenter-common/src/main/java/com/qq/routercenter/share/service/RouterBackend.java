package com.qq.routercenter.share.service;

import java.util.Set;

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

public interface RouterBackend {
	/**************************************************************
	 *   APIs used to manage Route
	 * 
	 **************************************************************/
	public int createRoute(RouteDTO routeDTO);
	
	public boolean deleteRoute(int id);
	
	public boolean updateRoute(RouteDTO routeDTO);
	
	public boolean updateRouteState(int[] id, State state);
	
	public QueryResult<RouteDTO> queryRoute(RouteQueryCriteria criteria, int limit, int offset);
    
	/**************************************************************
	 *   APIs used to manage RouteNode
	 * 
	 **************************************************************/
	public int createNode(RouteNodeDTO nodeDTO);
	
	public boolean deleteNode(int id);
	
	public boolean updateNode(RouteNodeDTO nodeDTO);
	
	public boolean updateNodeState(int[] id, State state);
	
	public QueryResult<RouteNodeDTO> queryNode(RouteNodeQueryCriteria criteria, int limit, int offset);
	
	public Set<String> queryNodeHost(RouteNodeQueryCriteria criteria);
	
	/**************************************************************
	 *   APIs used to manage RouteRule
	 * 
	 **************************************************************/
    public int createRule(RouteRuleDTO ruleDTO);
	
	public boolean deleteRule(int id);
	
	public boolean updateRule(RouteRuleDTO ruleDTO);
	
	public boolean updateRuleState(int[] id, State state);
	
	public QueryResult<RouteRuleDTO> queryRule(RouteRuleQueryCriteria criteria, int limit, int offset);
	
	/**************************************************************
	 *   APIs used to manage RouteStrategy
	 * 
	 **************************************************************/
    public int createStrategy(RouteStrategyDTO strategyDTO);
	
	public boolean deleteStrategy(int id);
	
	public boolean updateStrategy(RouteStrategyDTO strategyDTO);
	
	public boolean updateStrategyState(int[] id, State state);
	
	public QueryResult<RouteStrategyDTO> queryStrategy(RouteStrategyQueryCriteria criteria, int limit, int offset);
}
