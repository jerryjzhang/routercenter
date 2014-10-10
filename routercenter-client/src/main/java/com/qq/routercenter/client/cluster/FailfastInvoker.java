package com.qq.routercenter.client.cluster;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.loadbalance.LoadBalancer;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.InvocationException;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.service.RouteInfo;
import com.qq.routercenter.share.service.RouteNodeInfo;

public class FailfastInvoker extends ClusterInvoker{
	public ReturnResult doInvoke(RouteInfo route, List<RouteNodeInfo> nodes, 
			RemoteInvoker invoker, InvocationContext ctx, LoadBalancer lb, 
			Arbiter arbiter, Configuration config){
		RouteNodeInfo node = lb.select(route, nodes);
		ReturnResult result = invoker.invoke(node, ctx);
		if(result.getReturnCode() == ReturnCode.CODE_OK){
			return result;
		}
		arbiter.arbit(route, node);
		throw new InvocationException("Failed to invoke remote node after 1 retries", result.getErrorCode());
	}
}
