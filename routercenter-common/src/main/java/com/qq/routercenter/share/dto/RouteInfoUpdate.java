package com.qq.routercenter.share.dto;

public class RouteInfoUpdate {
	private boolean hasUpdate;
	private RouteInfo result;
	private String hasCode;
	
	public RouteInfoUpdate(){ }
	
	public RouteInfoUpdate(boolean hasUpdate, RouteInfo result, String hasCode) {
		super();
		this.hasUpdate = hasUpdate;
		this.result = result;
		this.hasCode = hasCode;
	}

	public boolean isHasUpdate() {
		return hasUpdate;
	}
	public void setHasUpdate(boolean hasUpdate) {
		this.hasUpdate = hasUpdate;
	}
	public RouteInfo getResult() {
		return result;
	}
	public void setResult(RouteInfo result) {
		this.result = result;
	}
	public String getHasCode() {
		return hasCode;
	}
	public void setHasCode(String hasCode) {
		this.hasCode = hasCode;
	}
}
