package com.qq.routercenter.client.route;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleOp;

public class HostRouter extends Router {
	private static final Logger LOG = Logger.getLogger(HostRouter.class);
	static{
		System.setProperty("java.net.preferIPv4Stack" , "true");
	}
	
	public List<RouteNodeInfo> route(RouteRuleInfo rule,
			List<RouteNodeInfo> nodes, InvocationContext ctx) {
		Set<String> localHostIPs = getLocalHostAddresses();
		if(LOG.isDebugEnabled()){
			LOG.debug("Source ips are: " + localHostIPs);
		}
		if (localHostIPs.isEmpty())
			return nodes;
		String[] srcHostPatterns = rule.getSrcValue().split(
				ROUTE_RULE_DELIMITER);
		String[] dstHostPatterns = rule.getDestination().split(
				ROUTE_RULE_DELIMITER);
		boolean isMatch = false;
		for(String ip : localHostIPs){
			if(hasMatchingPattern(srcHostPatterns, ip)){
				isMatch = true;
				if(LOG.isDebugEnabled()){
					LOG.debug("Source IP " + ip + " matches route rule");
				}
				break;
			}
		}
		if (isMatch && rule.getSrcOp() == RouteRuleOp.EQUAL) {
			nodes = findMatchingNodes(dstHostPatterns, nodes);
		} else if (!isMatch && rule.getSrcOp() == RouteRuleOp.INEQUAL) {
			nodes = findMatchingNodes(dstHostPatterns, nodes);
		}

		return nodes;
	}

	static Set<String> getLocalHostAddresses() {
		Set<String> ipSet = new HashSet<String>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress addr = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				Enumeration<InetAddress> addrs = ni.getInetAddresses();
				while(addrs.hasMoreElements()){
					addr = (InetAddress) addrs.nextElement();
					if(!(addr instanceof Inet6Address) && !addr.isLoopbackAddress()){
						ipSet.add(addr.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			LOG.error("Unknown host: " + e.getMessage());
		} 
		
		return ipSet;
	}
}
