package com.qq.routercenter.service.impl;

import java.util.List;

import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouterServiceThrift;

public class RouterServiceThriftImpl implements RouterServiceThrift.Iface {
	public void heartbeat(List<RouteNodeInfo> nodes) throws org.apache.thrift.TException{
		System.out.println("Received heartbeat " + nodes.size());
	}
}
