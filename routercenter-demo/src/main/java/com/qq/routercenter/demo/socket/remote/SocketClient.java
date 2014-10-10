package com.qq.routercenter.demo.socket.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.RouterCenter;
import com.qq.routercenter.client.RouterConfigKeys;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.service.RouteNodeInfo;

/**
 * Sample Socket Client that discovers servers using routercenter 
 * and sends request. 
 * 
 * @author jerryjzhang
 *
 */
public class SocketClient {
	static{
    	// the discovery interval of route info (in seconds) from the routercenter service 
    	// is configurable by clients with system property
		System.setProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTIALDELAY_KEY, "5");
		System.setProperty(RouterConfigKeys.ROUTER_DISCOVERY_INTERVAL_KEY, "5");
	}
	
    public static void main(String[] args) throws IOException, InterruptedException {
        RouterCenter routerCenter = new RouterCenter("localhost:19800");
    	routerCenter.discoverService("demo.simple-socket-service", new SocketRemoteInvoker());
    	
    	int callID = 1;
        while(true){
    		String msg = (String)routerCenter.invokeService("demo.simple-socket-service");
    		System.out.println("callID=" + callID++ + " Received: " + msg);
	        Thread.sleep(1000);
    	}
    }
    
    /**
     * Clients need to implement RemoteInvoker to run 
     * actual invocations.
     *
     */
    static class SocketRemoteInvoker implements RemoteInvoker {
    	public ReturnResult invoke(RouteNodeInfo node, InvocationContext ctx){
    		Socket s = null;
    		try{
	    		s = new Socket(node.getHost(), node.getPort());
		        BufferedReader input =
		            new BufferedReader(new InputStreamReader(s.getInputStream()));
		        return new ReturnResult(ReturnCode.CODE_OK, input.readLine());
    		}catch(IOException e){
    			e.printStackTrace();
    			// if ReturnCode is not 'CODE_OK', routercenter-client might
    			// do failover to other RouteNodeInfo and retry current invocation.
    			return new ReturnResult(ReturnCode.CODE_EXCEPTION, null);
    		}finally{
    			if(s != null){
    				try{
    					s.close();
    				}catch(Exception e){ }
    			}
    		}
    	}
    }
}
