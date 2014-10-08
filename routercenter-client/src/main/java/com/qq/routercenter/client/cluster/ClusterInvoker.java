package com.qq.routercenter.client.cluster;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.qq.routercenter.client.BeanFactory;
import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.StrategyConfigurable;
import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.loadbalance.LoadBalancer;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteStrategyInfo;
import com.qq.routercenter.share.enums.FaultToleranceStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;

public abstract class ClusterInvoker extends StrategyConfigurable{
	public static FaultToleranceStrategy getStrategy(RouteStrategyInfo strategy){
		if(strategy != null && strategy.getType() == RouteStrategyType.FAULT_TOLERANCE){
			return FaultToleranceStrategy.valueOf(strategy.getOption());
		}

    	return null;
    }
	
	public ReturnResult invoke(RouteInfo route, List<RouteNodeInfo> nodes, 
			RemoteInvoker invoker, InvocationContext ctx){
		LoadBalancer lb = BeanFactory.getLoadBalancer(
				LoadBalancer.getStrategy(route.getStrategies().get(RouteStrategyType.LOAD_BALANCE)));
		Arbiter arbiter = BeanFactory.getArbiter(
				Arbiter.getStrategy(route.getStrategies().get(RouteStrategyType.ARBITER)));
		Configuration config = getConfiguration(
				route.getStrategies().get(RouteStrategyType.FAULT_TOLERANCE));
		
		return doInvoke(route, nodes, invoker, ctx, lb, arbiter, config);
	}
	
	protected abstract ReturnResult doInvoke(RouteInfo route, List<RouteNodeInfo> nodes, 
			RemoteInvoker invoker, InvocationContext ctx, LoadBalancer lb, 
			Arbiter arbiter, Configuration config);
}
