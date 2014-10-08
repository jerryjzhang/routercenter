package com.qq.routercenter.share.dto;

import com.qq.routercenter.share.domain.ServiceIdentifier;

public class RouteInfoRequest {
	private ServiceIdentifier sid;
	private String lastHashCode;
	
	public RouteInfoRequest(){}
	
	public RouteInfoRequest(ServiceIdentifier sid, String lastHashCode) {
		this.sid = sid;
		this.lastHashCode = lastHashCode;
	}
	public ServiceIdentifier getSid() {
		return sid;
	}
	public void setSid(ServiceIdentifier sid) {
		this.sid = sid;
	}
	public String getLastHashCode() {
		return lastHashCode;
	}
	public void setLastHashCode(String lastHashCode) {
		this.lastHashCode = lastHashCode;
	}
}
