package com.qq.routercenter.share.enums;

public enum State {
	ACTIVE("active"),
	INACTIVE("inactive"),
	FREEZE("freeze"), 
	DRAFT("draft"),
	ILLEGAL("illegal");
	
	private final String stateName;
	
	private State(String stateName) {
		this.stateName = stateName;
	}
	
	public static State getServiceState(String stateName) {
		if (ACTIVE.stateName.equalsIgnoreCase(stateName)) {
			return ACTIVE;
		} else if (FREEZE.stateName.equalsIgnoreCase(stateName)) {
			return FREEZE;
		} else if (DRAFT.stateName.equalsIgnoreCase(stateName)) {
			return DRAFT;
		} else if (INACTIVE.stateName.equalsIgnoreCase(stateName)){ 
			return INACTIVE;
		} else {
			return ILLEGAL;
		}		
	}
}
