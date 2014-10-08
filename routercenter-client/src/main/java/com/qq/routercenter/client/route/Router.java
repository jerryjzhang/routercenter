package com.qq.routercenter.client.route;

import java.util.ArrayList;
import java.util.List;

import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;

/**
 * The base class of all Routers that route invocations
 * to some particular RouteNodes.
 * 
 * @author jerryjzhang
 *
 */
public abstract class Router {
	static final String ROUTE_RULE_DELIMITER = ",";
	
	static boolean hasMatchingPattern(String[] patterns, String value) {
		for(String pattern : patterns){
			if(isMatchPattern(pattern, value)){
				return true;
			}
		}
		
		return false;
	}

	static boolean isMatchPattern(String pattern, String value) {
        if ("*".equals(pattern))
            return true;
        if((pattern == null || pattern.length() == 0) 
                && (value == null || value.length() == 0)) 
            return true;
        if((pattern == null || pattern.length() == 0) 
                || (value == null || value.length() == 0)) 
            return false;
        
        int i = pattern.lastIndexOf('*');
        
        //no star found
        if(i == -1){
        	return value.equals(pattern);
        }
        
        boolean leftMatch = true;
        boolean rightMatch = true;
        if(i != 0){
        	leftMatch = value.startsWith(pattern.substring(0, i));
        }
        
        if(i != pattern.length() - 1){
        	rightMatch = value.endsWith(pattern.substring(i + 1));
        }

		return leftMatch && rightMatch;
	}

	static List<RouteNodeInfo> findMatchingNodes(String[] patterns, List<RouteNodeInfo> nodes) {
		List<RouteNodeInfo> matchedNodes = new ArrayList<RouteNodeInfo>();
		
		for(RouteNodeInfo node : nodes){
			if(hasMatchingPattern(patterns, node.getHost())){
				matchedNodes.add(node);
			}
		}
		
		return matchedNodes;
	}
	
	public abstract List<RouteNodeInfo> route(RouteRuleInfo rule, List<RouteNodeInfo> nodes, InvocationContext ctx);
}
