package com.qq.routercenter.client.route;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.apache.log4j.Logger;

import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.domain.RouteParam;
import com.qq.routercenter.share.service.RouteNodeInfo;
import com.qq.routercenter.share.service.RouteRuleInfo;

public class ArgumentCombinationRouter extends Router{
	private static final Logger LOG = Logger.getLogger(ArgumentCombinationRouter.class);
	
	public List<RouteNodeInfo> route(RouteRuleInfo rule, List<RouteNodeInfo> nodes, InvocationContext ctx){
		if(ctx == null || !(ctx instanceof RpcInvocationContext)
				|| rule.getSrcProp() == null 
				|| "".equals(rule.getSrcProp())){ 
			return nodes; 
		}
		
		RpcInvocationContext rpcCtx = (RpcInvocationContext)ctx;
		// Check if the method has all annotated parameters that are 
		// specified in the RouteRule. If not, just skip this rule.
		Map<String, RouteParam> paramAnnots = new HashMap<String, RouteParam>();
		Map<String, Integer> paramIndexies =new HashMap<String, Integer>();
		Method invokedMethod = rpcCtx.getMethodObj();
		Annotation[][] annos = invokedMethod.getParameterAnnotations();
		for(int i=0;i<annos.length;i++){
			for(Annotation ann : annos[i]){
				if(ann instanceof RouteParam){
					paramAnnots.put(((RouteParam) ann).value(), (RouteParam)ann);
					paramIndexies.put(((RouteParam) ann).value(), i);
				}
			}
		}
		String [] params = rule.getSrcProp().split(",");
		for(int i=0;i<params.length;i++){
			params[i] = params[i].trim();
			if(paramAnnots.get(params[i]) == null){
				return nodes;
			}
			// If parameter type is not supported, just skip this rule
			Class<?> paramClass = invokedMethod.getParameterTypes()[paramIndexies.get(params[i])];
			if(!String.class.equals(paramClass) && !Integer.class.equals(paramClass)){
				return nodes;
			}
		}
		// Replace param number with actual param names. 
		// The param names should be in the form like #{a}, which
		// is compliant with JEval spec
		Evaluator evaluator = new Evaluator();
		for(int i=1;i<=params.length;i++){
			String hehe = rule.getSrcValue().replaceAll("\\$"+i, "#{"+params[i-1]+"}");
			rule.setSrcValue(hehe);
			int paramIndex = paramIndexies.get(params[i-1]);
			Class<?> paramClass = invokedMethod.getParameterTypes()[paramIndex];
			String paramStr = null;
			if(String.class.equals(paramClass)){
				paramStr = "'" + (String)rpcCtx.getMethodArgs()[paramIndex] + "'";
			}else if(Integer.class.equals(paramClass)){
				paramStr = ((Integer)rpcCtx.getMethodArgs()[paramIndex]).toString();
			}
			evaluator.putVariable(params[i-1], paramStr);
		}
		
		try{
			if(evaluator.getBooleanResult(rule.getSrcValue())){
				String[] dstHostPatterns = rule.getDestination().split(ROUTE_RULE_DELIMITER);
				nodes = findMatchingNodes(dstHostPatterns, nodes);
			}
		}catch(EvaluationException e){
			LOG.error("Exception occurred during route rule evaluation");
		}
		
		return nodes;
	}
}
