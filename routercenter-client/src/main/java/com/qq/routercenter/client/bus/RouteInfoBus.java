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

import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteInfoList;
import com.qq.routercenter.share.dto.RouteNodeInfo;

public abstract class RouteInfoBus {
	private static final Logger LOG = Logger.getLogger(RouteInfoBus.class);

	protected Set<ServiceIdentifier> discoveredServices = new CopyOnWriteArraySet<ServiceIdentifier>();
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
				RouteInfoCache.loadRoute(route.getServiceID(), route, false);
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
				RouteInfoCache.loadRoute(route.getServiceID(), route, false);
			}
		}
	}
	
	public void load(RouteInfo route){
		RouteInfoCache.loadRoute(route.getServiceID(), route, false);
	}
	
	public RouteInfo get(ServiceIdentifier serviceID){
		return RouteInfoCache.getRoute(serviceID);
	}
	
    public void discoverService(ServiceIdentifier serviceID){
    	discoveredServices.add(serviceID);
    }
    public void undiscoveryService(ServiceIdentifier serviceID){
    	discoveredServices.remove(serviceID);
    	RouteInfoCache.evitRoute(serviceID);
    }
    public void registerService(RouteNodeInfo node){
    	registeredNodes.add(node);
    }
    public void unregisterService(ServiceIdentifier serviceID){
    	Iterator<RouteNodeInfo> itr = registeredNodes.iterator();
    	while(itr.hasNext()){
    		RouteNodeInfo node = itr.next();
    		if(node.getServiceID().equals(serviceID)){
    			itr.remove();
    		}
    	}
    }
}