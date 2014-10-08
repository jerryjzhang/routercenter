package com.qq.routercenter.demo.socket.remote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.qq.routercenter.client.RouterCenter;

/**
 * Sample Socket server that sends heartbeat to routercenter
 * so that it can be discovered by clients. 
 * 
 * @author jerryjzhang
 *
 */
public class SocketServer {
	public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(50030);
        
		RouterCenter routerCenter = new RouterCenter("localhost:19800");
		routerCenter.registerService("demo.simple-socket-service", "localhost", 50030);
		
        try {
        	System.out.println("Listening on port " + args[0]);
            while (true) {
                Socket socket = listener.accept();
                try {
                    PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                    out.println("I'm localhost:"+args[0]);
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }
}
