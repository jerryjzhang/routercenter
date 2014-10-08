package com.qq.routercenter.share.service;

import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteDTO;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteInfoRequest;
import com.qq.routercenter.share.dto.RouteInfoUpdate;
import com.qq.routercenter.share.dto.RouteNodeInfoList;
import com.qq.routercenter.share.enums.State;

public interface RouterService {	
	/**************************************************************
	 *   APIs used by client-side
	 * 
	 **************************************************************/
	public String ping();
	public RouteInfo pullRoute(ServiceIdentifier sid);
	public RouteInfo pullRouteByGroup(ServiceIdentifier sid, String group);
	public RouteInfoUpdate pullRouteUpdate(RouteInfoRequest request);
	public RouteInfoUpdate[] pullRouteUpdates(RouteInfoRequest[] requests);
	public String lookupNodes(ServiceIdentifier sid);
	public String lookupNodesByGroup(ServiceIdentifier sid, String group);
	
	public void heartbeat(RouteNodeInfoList nodes);
	
	public int createRoute(ServiceIdentifier sid, RouteDTO routeDTO);
	public boolean deleteRoute(ServiceIdentifier sid);
	public boolean updateRouteState(ServiceIdentifier sid, State state);
	public RouteDTO queryRoute(ServiceIdentifier sid);
	public RouteDTO [] queryRoutes(ServiceIdentifier [] sids);
}


