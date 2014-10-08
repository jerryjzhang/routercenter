package com.qq.routercenter.service.dao;

import java.util.List;

import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteRuleQueryCriteria;
import com.qq.routercenter.share.enums.State;

public interface RouteRuleDao {
	public int insert(RouteRule rule);
	public int delete(int id);
	public int update(RouteRule rule);
	public int update(int id, State state);
	public QueryResult<RouteRule> queryByCriteira(RouteRuleQueryCriteria criteria, int limit, int offset);
	public RouteRule queryById(int id);
	public List<RouteRule> queryByRouteId(int routeId);
	public int deleteByRouteId(int id);
}
