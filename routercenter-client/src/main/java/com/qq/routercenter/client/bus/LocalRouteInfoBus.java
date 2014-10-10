package com.qq.routercenter.client.bus;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteInfoList;

public class LocalRouteInfoBus extends RouteInfoBus {
	private static final Logger LOG = Logger.getLogger(LocalRouteInfoBus.class);
	
	public LocalRouteInfoBus(String filePath) {
		load(filePath);
	}
	
	public LocalRouteInfoBus(File file){
		load(file);
	}
	
	public LocalRouteInfoBus(RouteInfoList routeInfoList){
		for(RouteInfo route : routeInfoList.getRoutes()){
			load(route);
		}
	}
	
	public LocalRouteInfoBus(InputStream is){
		RouteInfoList routeList = null;
		try{
			routeList = RouteFileManager.read(RouteInfoList.class, is);
		}catch(RouteFileException e){
			LOG.fatal("Failed to load RouteInfos " + e.getMessage());
		}
		
		if(routeList != null){
			for(RouteInfo route : routeList.getRoutes()){
				RouteInfoCache.loadRoute(route.getSid(), route, false);
			}
		}
	}
}
