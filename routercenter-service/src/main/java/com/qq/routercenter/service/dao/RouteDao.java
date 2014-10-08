package com.qq.routercenter.service.dao;

import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteQueryCriteria;
import com.qq.routercenter.share.enums.State;

public interface RouteDao {
	public int insert(Route route);
	public int delete(int id);
	public int update(Route route);
	public int update(int id, State state);
	public QueryResult<Route> queryByCriteria(RouteQueryCriteria criteria, int limit, int offset);
	public Route queryById(int id);
	public Route queryByName(String name);
	public Route queryRouteInfoByName(String name);
}
