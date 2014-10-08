package com.qq.routercenter.client.loadbalance;

import java.util.List;

import com.qq.routercenter.client.StrategyConfigurable;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteStrategyInfo;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;

/**
 * The base class of all LoadBalancers that select one {@link RouteNodeInfo}
 * out of a list of RouteNodes based on specific {@link LoadBalanceStrategy}
 * 
 * @author jerryjzhang
 *
 */
public abstract class LoadBalancer extends StrategyConfigurable{
	public static LoadBalanceStrategy getStrategy(RouteStrategyInfo strategy) {
		if(strategy != null && strategy.getType() == RouteStrategyType.LOAD_BALANCE){
			return LoadBalanceStrategy.valueOf(strategy.getOption());
		}

    	return null;
	}

	/**
	 * Similar to select(), this method select one RouteNode out of
	 * a list of RouteNodes. The only difference is that RouteNodes 
	 * in parameter 'invokedNodes' will be ignored in the selection.
	 * 
	 * @param nodes
	 * @param invokedNodes
	 * @return
	 */
	public RouteNodeInfo reselect(RouteInfo route, List<RouteNodeInfo> nodes,
			List<RouteNodeInfo> invokedNodes) {
		if (nodes == null || nodes.isEmpty()
				|| nodes.size() <= invokedNodes.size())
			return null;

		// retry 3 times of nodes size in maximum to prevent infinite loop
		for(int i=1; i<= nodes.size() * 3; i++) {
			RouteNodeInfo node = select(route, nodes);
			//no node can be selected, just return
			if(node == null){
				break;
			}
			if (!invokedNodes.contains(node)) {
				return node;
			}
		}
		
		return null;
	}

	/**
	 * Select one RouteNode out of a list of RouteNodes based on specific
	 * LoadBalance algorithm.
	 * 
	 * @param nodes
	 * @return
	 */
	public RouteNodeInfo select(RouteInfo route, List<RouteNodeInfo> nodes){
		if(nodes == null || nodes.isEmpty())return null;
		
		if(nodes.size() == 1){
			return nodes.get(0);
		}
		
		return doSelect(route, nodes);
	}
	
	protected abstract RouteNodeInfo doSelect(RouteInfo route, List<RouteNodeInfo> nodes);
}
