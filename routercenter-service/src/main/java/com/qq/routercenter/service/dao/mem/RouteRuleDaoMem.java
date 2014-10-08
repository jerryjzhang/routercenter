package com.qq.routercenter.service.dao.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteRuleQueryCriteria;
import com.qq.routercenter.share.enums.State;

public class RouteRuleDaoMem implements RouteRuleDao{
	private Map<Integer, RouteRule> fakeDb = new HashMap<Integer, RouteRule>();
	private AtomicInteger id = new AtomicInteger(1);
	
	public int insert(RouteRule rule){
		rule.setId(id.getAndIncrement());
		fakeDb.put(rule.getId(), rule);
		
		return rule.getId();		
	}
	
	public int delete(int id){
		fakeDb.remove(id);
		
		return 1;
	}
	
	public int update(RouteRule rule){
		fakeDb.put(rule.getId(), rule);
		
		return 1;
	}
	
	public int update(int id, State state){
		RouteRule rule = fakeDb.get(id);
		if(rule != null){
			rule.setState(state);
			fakeDb.put(id, rule);
			return 1;
		}
		return 0;
	}
	
	public QueryResult<RouteRule> queryByCriteira(RouteRuleQueryCriteria criteria, int limit, int offset){
		List<RouteRule> rules = new ArrayList<RouteRule>(fakeDb.values());
		
		if(criteria != null){
			for(Map.Entry<Integer, RouteRule> entry : fakeDb.entrySet()){
				RouteRule rule = entry.getValue();
				if(criteria.getId() > 0 && 
						criteria.getId() != entry.getKey()){
					rules.remove(rule);
				}		
				if(criteria.getState() != null &&
						!criteria.getState().equals(rule.getState())){
					rules.remove(rule);
				}
				//模糊匹配
				if(criteria.getRouteName() != null && rule.getRoute() != null &&
						!rule.getRoute().getName().contains(criteria.getRouteName())){
					rules.remove(rule);
				}
			}
		}
		
		return new QueryResult<RouteRule>(rules, rules.size());
	}
	
	public RouteRule queryById(int id){
		return fakeDb.get(id);
	}
	
	public List<RouteRule> queryByRouteId(int routeId){
		List<RouteRule> rules = new ArrayList<RouteRule>();
		for(Map.Entry<Integer, RouteRule> entry : fakeDb.entrySet()){
			if(entry.getValue().getRoute().getId() == routeId){
				rules.add(entry.getValue());
			}
		}
		return rules;
	}
	
	public int deleteByRouteId(int id){
		List<RouteRule> rules = queryByRouteId(id);
		
		for(RouteRule rule : rules){
			fakeDb.remove(rule.getId());
		}
		
		return 1;
	}
}
