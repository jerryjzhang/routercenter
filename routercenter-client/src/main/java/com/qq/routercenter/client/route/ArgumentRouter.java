package com.qq.routercenter.client.route;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.domain.RouteParam;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteRuleInfo;
import com.qq.routercenter.share.enums.RouteRuleOp;

public class ArgumentRouter extends Router{
		private static final Logger LOG = Logger.getLogger(ArgumentRouter.class);
		
		public List<RouteNodeInfo> route(RouteRuleInfo rule, List<RouteNodeInfo> nodes, InvocationContext ctx){
			if(ctx == null || !(ctx instanceof RpcInvocationContext)
					|| rule.getSrcProp() == null 
					|| "".equals(rule.getSrcProp())){ 
				return nodes; 
			}
			
			RpcInvocationContext rpcCtx = (RpcInvocationContext)ctx;
			// Check if the invoked method has paramater annotated with RouteParam
			Method invokedMethod = rpcCtx.getMethodObj();
			Annotation[][] annos = invokedMethod.getParameterAnnotations();
			RouteParam paramAnno = null;
			int paramIndex = 0;
			for(int i=0;i<annos.length;i++){
				for(Annotation ann : annos[i]){
					if(ann instanceof RouteParam){
						paramIndex = i;
						paramAnno = (RouteParam)ann;
						break;
					}
				}
			}
			
			if(paramAnno != null){
				// if param name doesn't match specified rule prop, just skip
				if(rule.getSrcValue() != null &&
						paramAnno.value().equals(rule.getSrcProp())){
					Class<?> paramClass = invokedMethod.getParameterTypes()[paramIndex];
					if(String.class.equals(paramClass)){
						String paramValue = (String)rpcCtx.getMethodArgs()[paramIndex];
						boolean isMatch = paramValue.equals(rule.getSrcValue());
						String[] dstHostPatterns = rule.getDestination().split(ROUTE_RULE_DELIMITER);
						if (isMatch && rule.getSrcOp() == RouteRuleOp.EQUAL) {
							nodes = findMatchingNodes(dstHostPatterns, nodes);
						} else if (!isMatch && rule.getSrcOp() == RouteRuleOp.INEQUAL) {
							nodes = findMatchingNodes(dstHostPatterns, nodes);
						}
					}else if(Integer.class.equals(paramClass) || int.class.equals(paramClass)){
						String[] dstHostPatterns = rule.getDestination().split(ROUTE_RULE_DELIMITER);
						if(rule.getSrcOp() == RouteRuleOp.BETWEEN){
							String [] values = rule.getSrcValue().split("-");
							if(values.length != 2){
								LOG.error("The format of the prop value is invalid for between condition");
								return nodes;
							}
							int min = Integer.valueOf(values[0]);
							int max = Integer.valueOf(values[1]);
							int paramValue = (Integer)rpcCtx.getMethodArgs()[paramIndex];						
							if(paramValue >= min && paramValue <= max){
								nodes = findMatchingNodes(dstHostPatterns, nodes);
							}
							return nodes;
						}
						
						Integer paramValue = (Integer)rpcCtx.getMethodArgs()[paramIndex];
						boolean isMatch = paramValue.equals(Integer.valueOf(rule.getSrcValue()));
						if (isMatch && rule.getSrcOp() == RouteRuleOp.EQUAL) {
							nodes = findMatchingNodes(dstHostPatterns, nodes);
						} else if (!isMatch && rule.getSrcOp() == RouteRuleOp.INEQUAL) {
							nodes = findMatchingNodes(dstHostPatterns, nodes);
						}
					}
				}
			}
			
			return nodes;
		}
}
