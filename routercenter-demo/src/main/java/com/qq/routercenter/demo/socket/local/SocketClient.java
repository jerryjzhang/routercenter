package com.qq.routercenter.demo.socket.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.qq.routercenter.client.RemoteInvoker;
import com.qq.routercenter.client.RouterCenter;
import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnCode;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.dto.RouteNodeInfo;

public class SocketClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        RouterCenter routerCenter = new RouterCenter(Thread.currentThread().getContextClassLoader().
    			getResourceAsStream("com/qq/routercenter/demo/socket/local/socket-site.xml"));
    	routerCenter.discoverService("demo.simple-socket-service", new SocketRemoteInvoker());
    	
    	int callID = 1;
        while(true){
    		String msg = (String)routerCenter.invokeService("demo.simple-socket-service");
    		System.out.println("callID=" + callID++ + " Received: " + msg);
	        Thread.sleep(2000);
    	}
    }
    
    static class SocketRemoteInvoker implements RemoteInvoker {
    	public ReturnResult invoke(RouteNodeInfo node, InvocationContext ctx){
    		try{
	    		Socket s = new Socket(node.getHost(), node.getPort());
		        BufferedReader input =
		            new BufferedReader(new InputStreamReader(s.getInputStream()));
		        return new ReturnResult(ReturnCode.CODE_OK, input.readLine());
    		}catch(IOException e){
    			e.printStackTrace();
    			return new ReturnResult(ReturnCode.CODE_EXCEPTION, null);
    		}
    	}
    }
}
