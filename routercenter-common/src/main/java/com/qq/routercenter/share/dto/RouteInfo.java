package com.qq.routercenter.share.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.enums.RouteStrategyType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteInfo {
	private ServiceIdentifier serviceID;
	
	@XmlElement(name = "node", type = RouteNodeInfo.class)
	@XmlElementWrapper(name = "nodes")
	private List<RouteNodeInfo> nodes = new ArrayList<RouteNodeInfo>();

	@XmlElement(name = "rule", type = RouteRuleInfo.class)
	@XmlElementWrapper(name = "rules")
	private List<RouteRuleInfo> rules = new ArrayList<RouteRuleInfo>();

	@XmlElementWrapper(name = "strategies")
	private Map<RouteStrategyType, RouteStrategyInfo> strategies = new HashMap<RouteStrategyType, RouteStrategyInfo>();

	public RouteInfo(){
	}
	
	public static RouteInfo valueOf(Route route){
		RouteInfo routeInfo = new RouteInfo();
		routeInfo.serviceID = ServiceIdentifier.valueOf(route.getName());
		for(RouteNode node : route.getNodes()){
			RouteNodeInfo nodeInfo = RouteNodeInfo.valueof(node);
			nodeInfo.setServiceID(routeInfo.serviceID);
			routeInfo.nodes.add(nodeInfo);
		}
		for(RouteRule rule : route.getRules()){
			routeInfo.rules.add(RouteRuleInfo.valueOf(rule));
		}
		for(RouteStrategy strategy : route.getStrategies()){
			routeInfo.strategies.put(strategy.getType(), RouteStrategyInfo.valueOf(strategy));
		}
		
		return routeInfo;
	}
	
	public ServiceIdentifier getServiceID() {
		return serviceID;
	}

	public void setServiceID(ServiceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public List<RouteNodeInfo> getNodes() {
		return nodes;
	}

	public void setNodes(List<RouteNodeInfo> nodes) {
		this.nodes = nodes;
	}

	public List<RouteRuleInfo> getRules() {
		return rules;
	}

	public void setRules(List<RouteRuleInfo> rules) {
		this.rules = rules;
	}

	public Map<RouteStrategyType, RouteStrategyInfo> getStrategies() {
		return strategies;
	}

	public void setStrategies(Map<RouteStrategyType, RouteStrategyInfo> strategies) {
		this.strategies = strategies;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteInfo other = (RouteInfo) obj;
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!serviceID.equals(other.serviceID))
			return false;
		return true;
	}
}
