package com.qq.routercenter.share.domain;

/** 
 * This class contains constants for configuration keys used
 * in routercenter-client.
 *
 */
public interface RouteStrategyConfigKeys {
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
