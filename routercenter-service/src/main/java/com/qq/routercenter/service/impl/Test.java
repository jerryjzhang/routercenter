package com.qq.routercenter.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.qq.routercenter.share.service.RouteInfoRequest;
import com.qq.routercenter.share.service.RouteInfoUpdate;
import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouterServiceThrift;

public class Test {
	public static void main(String[] args)throws Exception {
		TTransport transport;

		transport = new TSocket("localhost", 19800);
		transport.open();

		TProtocol protocol = new TBinaryProtocol(transport);
		RouterServiceThrift.Client client = new RouterServiceThrift.Client(
				protocol);
		List<RouteNodeInfo> nodes = new ArrayList<RouteNodeInfo>();
		RouteNodeInfo node = new RouteNodeInfo("localhost", 50030);
		node.setSid("demo.simple-socket-service");
		nodes.add(node);
		client.heartbeat(nodes);
		RouteInfoRequest request = new RouteInfoRequest();
		request.setSid("demo.simple-socket-service");
		RouteInfoUpdate update = client.pullRouteUpdate(request);
		System.out.println(update.getResult());
		Thread.sleep(300 * 1000);
		transport.close();
	}
}
