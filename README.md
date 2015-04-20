软负载(RouterCenter)是一个分布式集群路由中间件，为分布式系统提供动态寻址、负载均衡、集群容灾及路由分发等功能。作为旁路系统，软负载不参与客户端和服务端之间的实际通讯，因而可通用于大多数的分布式应用。

## 为什么需要软负载

分布式系统通常将资源(计算或存储)以partition与replication两种形式分散在机器集群上，这就要求分布式系统处理以下几个通用问题：

- 动态寻址：当服务集群扩容/缩容时，客户端如何动态地更新服务的地址列表。

- 负载分配：如何为服务集群中的机器分配流量。如果是对等集群，流量需要均匀分配；而对于非对等集群，流量需要根据一定的比例分配。

- 失败切换：当某个服务节点出现异常导致客户端操作失败时，如何自动切换到其他正常节点。

- 升级灰度：当服务升级版本做灰度时，如何将灰度客户引流到新版本的服务节点、普通客户引流到老版本的服务节点。

软负载将类似的通用问题抽象出来，实现为通用模块，在降低分布式应用开发成本的同时，统一化运维管理流程及界面。

## 软负载能提供什么

自动化服务注册/发现，动态获取集群节点的更新。
根据服务标识(ServiceIdentifier,简称SID)，解析服务地址URL。

- 多种负载均衡策略，如简单轮询/随机、基于权重的轮询。

- 多种集群容灾策略，如失败切换、快速失败、失败安全、广播。

- 多种路由分发规则，如基于机器IP路由、基于方法参数路由。

- 多种使用模式，如托管模式、客户端托管模式、非托管模式。

- 运维管理平台界面。

## 工作原理

软负载有两大功能模块——动态寻址和路由选路。动态寻址根据用户指定的SID，动态获取到URL列表的更新；路由选路则负责从URL列表里选择合适的一个，返回给用户发起连接。

如下图所示，软负载的工作流是一个三角模型，中心点是管理维护(SID, URLs)映射关系的软负载中心，两个端点分别是发起动态注册的服务端和实施动态发现的客户端。软负载给服务端/客户端提供一个软负载SDK来实现动态注册/发现，服务端/客户端也可以遵照协议自行实现。

![Architecture][1]

  [1]: https://raw.githubusercontent.com/tragicjun/tragicjun.github.io/master/images/RouterCenterOverview.png
  
## 动态寻址

如下图所示是动态寻址的工作流，描述了服务端URL如何动态传递到客户端。软负载SDK提供两个callback接口——HeartbeatBuilder和RemoteInvoker，分别作为服务端和客户端与软负载SDK的接入点。前者要求实现者返回(SID,URL)的映射，后者要求实现者返回调用结果。

![Routing][2]

  [2]: https://raw.githubusercontent.com/tragicjun/tragicjun.github.io/master/images/RouterCenterRouting.png
  
软负载SDK的工作流程如下：

1. HeartbeatReporter定期调用callback接口HeartbeatBuilder，来获取服务端特定的(SID,URL)映射，接着将映射信息上报给软负载中心。

2. RouteInfoBus定期从软负载中心拉取指定SID的URL列表更新，并缓存在本地。

3. RouterCenter接收客户端的调用请求，从本地缓存获取URL列表，再经过路由选路模块，最终将寻址到的URL传递给callback接口RemoteInvoker。

## 路由选路

下图将路由选路模块展开，描述了URL列表是如何经过层层筛选过滤的。

![RoutingPaths][3]

  [3]: https://raw.githubusercontent.com/tragicjun/tragicjun.github.io/master/images/RouterCenterRoutingPaths.png
  
总的来说，主要有以下四层选路：

- Router：路由分发层，根据路由规则挑选匹配的URL。用户可为每个SID配置多条路由规则，而每条路由规则可定义匹配条件和路由目标。如下表所示的路由规则表，为同一个SID配置了3条不同类型的规则，分别解释如下：

  - 机器IP：当客户端IP匹配'192.168.1.*'时，将其路由到IP匹配'10.215.132.13'的服务端。
  
  - 方法参数：当客户端调用参数QQ取值在10000到20000之间时，将其路由到IP匹配'10.215.129.101' 或'10.215.129.102'的服务端。
  
  - 方法参数组合：当客户端调用参数组合(QQ,ID)匹配表达式'QQ >= 1000 && ID > 1 && ID < 5'时，将其路由到IP匹配'10.136.172.*'的服务端。

![RoutingTable][4]

  [4]: https://raw.githubusercontent.com/tragicjun/tragicjun.github.io/master/images/RouterCenterRoutingTable.png
  
