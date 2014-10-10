package com.qq.routercenter.client.bus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteInfoList;
import com.qq.routercenter.share.service.RouteNodeInfo;

public abstract class RouteInfoBus {
	private static final Logger LOG = Logger.getLogger(RouteInfoBus.class);

	protected Set<String> discoveredServices = new CopyOnWriteArraySet<String>();
	protected List<RouteNodeInfo> registeredNodes = new CopyOnWriteArrayList<RouteNodeInfo>();
	
	public void load(String filePath){
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		RouteInfoList routeList = null;
		if(is != null){
			try{
				routeList = RouteFileManager.read(RouteInfoList.class, is);
			}catch(RouteFileException e){
				LOG.fatal("Failed to load service configs " + e.getMessage());
			}
		}else{
			LOG.fatal("Failed to find service config file:" + filePath);
		}
		
		if(routeList != null){
			for(RouteInfo route : routeList.getRoutes()){
				RouteInfoCache.loadRoute(route.getSid(), route, false);
			}
		}
	}
	
	public void load(File file){
		RouteInfoList routeList = null;
		try{
			InputStream is = new FileInputStream(file);
			routeList = RouteFileManager.read(RouteInfoList.class, is);
		}catch(RouteFileException e){
			LOG.fatal("Failed to load service configs " + e.getMessage());
		}catch(FileNotFoundException e){
			LOG.fatal("Failed to find service config file:" + file.getAbsolutePath());
		}	
		
		if(routeList != null){
			for(RouteInfo route : routeList.getRoutes()){
				RouteInfoCache.loadRoute(route.getSid(), route, false);
			}
		}
	}
	
	public void load(RouteInfo route){
		RouteInfoCache.loadRoute(route.getSid(), route, false);
	}
	
	public RouteInfo get(String serviceID){
		return RouteInfoCache.getRoute(serviceID);
	}
	
    public void discoverService(String serviceID){
    	discoveredServices.add(serviceID);
    }
    public void undiscoveryService(String serviceID){
    	discoveredServices.remove(serviceID);
    	RouteInfoCache.evitRoute(serviceID);
    }
    public void registerService(RouteNodeInfo node){
    	registeredNodes.add(node);
    }
    public void unregisterService(String serviceID){
    	Iterator<RouteNodeInfo> itr = registeredNodes.iterator();
    	while(itr.hasNext()){
    		RouteNodeInfo node = itr.next();
    		if(node.getSid().equals(serviceID)){
    			itr.remove();
    		}
    	}
    }
}