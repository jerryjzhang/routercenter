package com.qq.routercenter.service.dao;

import java.util.List;
import java.util.Set;

import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.enums.State;

public interface RouteNodeDao {
	public int insert(RouteNode node);
	public int delete(int id);
	public int[] deleteBatch(Set<RouteNode> nodes);
	public int deleteByRouteId(int routeId);
	public int update(RouteNode node);
	public int update(int id, State state);
	public QueryResult<RouteNode> queryByCriteira(RouteNodeQueryCriteria criteria, int limit, int offset);
	public RouteNode queryById(int id);
	public RouteNode queryByUrl(String url);
	public List<RouteNode> queryByRouteId(int routeId);
}
