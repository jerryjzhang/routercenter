package com.qq.routercenter.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qq.routercenter.client.cluster.ClusterInvoker;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.client.pojo.RpcInvocationContext;
import com.qq.routercenter.share.dto.RouteInfo;
import com.qq.routercenter.share.dto.RouteNodeInfo;
import com.qq.routercenter.share.dto.RouteStrategyInfo;
import com.qq.routercenter.share.enums.FaultToleranceStrategy;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.RouterServices;
import com.qq.routercenter.share.service.RouterService;

public class RouterCenterService {
	private RouteInfo routeInfo;
	private RouterService proxy;
	
	public RouterCenterService(String connString){
		ConnectStringParser parser = new ConnectStringParser(connString);
		routeInfo = parser.getRouteInfo();
		proxy = (RouterService) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { RouterService.class }, new RouterServiceProxy());
	}
	
	public RouterService getService(){
		return proxy;
	}
	
	static class ConnectStringParser {
		private RouteInfo routeInfo = new RouteInfo();
		
		public ConnectStringParser(String connString){
			routeInfo.setServiceID(RouterServices.ROUTER_SERVICE.getSid());
			String [] addrs = connString.split(",");
			for(String addr : addrs){
				RouteNodeInfo node = new RouteNodeInfo();
				node.setServiceURL("http://" + addr + RouterServices.ROUTER_SERVICE.getUri());
				routeInfo.getNodes().add(node);
			}
			RouteStrategyInfo strategy = new RouteStrategyInfo();
			strategy.setType(RouteStrategyType.LOAD_BALANCE);
			strategy.setOption(LoadBalanceStrategy.ROUND_ROBIN.toString());
			routeInfo.getStrategies().put(strategy.getType(), strategy);
			strategy = new RouteStrategyInfo();
			strategy.setType(RouteStrategyType.FAULT_TOLERANCE);
			strategy.setOption(FaultToleranceStrategy.FAILOVER.toString());
			routeInfo.getStrategies().put(strategy.getType(), strategy);
		}

		public RouteInfo getRouteInfo() {
			return routeInfo;
		}
	}
	
	class RouterServiceProxy implements InvocationHandler {
		private RemoteInvoker invoker = new HttpRpcInvoker();
		
		@Override
	    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
			RpcInvocationContext ctx = RpcInvocationContext.Builder.newBuilder()
					.methodName(method.getName())
					.methodObj(method)
					.methodArgs(args).build();
			ClusterInvoker ftInvoker = BeanFactory.getClusterInvoker(
					ClusterInvoker.getStrategy(routeInfo.getStrategies().get(RouteStrategyType.FAULT_TOLERANCE)));
			ReturnResult result = ftInvoker.invoke(routeInfo, routeInfo.getNodes(), invoker, ctx);
			return result.getReturnValue();
		}
	}
	
	private static class HttpRpcInvoker implements RemoteInvoker {
		private static final ObjectMapper mapper = new ObjectMapper();
		private static final Client client = ClientBuilder.newClient()
				.register(JacksonFeature.class);

		public ReturnResult invoke(RouteNodeInfo node, InvocationContext ctx) {
			if (!(ctx instanceof RpcInvocationContext)) {
				throw new IllegalArgumentException(
						"InvocationContext is not expected");
			}
			RpcInvocationContext rpcCtx = (RpcInvocationContext) ctx;
			try {
				String url = !node.getServiceURL().startsWith("tas") ? node
						.getServiceURL() : node.getServiceURL().replaceFirst(
						"tas", "http");
				RouterHttpResponse response = invokeHTTPPostSync(url, rpcCtx
						.getMethodObj().getName(), rpcCtx.getMethodArgs());
				if ("0".equals(response.getRetCode())) {
					Object obj = null;
					if (!void.class.equals(rpcCtx.getMethodObj()
							.getReturnType())) {
						obj = mapper.convertValue(response.getRetObj(), rpcCtx
								.getMethodObj().getReturnType());
					}
					return new ReturnResult(ReturnCode.CODE_OK, obj, 
							Integer.valueOf(response.getRetCode()));
				} else {
					return new ReturnResult(ReturnCode.CODE_FAIL, response.getRetMsg(), 
							Integer.valueOf(response.getRetCode()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ReturnResult(ReturnCode.CODE_EXCEPTION,
						e.getMessage());
			}
		}

		public static RouterHttpResponse invokeHTTPPostSync(String serviceURL,
				String op, Object[] obj) throws IOException {
			String params = mapper.writeValueAsString(obj);

			WebTarget target = client.target(serviceURL);

			Form form = new Form();
			form.param("m", op);
			form.param("p", params);

			RouterHttpResponse response = target.request(
					MediaType.APPLICATION_JSON_TYPE).post(
					Entity.entity(form,
							MediaType.APPLICATION_FORM_URLENCODED_TYPE),
					RouterHttpResponse.class);

			return response;
		}
	}

	public static class RouterHttpResponse {
		private String retCode;
		private String retMsg;
		private Object retObj;

		public String getRetCode() {
			return retCode;
		}

		public String getRetMsg() {
			return retMsg;
		}

		public Object getRetObj() {
			return retObj;
		}

		public void setRetCode(String retCode) {
			this.retCode = retCode;
		}

		public void setRetMsg(String retMsg) {
			this.retMsg = retMsg;
		}

		public void setRetObj(Object retObj) {
			this.retObj = retObj;
		}
	}
}
