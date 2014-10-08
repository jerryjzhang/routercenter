package com.qq.routercenter.demo.socket.local;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(Integer.valueOf(args[0]));
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
