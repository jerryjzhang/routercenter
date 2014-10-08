package com.qq.routercenter.client.cluster;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.loadbalance.LoadBalancer;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;

public class FailsafeInvoker extends ClusterInvoker{
	private static final Logger LOG = Logger.getLogger(FailsafeInvoker.class);
			
	public ReturnResult doInvoke(RouteInfo route, List<RouteNodeInfo> nodes, 
			RemoteInvoker invoker, InvocationContext ctx, LoadBalancer lb, 
			Arbiter arbiter, Configuration config){
		RouteNodeInfo node = lb.select(route, nodes);
		ReturnResult result = invoker.invoke(node, ctx);
		if(result.getReturnCode() != ReturnCode.CODE_OK){
			if(LOG.isDebugEnabled()){
				LOG.debug("Ignore failure: " + result.getReturnValue());
			}
			result = new ReturnResult(ReturnCode.CODE_OK, null, result.getErrorCode());
			arbiter.arbit(route, node);
		}
		
		return result;
	}
}
