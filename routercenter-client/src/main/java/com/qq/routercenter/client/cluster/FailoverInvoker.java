package com.qq.routercenter.client.cluster;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.loadbalance.LoadBalancer;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.InvocationException;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

public class FailoverInvoker extends ClusterInvoker{	
	private static final Logger LOG = Logger.getLogger(FailoverInvoker.class);
	
	@Override
	public ReturnResult doInvoke(RouteInfo route, List<RouteNodeInfo> nodes, 
			RemoteInvoker invoker, InvocationContext ctx, LoadBalancer lb, 
			Arbiter arbiter, Configuration config){
		//retry loop, fail over to next route node upon any failure
		//until the given threshold is reached or all nodes have been invoked
		int retries = config.getInt(RouterConfigKeys.ROUTER_FAILOVER_RETRIES_KEY, 
				RouterConfigKeys.ROUTER_FAILOVER_RETRIES_DEFAULT);
		List<RouteNodeInfo> invokedNodes = new ArrayList<RouteNodeInfo>();
		RouteNodeInfo node = null;
		ReturnResult result = null;
		int retried = 1;
		for(;retried<=retries;retried++){
			if(retried == 1){
				node = lb.select(route, nodes);
			}else{
				node = lb.reselect(route, nodes, invokedNodes);
			}
			if(node == null){
				LOG.warn("no route node is selected by the load balancer");
				break;
			}
			invokedNodes.add(node);
			
			if(LOG.isDebugEnabled()){
				LOG.debug("Trying to invoke service=" + node.getServiceURL());
			}
			result = invoker.invoke(node, ctx);
			if(result.getReturnCode() == ReturnCode.CODE_OK){
				return result;
			}
			arbiter.arbit(route, node);
		}
		int errorCode = -999;
		if(result != null){
			errorCode = result.getErrorCode();
		}
		throw new InvocationException("Failed to invoke remote node after " + retried + " retries", errorCode);
	}
}