- Arbiter：失败仲裁层，根据之前的仲裁结果，将被判定无效的URL过滤掉。每次调用失败都会由ClusterInvoker向Arbiter申请仲裁，仲裁结果会影响下次的选路。

- ClusterInvoker：集群容错层，利用集群来处理调用失败，有如下几种处理策略：

  - 失败切换：自动重试其它服务器，可配置重试次数。常用于幂等调用。

  - 快速失败：只发起一次调用，失败立即报错。常用于非幂等调用。

  - 失败安全：直接忽略失败。常用于日志记录等非关键调用。

  - 广播：广播调用所有节点，逐个调用，任意一台成功即成功。

- LoadBalancer：负载分配层，根据策略从选取URL，有如下两种策略：

  - 简单轮询/随机：均匀分配流量到路由节点。

  - 基于权重轮询/随机：可为每个路由节点配置权重，根据节点之间的权重比例来分配流量。

## 如何使用软负载

### 使用模式

软负载提供三种使用接口——软负载中心API、软负载SDK及软负载管理平台。不同的使用场景会用到不同的使用接口或接口组合，可以总结为以下几种使用模式：

- 全托管模式：对于内部系统，方便对服务端和客户端同时进行改造，嵌入软负载SDK，从而获得动态注册/发现、负载均衡、集群容灾、路由分发等全部功能。

- 客户端托管模式：对于外部服务，服务端无法嵌入软负载SDK，这时可以通过管理平台来人工静态注册服务URL。

- 服务端托管模式：对于外部客户，客户端无法嵌入软负载SDK，这时可以让客户端调用软负载中心API来静态获取服务URL列表。

- 非托管模式：某些使用场景只需要简单寻址(根据SID获取服务URL列表)，不需要动态注册/发现、路由选路等功能。这种场景下，服务端和客户端都不需要嵌入软负载SDK，而是使用管理平台静态注册、软负载中心API静态发现。

下表总结了以上讨论的几种使用模式：
    
![WorkMode][5]

  [5]: https://raw.githubusercontent.com/tragicjun/tragicjun.github.io/master/images/RouterCenterWorkModes.png
  
## 应用示例

下面用一个简单的socket通讯作示例，演示如何使用软负载SDK。

### 服务端

```
public static void main(String[] args) throws IOException {
    //获取软负载SDK入口实例，输入软负载中心的地址列表
    RouterCenter routerCenter = RouterCenter.getInstance("localhost:19800,localhost:19900");
    //向软负载SDK注册服务节点，绑定节点(SID, host,port)，其中SID为服务标识，必须以.号分隔
    routerCenter.registerService("demo.simple-socket-service", "localhost", 50030);
    
    try {
        ServerSocket listener = new ServerSocket(50030);
        while (true) {
            Socket socket = listener.accept();
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("I'm localhost:"+ 50030);
            } finally {
                socket.close();
            }
        }
    }
    finally {
        listener.close();
    }
}
```

### 客户端

客户端通过代理模式来发起服务调用，因而首先需要继承软负载SDK的ServiceProxy抽象类，并实现invoke方法来发起远程调用，软负载SDK本身不参与远程调用：

```
static class SocketServiceProxy extends ServiceProxy {
    public SocketServiceProxy(RouterCenter routerCenter, String sid){
        super(routerCenter, sid);
    }
    
    public Object invoke(RouteNodeInfo node, InvocationContext ctx){
        Socket s = null;
        try{
            s = new Socket(node.getHost(), node.getPort());
            BufferedReader input =
                new BufferedReader(new InputStreamReader(s.getInputStream()));
            return input.readLine();
        }catch(IOException e){
            //抛出InvocationException表示调用异常，可能触发软负载SDK执行失败切换，即换一个服务节点重试
            throw new InvocationException(e);
        }finally{
            if(s != null){
                try{ 
                    s.close();
                }catch(Exception e){ }
            }
        }
    }
}
```

通过软负载Driver程序初始化SDK、实例化ServiceProxy、发起调用：

```
public static void main(String[] args) throws IOException, InterruptedException {
    //获取软负载SDK入口实例，输入软负载中心的地址列表
    RouterCenter routerCenter = RouterCenter.getInstance("localhost:19800,localhost:19900");
    //实例化ServiceProxy，输入对应service的服务标识SID
    ServiceProxy simpleSocketService = new SocketServiceProxy(routerCenter, "demo.simple-socket-service");
    
    int callID = 1;
    while(true){
        //通过ServiceProxy对服务发起调用
        String msg = (String)simpleSocketService.invokeService();;
        System.out.println("callID=" + callID++ + " Received: " + msg);
        Thread.sleep(1000);
    }
}
```
