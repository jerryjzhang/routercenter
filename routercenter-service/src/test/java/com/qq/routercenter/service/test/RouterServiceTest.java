package com.qq.routercenter.service.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.qq.routercenter.service.impl.RouterServiceImpl;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteDTO;
import com.qq.routercenter.share.dto.RouteInfoRequest;
import com.qq.routercenter.share.dto.RouteInfoUpdate;
import com.qq.routercenter.share.enums.State;
import com.qq.routercenter.share.service.RouterService;

public class RouterServiceTest {
	private RouterService service;
	private ServiceIdentifier sid;
	
	@Before
	public void setUp(){
		System.setProperty("CONFIG_MODE", "LOCAL");
		service = new RouterServiceImpl();
		sid = new ServiceIdentifier("test","RouterCenterService");
	}
	
	@Test
	public void testPullRouteUpdate(){
		RouteDTO route = new RouteDTO();
		route.setName(sid.toFullSID());
		route.setState(State.ACTIVE);
		service.createRoute(sid, route);
		String hashCode = null;
		RouteInfoUpdate routeUpdate = service.pullRouteUpdate(new RouteInfoRequest(sid,null));
		Assert.assertTrue(routeUpdate.isHasUpdate());
		Assert.assertEquals(routeUpdate.getResult().getServiceID(), sid);
		hashCode = routeUpdate.getHasCode();
		routeUpdate = service.pullRouteUpdate(new RouteInfoRequest(sid,hashCode));
		Assert.assertFalse(routeUpdate.isHasUpdate());
		Assert.assertNull(routeUpdate.getResult());
	}
}
