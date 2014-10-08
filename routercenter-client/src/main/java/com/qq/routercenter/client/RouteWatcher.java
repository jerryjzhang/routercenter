package com.qq.routercenter.client;

import com.qq.routercenter.client.pojo.RouteEvent;

public interface RouteWatcher {
	void process(RouteEvent e);
}
