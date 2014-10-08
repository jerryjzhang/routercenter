package com.qq.routercenter.share.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.qq.routercenter.share.enums.ArbiterStrategy;
import com.qq.routercenter.share.enums.FaultToleranceStrategy;
import com.qq.routercenter.share.enums.LoadBalanceStrategy;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class Route {
	private int id;
	private String name;
	private String desc;
	private State state;
	private String incharge;
	private Timestamp createTime;
	private Timestamp lastUpdate;

	private List<RouteNode> nodes = new ArrayList<RouteNode>();

	private List<RouteRule> rules = new ArrayList<RouteRule>();

	private List<RouteStrategy> strategies = new ArrayList<RouteStrategy>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getIncharge() {
		return incharge;
	}

	public void setIncharge(String incharge) {
		this.incharge = incharge;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<RouteNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<RouteNode> nodes) {
		this.nodes = nodes;
	}

	public List<RouteRule> getRules() {
		if(rules.isEmpty()){
			RouteRule defaultRule = new RouteRule();
			defaultRule.setRoute(this);
			defaultRule.setState(State.ACTIVE);
			defaultRule.setType(RouteRuleType.HOST);
			defaultRule.setSrcProp("IP");
			defaultRule.setSrcOp(RouteRuleOp.EQUAL);
			defaultRule.setSrcValue("*");
			defaultRule.setDestination("*");
			rules.add(defaultRule);
		}
		return rules;
	}

	public void setRules(List<RouteRule> rules) {
		this.rules = rules;
	}

	public List<RouteStrategy> getStrategies() {
		if(strategies.isEmpty()){
			RouteStrategy defaultStrategy = new RouteStrategy();
			defaultStrategy.setRoute(this);
			defaultStrategy.setType(RouteStrategyType.LOAD_BALANCE);
			defaultStrategy.setOption(LoadBalanceStrategy.ROUND_ROBIN.toString());
			defaultStrategy.setState(State.ACTIVE);
			strategies.add(defaultStrategy);
			
			defaultStrategy = new RouteStrategy();
			defaultStrategy.setRoute(this);
			defaultStrategy.setType(RouteStrategyType.FAULT_TOLERANCE);
			defaultStrategy.setOption(FaultToleranceStrategy.FAILOVER.toString());
			defaultStrategy.setConfig(RouteStrategyConfigKeys.ROUTER_FAILOVER_RETRIES_KEY + "="
					+ RouteStrategyConfigKeys.ROUTER_FAILOVER_RETRIES_DEFAULT);
			defaultStrategy.setState(State.ACTIVE);
			strategies.add(defaultStrategy);
			
			defaultStrategy = new RouteStrategy();
			defaultStrategy.setRoute(this);
			defaultStrategy.setType(RouteStrategyType.ARBITER);
			defaultStrategy.setOption(ArbiterStrategy.BLACKLIST.toString());
			StringBuilder str = new StringBuilder();
			str.append(RouteStrategyConfigKeys.ROUTER_ARBIT_EFFECTIVE_PERIOD_KEY + "=" 
					+ RouteStrategyConfigKeys.ROUTER_ARBIT_EFFECTIVE_PERIOD_DEFAULT);
			str.append("\n");
			str.append(RouteStrategyConfigKeys.ROUTER_ARBIT_EFFECTIVE_TIMES_KEY + "=" 
					+ RouteStrategyConfigKeys.ROUTER_ARBIT_EFFECTIVE_TIMES_DEFAULT);
			str.append("\n");
			str.append(RouteStrategyConfigKeys.ROUTER_ARBIT_BLACKLIST_PERIOD_KEY + "=" 
					+ RouteStrategyConfigKeys.ROUTER_ARBIT_BLACKLIST_PERIOD_DEFAULT);
			defaultStrategy.setConfig(str.toString());
			defaultStrategy.setState(State.ACTIVE);
			strategies.add(defaultStrategy);
		}
		return strategies;
	}

	public void setStrategies(List<RouteStrategy> strategies) {
		this.strategies = strategies;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", name=" + name + ", desc=" + desc
				+ ", state=" + state + ", incharge=" + incharge
				+ ", createTime=" + createTime + ", lastUpdate=" + lastUpdate
				+ ", nodes=" + nodes + ", rules=" + rules + ", strategies="
				+ strategies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
