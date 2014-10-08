package com.qq.routercenter.share.domain;

import java.io.Serializable;

public class ServiceIdentifier implements Serializable{
	private static final long serialVersionUID = -1819065000961593157L;
	
	private String business;
	private String service;
	
	public ServiceIdentifier(){
	}
	
	public ServiceIdentifier(String business, String service) {
		super();
		this.business = business != null ? business : "unknown";
		this.service = service;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business != null ? business : "unknown";
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((business == null) ? 0 : business.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		ServiceIdentifier other = (ServiceIdentifier) obj;
		if (business == null) {
			if (other.business != null)
				return false;
		} else if (!business.equals(other.business))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServiceIdentifier [business=" + business + ", service="
				+ service + "]";
	}
	
	public static ServiceIdentifier valueOf(String fullSID){
		String [] items = fullSID.split("\\.");
		if(items.length == 2){
			return new ServiceIdentifier(items[0], items[1]);
		}
		
		return new ServiceIdentifier("unknown", fullSID);
	}
	
	public String toFullSID(){
		return business + "." + service;
	}
}