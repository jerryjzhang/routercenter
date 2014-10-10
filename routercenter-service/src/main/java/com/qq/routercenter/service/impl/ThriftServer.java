package com.qq.routercenter.service.impl;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.qq.routercenter.share.service.RouterServiceThrift;

public class ThriftServer {
	public static RouterServiceThriftImpl handler;

	public static RouterServiceThrift.Processor processor;

	public static void main(String[] args) {
		try {
			handler = new RouterServiceThriftImpl();
			processor = new RouterServiceThrift.Processor(handler);

			TServerTransport serverTransport = new TServerSocket(19800);
			TServer server = new TThreadPoolServer(
					new Args(serverTransport).processor(processor));

			System.out.println("Starting the simple server...");
			server.serve();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
