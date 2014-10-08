package com.qq.routercenter.share.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RouteInfoList implements Serializable{
	private static final long serialVersionUID = 2221180309097496582L;
	
	@XmlElement(name="routeInfo")
	private List<RouteInfo> routes = new ArrayList<RouteInfo>();
	
	public List<RouteInfo> getRoutes() {
		return routes;
	}

	public void setRoutes(List<RouteInfo> routes) {
		this.routes = routes;
	}

	public void add(RouteInfo node) {
		routes.add(node);
	}

	public void clear() {
		routes.clear();
	}

	public void addAll(Collection<RouteInfo> nodes) {
		routes.addAll(nodes);
	}
}
