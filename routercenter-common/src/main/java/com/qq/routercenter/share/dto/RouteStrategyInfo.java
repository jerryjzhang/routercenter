package com.qq.routercenter.share.dto;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteStrategyInfo {
	private RouteStrategyType type;
	private String option;
	private Map<String, String> config = new HashMap<String, String>();
	
	public RouteStrategyInfo(){
	}
	
	public static RouteStrategyInfo valueOf(RouteStrategy strategy){
		RouteStrategyInfo strategyInfo = new RouteStrategyInfo();
		strategyInfo.config = strategy.buildConfigMap();
		strategyInfo.option = strategy.getOption();
		strategyInfo.type = strategy.getType();
		
		return strategyInfo;
	}
	
	public RouteStrategyType getType() {
		return type;
	}
	public void setType(RouteStrategyType type) {
		this.type = type;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		this.config = config;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RouteStrategyInfo other = (RouteStrategyInfo) obj;
		if (type != other.type)
			return false;
		return true;
	}
}