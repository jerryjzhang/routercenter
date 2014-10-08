package com.qq.routercenter.share.domain;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class RouteStrategy {
	private int id;
	private RouteStrategyType type;
	private String option;
	private String config;
	private State state;
	private Route route;
	private Timestamp createTime;
	private Timestamp lastUpdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RouteStrategyType getType() {
		return type;
	}

	public void setType(RouteStrategyType type) {
		this.type = type;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Map<String, String> buildConfigMap() {
		Map<String, String> configMap = new HashMap<String, String>();
		if (config != null) {
			String[] configs = config.split("\n");
			for (String c : configs) {
				String[] entry = c.split("=");
				if (entry.length == 2) {
					configMap.put(entry[0], entry[1]);
				}
			}
		}
		return configMap;
	}

	@Override
	public String toString() {
		return "RouteStrategy [id=" + id + ", type=" + type + ", option="
				+ option + ", config=" + config + ", state=" + state
				+ ", createTime=" + createTime + ", lastUpdate=" + lastUpdate
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((option == null) ? 0 : option.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RouteStrategy other = (RouteStrategy) obj;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (id != other.id)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (option == null) {
			if (other.option != null)
				return false;
		} else if (!option.equals(other.option))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
