package com.qq.routercenter.client;

import java.util.HashMap;
import java.util.Map;

import com.qq.routercenter.client.arbite.Arbiter;
import com.qq.routercenter.client.arbite.BlacklistBasedArbiter;
import com.qq.routercenter.client.cluster.ClusterInvoker;
import com.qq.routercenter.client.cluster.FailfastInvoker;
import com.qq.routercenter.client.cluster.FailoverInvoker;
import com.qq.routercenter.client.cluster.FailsafeInvoker;
import com.qq.routercenter.client.loadbalance.LoadBalancer;
import com.qq.routercenter.client.loadbalance.RandomLoadBalancer;
import com.qq.routercenter.client.loadbalance.RoundRobinLoadBalancer;
import com.qq.routercenter.client.loadbalance.WeightedRandomLoadBalancer;
import com.qq.routercenter.client.loadbalance.WeightedRoundRobinLoadBalancer;
import com.qq.routercenter.client.route.ArgumentCombinationRouter;
import com.qq.routercenter.client.route.ArgumentRouter;
import com.qq.routercenter.client.route.HostRouter;
import com.qq.routercenter.client.route.Router;
import com.qq.routercenter.share.enums.ArbiterStrategy;
import com.qq.routercenter.share.enums.FaultToleranceStrategy;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;
import com.qq.routercenter.share.enums.RouteRuleType;

public class BeanFactory {
	private final static Map<LoadBalanceStrategy, LoadBalancer> loadBalancers =
			new HashMap<LoadBalanceStrategy, LoadBalancer>();
	private final static Map<FaultToleranceStrategy, ClusterInvoker> ftInvokers =
			new HashMap<FaultToleranceStrategy, ClusterInvoker>();
	private final static Map<ArbiterStrategy, Arbiter> arbiters =
			new HashMap<ArbiterStrategy, Arbiter>();
	private final static Map<RouteRuleType, Router> routers =
			new HashMap<RouteRuleType, Router>();
	
	static {
		loadBalancers.put(LoadBalanceStrategy.RANDOM, new RandomLoadBalancer());
		loadBalancers.put(LoadBalanceStrategy.ROUND_ROBIN, new RoundRobinLoadBalancer());
		loadBalancers.put(LoadBalanceStrategy.WEIGHT_RANDOM, new WeightedRandomLoadBalancer());
		loadBalancers.put(LoadBalanceStrategy.WEIGHT_ROUNDROBIN, new WeightedRoundRobinLoadBalancer());
		ftInvokers.put(FaultToleranceStrategy.FAILOVER, new FailoverInvoker());
		ftInvokers.put(FaultToleranceStrategy.FAILFAST, new FailfastInvoker());
		ftInvokers.put(FaultToleranceStrategy.FAILSAFE, new FailsafeInvoker());
		arbiters.put(ArbiterStrategy.BLACKLIST, new BlacklistBasedArbiter());
		routers.put(RouteRuleType.HOST, new HostRouter());
		routers.put(RouteRuleType.METHOD_ARGS, new ArgumentRouter());
		routers.put(RouteRuleType.METHOD_ARGS_COMB, new ArgumentCombinationRouter());
	}
	
	public static LoadBalancer getLoadBalancer(LoadBalanceStrategy strategy){
		return strategy == null ? loadBalancers.get(LoadBalanceStrategy.ROUND_ROBIN)
				: loadBalancers.get(strategy);
	}
	
	public static ClusterInvoker getClusterInvoker(FaultToleranceStrategy strategy){
		return strategy == null ? ftInvokers.get(FaultToleranceStrategy.FAILOVER)
				: ftInvokers.get(strategy);
	}
	
	public static Arbiter getArbiter(ArbiterStrategy strategy){
		return strategy == null ? arbiters.get(ArbiterStrategy.BLACKLIST)
				: arbiters.get(strategy);
	}
	
	public static Router getRouter(RouteRuleType type){
		return type != null ? routers.get(type) : null;
	}
}
