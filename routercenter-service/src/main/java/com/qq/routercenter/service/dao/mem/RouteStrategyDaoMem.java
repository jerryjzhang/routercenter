package com.qq.routercenter.service.dao.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteStrategyQueryCriteria;
import com.qq.routercenter.share.enums.State;

public class RouteStrategyDaoMem implements RouteStrategyDao {
	private Map<Integer, RouteStrategy> fakeDb = new HashMap<Integer, RouteStrategy>();
	private AtomicInteger id = new AtomicInteger(1);

	public int insert(RouteStrategy strategy) {
		strategy.setId(id.getAndIncrement());
		fakeDb.put(strategy.getId(), strategy);

		return strategy.getId();
	}

	public int delete(int id) {
		fakeDb.remove(id);

		return 1;
	}

	public int update(RouteStrategy strategy) {
		fakeDb.put(strategy.getId(), strategy);

		return 1;
	}

	public int update(int id, State state) {
		RouteStrategy strategy = fakeDb.get(id);
		if (strategy != null) {
			strategy.setState(state);
			fakeDb.put(id, strategy);
			return 1;
		}
		return 0;
	}

	public QueryResult<RouteStrategy> queryByCriteira(
			RouteStrategyQueryCriteria criteria, int limit, int offset) {
		List<RouteStrategy> strategies = new ArrayList<RouteStrategy>(
				fakeDb.values());

		if (criteria != null) {
			for (Map.Entry<Integer, RouteStrategy> entry : fakeDb.entrySet()) {
				RouteStrategy strategy = entry.getValue();
				if (criteria.getId() > 0 && criteria.getId() != entry.getKey()) {
					strategies.remove(strategy);
				}
				if (criteria.getState() != null
						&& !criteria.getState().equals(strategy.getState())) {
					strategies.remove(strategy);
				}
				// 模糊匹配
				if (criteria.getRouteName() != null
						&& strategy.getRoute() != null
						&& !strategy.getRoute().getName()
								.contains(criteria.getRouteName())) {
					strategies.remove(strategy);
				}
			}
		}
		
		return new QueryResult<RouteStrategy>(strategies, strategies.size());
	}

	public RouteStrategy queryById(int id) {
		return fakeDb.get(id);
	}

	public List<RouteStrategy> queryByRouteId(int routeId) {
		List<RouteStrategy> strategies = new ArrayList<RouteStrategy>();
		for (Map.Entry<Integer, RouteStrategy> entry : fakeDb.entrySet()) {
			if (entry.getValue().getRoute().getId() == routeId) {
				strategies.add(entry.getValue());
			}
		}
		return strategies;
	}

	public int deleteByRouteId(int id) {
		List<RouteStrategy> strategies = queryByRouteId(id);

		for (RouteStrategy strategy : strategies) {
			fakeDb.remove(strategy.getId());
		}

		return 1;
	}
}
