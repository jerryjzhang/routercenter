package com.qq.routercenter.share.enums;

import com.qq.routercenter.share.domain.ServiceIdentifier;

public enum RouterServices {
	ROUTER_SERVICE("DSE_RESERVE", "RouterCenterService", "/routercenter/RouterCenterService"),
	ROUTER_BACKEND("DSE_RESERVE", "RouterCenterBackend", "/routercenter/RouterCenterBackend");
	
	private RouterServices(String business, String service, String uri){
		sid = new ServiceIdentifier(business, service);
		this.uri = uri;
	}
	
	private ServiceIdentifier sid;
	private String uri;

	public ServiceIdentifier getSid() {
		return sid;
	}

	public String getUri() {
		return uri;
	}
}
