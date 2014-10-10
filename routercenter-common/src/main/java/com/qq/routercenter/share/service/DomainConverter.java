package com.qq.routercenter.share.service;

import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;

public class DomainConverter {
	public static RouteInfo convertRouteToInfo(Route route){
		RouteInfo routeInfo = new RouteInfo();
		routeInfo.setSid(route.getName());
		for(RouteNode node : route.getNodes()){
			RouteNodeInfo nodeInfo = convertRouteNodeToInfo(node);
			nodeInfo.setSid(route.getName());
			routeInfo.addToNodes(nodeInfo);
		}
		for(RouteRule rule : route.getRules()){
			routeInfo.addToRules(convertRouteRuleToInfo(rule));
		}
		for(RouteStrategy strategy : route.getStrategies()){
			routeInfo.putToStrategies(strategy.getType().toString(), convertRouteStrategyToInfo(strategy));
		}
		
		return routeInfo;
	}
	
	public static RouteNodeInfo convertRouteNodeToInfo(RouteNode node){
		RouteNodeInfo nodeInfo = new RouteNodeInfo();
		nodeInfo.host = node.getHost();
		nodeInfo.port = node.getPort();
		nodeInfo.serviceURL = node.getServiceURL();
		
		return nodeInfo;
	}
	
	public static RouteRuleInfo convertRouteRuleToInfo(RouteRule rule){
		RouteRuleInfo ruleInfo = new RouteRuleInfo();
		ruleInfo.destination = rule.getDestination();
		ruleInfo.srcOp = rule.getSrcOp().toString();
		ruleInfo.srcProp = rule.getSrcProp();
		ruleInfo.srcValue = rule.getSrcValue();
		ruleInfo.type = rule.getType().toString();
		
		return ruleInfo;
	}
	
	public static RouteStrategyInfo convertRouteStrategyToInfo(RouteStrategy strategy){
		RouteStrategyInfo strategyInfo = new RouteStrategyInfo();
		strategyInfo.config = strategy.buildConfigMap();
		strategyInfo.option = strategy.getOption();
		strategyInfo.type = strategy.getType().toString();
		
		return strategyInfo;
	}
}
