package com.qq.routercenter.share.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RouteNodeInfoList {
	private List<RouteNodeInfo> nodeInfos = new ArrayList<RouteNodeInfo>();

	public List<RouteNodeInfo> getNodeInfos() {
		return nodeInfos;
	}

	public void setNodeInfos(List<RouteNodeInfo> nodeInfos) {
		this.nodeInfos = nodeInfos;
	}

	public void add(RouteNodeInfo node) {
		nodeInfos.add(node);
	}

	public void clear() {
		nodeInfos.clear();
	}

	public void addAll(Collection<RouteNodeInfo> nodes) {
		nodeInfos.addAll(nodes);
	}
}
