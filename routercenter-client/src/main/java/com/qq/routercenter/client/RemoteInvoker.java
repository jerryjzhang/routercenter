package com.qq.routercenter.client;

import com.qq.routercenter.client.pojo.InvocationContext;
import com.qq.routercenter.client.pojo.ReturnResult;
import com.qq.routercenter.share.service.RouteNodeInfo;

public interface RemoteInvoker {
    public ReturnResult invoke(RouteNodeInfo node, InvocationContext ctx);
}
