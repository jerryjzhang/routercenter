package com.qq.routercenter.share.enums;

import java.util.List;

public enum RouteRuleOp {
	EQUAL("="), 
	INEQUAL("!="), 
	BETWEEN("between"), 
	ILLEGAL("illegal");
	
	private final String operator;
	
	private RouteRuleOp(String operator) {
		this.operator = operator;
	}
	
	public static RouteRuleOp getRouteOp(String userOperator) {
		if (EQUAL.operator.equalsIgnoreCase(userOperator)) {
			return EQUAL;
		} else if (INEQUAL.operator.equalsIgnoreCase(userOperator)) {
			return INEQUAL;
		} else if (BETWEEN.operator.equalsIgnoreCase(userOperator)) {
			return BETWEEN;
		} else {
			return ILLEGAL;
		}
	}
	
	public static boolean opRunValue(RouteRuleOp routerOperator, 
			                         List<String> options, 
			                         String opValue) {
		switch (routerOperator) {
			case EQUAL:
				return options.get(0).equals(opValue);
			case INEQUAL:
				return !options.get(0).equals(opValue);
			case BETWEEN:
				long longValue = Long.valueOf(opValue);
				long minValue = Long.valueOf(options.get(0));
				long maxValue = Long.valueOf(options.get(1));
				
				return (longValue >= minValue && longValue <= maxValue);
			default:
		}
		
		return false;
	}
}
