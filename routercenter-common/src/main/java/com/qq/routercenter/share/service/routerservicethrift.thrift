namespace java com.qq.routercenter.share.service

typedef i32 int // We can use typedef to get pretty names for the types we are using

struct RouteNodeInfo {
	1: required string host;                  
    2: required int port;             
    3: optional string serviceURL;
    4: optional int weight = 100;             
    5: optional string sid
}
struct RouteRuleInfo {
	1: required string type;
	2: optional string srcProp;
	3: optional string srcOp;
	4: optional string srcValue;
	5: optional string destination
}
struct RouteStrategyInfo {
	1: required string type;
	2: optional string option;
	3: optional map<string,string> config
}
struct RouteInfo {
	1: required string sid;
	2: optional list<RouteNodeInfo> nodes;
	3: optional map<string,RouteStrategyInfo> strategies;
	4: optional list<RouteRuleInfo> rules
}
struct RouteInfoList {
	1: required list<RouteInfo> routes
}
struct RouteInfoRequest {
	1: required string sid;
	2: optional string lastHashCode
}
struct RouteInfoUpdate {
	1: optional bool hasUpate;
	2: optional RouteInfo result;
	3: optional string hasCode
}

service RouterServiceThrift
{
	void heartbeat(1:list<RouteNodeInfo> nodes),
	RouteInfoUpdate pullRouteUpdate(1:RouteInfoRequest request),
	list<RouteInfoUpdate> pullRouteUpdates(1:list<RouteInfoRequest> requests),
}