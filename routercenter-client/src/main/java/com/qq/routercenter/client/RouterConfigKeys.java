package com.qq.routercenter.client;

/** 
 * This class contains constants for configuration keys used
 * in routercenter-client.
 *
 */
public interface RouterConfigKeys {
	/* system-level configs which could be set with system properties */
	public static final String ROUTER_SERVICE_ADDRESS_KEY = "router.service.address";
	public static final String ROUTER_SITE_PATH_KEY = "router.sites.filepath";
	public static final String ROUTER_SITE_PATH_DEFAULT = "router-site.xml";
	public static final String ROUTER_CACHE_BASE_DIR_KEY = "router.cache.dir";
	public static final String ROUTER_CACHE_BASE_DIR_DEFAULT = ".routecache";
	public static final String ROUTER_DISCOVERY_INTERVAL_KEY = "router.discovery.interval";
	public static final int    ROUTER_DISCOVERY_INTERVAL_DEFAULT = 3 * 60; // in seconds
	public static final String ROUTER_DISCOVERY_INTIALDELAY_KEY = "router.discovery.intialDelay";
	public static final int    ROUTER_DISCOVERY_INTIALDELAY_DEFAULT = 60; // in seconds
	public static final String ROUTER_HEARTBEAT_INTERVAL_KEY = "router.heartbeat.interval";
	public static final int    ROUTER_HEARTBEAT_INTERVAL_DEFAULT = 60; // in seconds
	public static final String ROUTER_HEARTBEAT_INTIALDELAY_KEY = "router.heartbeat.intialDelay";
	public static final int    ROUTER_HEARTBEAT_INTIALDELAY_DEFAULT = 5; // in seconds
	
	/* route-level configs which could be set with 'router-site.xml' */
	public static final String ROUTER_FAILOVER_RETRIES_KEY = "router.failover.retries";
	public static final int    ROUTER_FAILOVER_RETRIES_DEFAULT = 4;
	public final static String ROUTER_ARBIT_EFFECTIVE_PERIOD_KEY = "router.arbite.effective.period";
	public final static int    ROUTER_ARBIT_EFFECTIVE_PERIOD_DEFAULT = 5 * 60; // in seconds
	public final static String ROUTER_ARBIT_EFFECTIVE_TIMES_KEY  = "router.arbite.effective.times";
	public final static int    ROUTER_ARBIT_EFFECTIVE_TIMES_DEFAULT  = 2; 
	public final static String ROUTER_ARBIT_BLACKLIST_PERIOD_KEY  = "router.arbite.blacklist.period";
	public final static int    ROUTER_ARBIT_BLACKLIST_PERIOD_DEFAULT  = 60; // in seconds
}
