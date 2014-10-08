package com.qq.routercenter.share.enums;

public enum RouteStrategyType {
	LOAD_BALANCE("load_balance"), 
	FAULT_TOLERANCE("fault_tolerance"),
	ARBITER("abiter"),
	ILLEGAL("illegal");
	
	private final String configType;
	
	private RouteStrategyType(String configType) {
		this.configType = configType;
	}
	
	public static RouteStrategyType getConfigType(String userConfigType) {
		if (LOAD_BALANCE.configType.equalsIgnoreCase(userConfigType)) {
			return LOAD_BALANCE;
		} else if (FAULT_TOLERANCE.configType.equalsIgnoreCase(userConfigType)) {
			return FAULT_TOLERANCE;
		} else {
			return ILLEGAL;
		}
	}
}
