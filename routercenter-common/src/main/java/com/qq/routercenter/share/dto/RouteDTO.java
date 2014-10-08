package com.qq.routercenter.share.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.qq.routercenter.share.enums.State;

public class RouteDTO implements Serializable{
	private static final long serialVersionUID = -3972983819799926935L;
	
	private int id;
	private String name;
	private String desc;
	private State state;
	private String incharge;
	private Timestamp createTime;
	private Timestamp lastUpdate;
	
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
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getIncharge() {
		return incharge;
	}
	public void setIncharge(String incharge) {
		this.incharge = incharge;
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
}