package com.qq.routercenter.share.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteRuleInfo {
	private RouteRuleType type;
	private String srcProp;
	private RouteRuleOp srcOp;
	private String srcValue;
	private String destination;
	
	public RouteRuleInfo(){
	}
	
	public static RouteRuleInfo valueOf(RouteRule rule){
		RouteRuleInfo ruleInfo = new RouteRuleInfo();
		ruleInfo.destination = rule.getDestination();
		ruleInfo.srcOp = rule.getSrcOp();
		ruleInfo.srcProp = rule.getSrcProp();
		ruleInfo.srcValue = rule.getSrcValue();
		ruleInfo.type = rule.getType();
		
		return ruleInfo;
	}
	
	public RouteRuleType getType() {
		return type;
	}
	public void setType(RouteRuleType type) {
		this.type = type;
	}
	public String getSrcProp() {
		return srcProp;
	}
	public void setSrcProp(String srcProp) {
		this.srcProp = srcProp;
	}
	public RouteRuleOp getSrcOp() {
		return srcOp;
	}
	public void setSrcOp(RouteRuleOp srcOp) {
		this.srcOp = srcOp;
	}
	public String getSrcValue() {
		return srcValue;
	}
	public void setSrcValue(String srcValue) {
		this.srcValue = srcValue;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((srcOp == null) ? 0 : srcOp.hashCode());
		result = prime * result + ((srcProp == null) ? 0 : srcProp.hashCode());
		result = prime * result
				+ ((srcValue == null) ? 0 : srcValue.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RouteRuleInfo other = (RouteRuleInfo) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (srcOp != other.srcOp)
			return false;
		if (srcProp == null) {
			if (other.srcProp != null)
				return false;
		} else if (!srcProp.equals(other.srcProp))
			return false;
		if (srcValue == null) {
			if (other.srcValue != null)
				return false;
		} else if (!srcValue.equals(other.srcValue))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
