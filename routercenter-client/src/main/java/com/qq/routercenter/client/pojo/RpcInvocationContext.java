package com.qq.routercenter.client.pojo;

import java.lang.reflect.Method;

public class RpcInvocationContext extends InvocationContext {
	protected String methodName;
	protected Object[] methodArgs;
	protected Method methodObj;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getMethodArgs() {
		return methodArgs;
	}

	public void setMethodArgs(Object[] methodArgs) {
		this.methodArgs = methodArgs;
	}

	public Method getMethodObj() {
		return methodObj;
	}

	public void setMethodObj(Method methodObj) {
		this.methodObj = methodObj;
	}

	public static class Builder {
		private String methodName;
		private Object[] methodArgs;
		private Method methodObj;
		
		private Builder(){
		}
		
		public static Builder newBuilder(){
			return new Builder();
		}

		public Builder methodName(String methodName) {
			this.methodName = methodName;
			return this;
		}

		public Builder methodArgs(Object[] methodArgs) {
			this.methodArgs = methodArgs;
			return this;
		}

		public Builder methodObj(Method methodObj) {
			this.methodObj = methodObj;
			return this;
		}

		public RpcInvocationContext build() {
			return new RpcInvocationContext(this);
		}
	}

	protected RpcInvocationContext(Builder builder) {
		this.methodName = builder.methodName;
		this.methodArgs = builder.methodArgs;
		this.methodObj = builder.methodObj;
	}
}
