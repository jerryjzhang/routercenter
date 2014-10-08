package com.qq.routercenter.share.enums;
public enum ServiceError {
	INTERNAL_EXCEPTION("500", "Service internal exception"),
	INVALID_PARAMETER("400", "Invalid parameters"),
	OBJECT_NOT_FOUND("401", "Requested object not found"),
	OBJECT_EXISTED("402", "Requested object already exists");
	
	private String code;
	private String msg;
	
	private ServiceError(String code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}