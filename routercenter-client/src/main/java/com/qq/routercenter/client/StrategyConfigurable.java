package com.qq.routercenter.client;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

import com.qq.routercenter.share.dto.RouteStrategyInfo;

public abstract class StrategyConfigurable {
	public Configuration getConfiguration(RouteStrategyInfo strategy) {
		if(strategy != null && strategy.getConfig() != null){
			return new MapConfiguration(strategy.getConfig());
		}
		
    	return new BaseConfiguration();
	}


}
