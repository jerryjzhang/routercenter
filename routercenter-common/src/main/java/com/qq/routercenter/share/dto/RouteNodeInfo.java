package com.qq.routercenter.share.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.ServiceIdentifier;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteNodeInfo {
	private String host;
	private int port;
	private String serviceURL;
	private int weight = 100; //default to 100
	private ServiceIdentifier serviceID;
	
	public RouteNodeInfo(){
	}
	
	public static RouteNodeInfo valueof(RouteNode node){
		RouteNodeInfo nodeInfo = new RouteNodeInfo();
		nodeInfo.host = node.getHost();
		nodeInfo.port = node.getPort();
		nodeInfo.serviceURL = node.getServiceURL();
		
		return nodeInfo;
	}
	
	public RouteNodeInfo(ServiceIdentifier serviceID, 
			String host, int port){
		this(serviceID, host, port, host+":"+port);
	}
	
	public RouteNodeInfo(ServiceIdentifier serviceID, 
			String host, int port, String serviceURL){
		this(serviceID, host, port, serviceURL, 100);
	}
	
	public RouteNodeInfo(ServiceIdentifier serviceID, 
			String host, int port, String serviceURL,int weight){
		this.serviceID = serviceID;
		this.host = host;
		this.port = port;
		this.serviceURL = serviceURL;
		this.weight = weight;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServiceURL() {
		return serviceURL != null ? serviceURL : host+":"+port;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public ServiceIdentifier getServiceID() {
		return serviceID;
	}

	public void setServiceID(ServiceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	@Override
	public String toString() {
		return "RouteNodeInfo [host=" + host + ", port=" + port
				+ ", serviceURL=" + serviceURL + ", weight=" + weight
				+ ", serviceID=" + serviceID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
		result = prime * result
				+ ((serviceURL == null) ? 0 : serviceURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteNodeInfo other = (RouteNodeInfo) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (port != other.port)
			return false;
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!serviceID.equals(other.serviceID))
			return false;
		if (serviceURL == null) {
			if (other.serviceURL != null)
				return false;
		} else if (!serviceURL.equals(other.serviceURL))
			return false;
		return true;
	}
}
