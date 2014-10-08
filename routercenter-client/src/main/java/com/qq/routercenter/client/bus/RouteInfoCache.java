package com.qq.routercenter.client.bus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.share.domain.ServiceIdentifier;
import com.qq.routercenter.share.dto.RouteInfo;

/* Cache used to record router information. */
public class RouteInfoCache {
	private static final Logger LOG = Logger.getLogger(RouteInfoCache.class);
			
    private static final ConcurrentMap<ServiceIdentifier, RouteInfo> allRoutes = 
    	new ConcurrentHashMap<ServiceIdentifier, RouteInfo>(); 
    
    private static final File CACHE_BASE_DIR;
	private static final String CACHE_FILE_SUFFIX = ".xml";
	private static boolean CACHE_SWITCH_ON = true;
    static{
    	String customCachePath = System.getProperty(
    			RouterConfigKeys.ROUTER_CACHE_BASE_DIR_KEY);
    	CACHE_BASE_DIR = new File(customCachePath != null 
    			? customCachePath : RouterConfigKeys.ROUTER_CACHE_BASE_DIR_DEFAULT);
    	if(!CACHE_BASE_DIR.exists()){
    		try{
    			CACHE_BASE_DIR.mkdir();
    		}catch(SecurityException e){
    			CACHE_SWITCH_ON = false;
    			LOG.warn("Failed to create cache directory " + CACHE_BASE_DIR.getAbsolutePath() 
    					+", the caching feature will be disabled");
    		}
    	}
    }
    
    /* Load service information for a specified service. */
    public static void loadRoute(ServiceIdentifier serviceID, RouteInfo route, boolean writeFile) {
    	allRoutes.put(serviceID, route);
    	if(writeFile){
    		writeCacheFile(serviceID, route);
    	}
    }
    
    public static void loadRoute(ServiceIdentifier serviceID, RouteInfo route) {
    	loadRoute(serviceID, route, true); //default write local cache file
    }
    
    public static RouteInfo getRoute(ServiceIdentifier serviceID) {
    	return getRoute(serviceID, false);
    }
    
    public static RouteInfo getRoute(ServiceIdentifier serviceID, boolean useFile) {
    	return useFile == false ? allRoutes.get(serviceID) : readCacheFile(serviceID);
    }
    
    public static void evitRoute(ServiceIdentifier serviceID){
    	allRoutes.remove(serviceID);
    }
	
	private static void writeCacheFile(ServiceIdentifier serviceID, RouteInfo route){
		if(!CACHE_SWITCH_ON){ return; }
		
		String cacheFilePath = null;
		try{
			cacheFilePath = buildCacheFilePath(serviceID.toFullSID());
			OutputStream os = new FileOutputStream(cacheFilePath);
			RouteFileManager.write(route, os);
		}catch(RouteFileException e){
			LOG.error("Failed to write service cache file=" + cacheFilePath + " :" + e.getMessage());
		}catch(FileNotFoundException e){
			LOG.error("Failed to create service cache file=" + cacheFilePath + " :" + e.getMessage());
		}
	}
	
	private static RouteInfo readCacheFile(ServiceIdentifier serviceID){
		if(!CACHE_SWITCH_ON){ return null; }
		
    	String cacheFilePath = buildCacheFilePath(serviceID.toFullSID());
		
		try{
			InputStream is = new FileInputStream(cacheFilePath);
			RouteInfo route = RouteFileManager.read(RouteInfo.class, is);
			RouteInfoCache.loadRoute(serviceID, route);
			return route;
		}catch(RouteFileException e){
			LOG.error("Failed to get service configs from local cache file for service=" + serviceID 
					+ "\nFailure reason:" + e.getMessage());
			return null;
		}catch(FileNotFoundException e){
			LOG.error("Failed to get service configs from local cache file for service=" + serviceID 
					+ "\nFailure reason:" + e.getMessage());
			return null;
		}
	}
	
	public static String buildCacheFilePath(String serviceID){
		return CACHE_BASE_DIR + File.separator + 
				serviceID + CACHE_FILE_SUFFIX;
	}
}
