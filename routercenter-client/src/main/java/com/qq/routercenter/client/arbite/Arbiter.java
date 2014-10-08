package com.qq.routercenter.client.arbite;

import java.util.List;

import com.qq.routercenter.client.StrategyConfigurable;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteStrategyInfo;
import com.qq.routercenter.share.enums.ArbiterStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;

/* Check whether we need to remove a router in specified times or period. */
public abstract class Arbiter extends StrategyConfigurable{
	public static ArbiterStrategy getStrategy(RouteStrategyInfo strategy){
		if(strategy != null && strategy.getType() == RouteStrategyType.ARBITER){
			return ArbiterStrategy.valueOf(strategy.getOption());
		}

    	return null;
    }
	
    public abstract void arbit(RouteInfo route, RouteNodeInfo node);
    public abstract List<RouteNodeInfo> guard(RouteInfo route, List<RouteNodeInfo> nodes);
}
