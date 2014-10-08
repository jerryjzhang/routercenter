package com.qq.routercenter.client.pojo;

public class InvocationException extends RuntimeException {
	private static final long serialVersionUID = -8468413365805539275L;
	
	private int errorCode;
	
	public InvocationException(String msg, int errorCode){
		super(msg);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
