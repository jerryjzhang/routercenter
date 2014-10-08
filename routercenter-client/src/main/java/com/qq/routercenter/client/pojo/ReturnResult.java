package com.qq.routercenter.client.pojo;


public class ReturnResult {
    private final ReturnCode returnCode;
    private final Object returnValue;
    private final int errorCode;
    
    public ReturnResult(ReturnCode returnCode, Object returnValue, int errorCode){
    	this.returnCode = returnCode;
    	this.returnValue = returnValue;
    	this.errorCode = errorCode;
    }
    
    public ReturnResult(ReturnCode returnCode, Object returnValue) {
    	this(returnCode, returnValue, -1);
    }
    
    public ReturnCode getReturnCode() {
    	return returnCode;
    }
    
    public Object getReturnValue() {
    	return returnValue;
    }

	public int getErrorCode() {
		return errorCode;
	}
}
