package com.qq.routercenter.client.arbite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.Configuration;

import com.qq.routercenter.client.RouteWatcher;
import com.qq.routercenter.client.RouterCenter;
import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.client.pojo.RouteEvent;
import com.qq.routercenter.client.pojo.RouteEvent.EventType;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.enums.RouteStrategyType;

public class BlacklistBasedArbiter extends Arbiter{
	private ConcurrentMap<RouteNodeInfo, TimeBasedCounter> candidates =
			new ConcurrentHashMap<RouteNodeInfo, TimeBasedCounter>();
	private ConcurrentMap<RouteNodeInfo, Long> blacklist =
			new ConcurrentHashMap<RouteNodeInfo, Long>();
	
    public void arbit(RouteInfo route, RouteNodeInfo node) {
    	//if already in the blacklist, return immediately
    	if(blacklist.containsKey(node)){
    		return;
    	}
    	
    	Configuration config = getConfiguration(
    			route.getStrategies().get(RouteStrategyType.ARBITER));
    	long now = System.currentTimeMillis();
    	//skip candidates if possible
    	int effectiveTimes = config.getInt(RouterConfigKeys.ROUTER_ARBIT_EFFECTIVE_TIMES_KEY, 
    			RouterConfigKeys.ROUTER_ARBIT_EFFECTIVE_TIMES_DEFAULT);
    	if(effectiveTimes == 1){
            candidates.remove(node);
    		blacklist.put(node, now);
            RouteWatcher watcher = RouterCenter.getEventHandler(route.getServiceID());
            if(watcher != null){
            	watcher.process(new RouteEvent(EventType.NodeBlacklisted, node));
            }
    		return;
    	}
    	
    	long effectivePeroid = config.getInt(RouterConfigKeys.ROUTER_ARBIT_EFFECTIVE_PERIOD_KEY, 
    			RouterConfigKeys.ROUTER_ARBIT_EFFECTIVE_PERIOD_DEFAULT) * 1000;
    	TimeBasedCounter counter = candidates.putIfAbsent(node,
    			new TimeBasedCounter(now, new AtomicInteger(1)));	
    	//if there was an associated counter in the blacklist, update it
    	if(counter != null){
    		if(now - counter.getTimestamp() <= effectivePeroid){
    			if(counter.getCounter().incrementAndGet() >= effectiveTimes){
    				candidates.remove(node);
    				blacklist.put(node, now);
    				RouteWatcher watcher = RouterCenter.getEventHandler(route.getServiceID());
    	            if(watcher != null){
    	            	watcher.process(new RouteEvent(EventType.NodeBlacklisted, node));
    	            }
    			}
    		}else{ //reset if effective period is over
    			counter.getCounter().set(1);
    			counter.setTimestamp(now);
    		}
    	}
    }
    
    public List<RouteNodeInfo> guard(RouteInfo route, List<RouteNodeInfo> nodes){
    	Configuration config = getConfiguration(route.getStrategies().get(RouteStrategyType.ARBITER));
    	long expirePeroid = config.getInt(RouterConfigKeys.ROUTER_ARBIT_BLACKLIST_PERIOD_KEY, 
    			RouterConfigKeys.ROUTER_ARBIT_BLACKLIST_PERIOD_DEFAULT) * 1000;
    	long now = System.currentTimeMillis();
    	
    	List<RouteNodeInfo> validNodes = new ArrayList<RouteNodeInfo>();
    	
    	for(RouteNodeInfo node : nodes){
    		boolean isValid = true;
    		Long startTime = blacklist.get(node);
        	
        	if(startTime != null){
        		//check if the blacklisted route node expires
        		if(now - startTime >= expirePeroid){
        			blacklist.remove(node);
        			RouteWatcher watcher = RouterCenter.getEventHandler(route.getServiceID());
                    if(watcher != null){
                    	watcher.process(new RouteEvent(EventType.NodeWhitelisted, node));
                    }
        		}else{ 
        			isValid = false;
        		}
        	}
        	
        	if(isValid){
        		validNodes.add(node);
        	}
    	}
    	
    	return validNodes;
    }
    
    private static class TimeBasedCounter {
    	private long timestamp;
    	private AtomicInteger counter;
		public TimeBasedCounter(long timestamp, AtomicInteger counter) {
			super();
			this.timestamp = timestamp;
			this.counter = counter;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public AtomicInteger getCounter() {
			return counter;
		}
    }
}
