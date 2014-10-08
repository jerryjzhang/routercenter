package com.qq.routercenter.service.dao;

import java.util.List;

import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteStrategyQueryCriteria;
import com.qq.routercenter.share.enums.State;

public interface RouteStrategyDao {
	public int insert(RouteStrategy strategy);
	public int delete(int id);
	public int update(RouteStrategy strategy);
	public int update(int id, State state);
	public QueryResult<RouteStrategy> queryByCriteira(RouteStrategyQueryCriteria 
			criteria, int limit, int offset);
	public RouteStrategy queryById(int id);
	public List<RouteStrategy> queryByRouteId(int routeId);
	public int deleteByRouteId(int id);
}
