namespace java com.qq.routercenter.share.service

typedef i32 int // We can use typedef to get pretty names for the types we are using

struct RouteNodeInfo {
	1: required string host;                  
    2: required int port;             
    3: optional string serviceURL;
    4: optional int weight = 100;             
    5: optional string sid
}

typedef list<RouteNodeInfo> RouteNodeList 

service RouterServiceThrift
{
	void heartbeat(1:RouteNodeList nodes),
}