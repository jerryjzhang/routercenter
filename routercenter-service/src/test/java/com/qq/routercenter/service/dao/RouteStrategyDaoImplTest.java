package com.qq.routercenter.service.dao;

import java.util.List;

import org.junit.Test;

import com.qq.routercenter.service.dao.impl.RouteStrategyDaoImpl;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.RouteStrategyQueryCriteria;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.service.dbpool.H2DBPool;

public class RouteStrategyDaoImplTest {
	private DBPool pool = new H2DBPool();
	private RouteStrategyDaoImpl routestrategy = new RouteStrategyDaoImpl(pool);
	
	@Test
	public void testinsert() {
		RouteStrategy node = new RouteStrategy();
		node.setState(State.DRAFT);
		node.setType(RouteStrategyType.ARBITER);
		node.setConfig("I am ok");
		node.setOption("Hello dogs");
		routestrategy.insert(node);
		System.out.println("testInsert():" );
		System.out.println("Insert success : routestrategy with id = " + node.getId());
		
	}
	
	@Test
	public void testdelete() {
		
		int id = 2;
		int ret = routestrategy.delete(id);
		System.out.println("testdelete():");
		if(ret == 0)
			System.out.println("Delete routestrategy failed!");
		else
			System.out.println("Delete routestrategy success : routenode with id = "+id);
	}

	@Test
	public void testupdatenode(){
		
		RouteStrategy node = new RouteStrategy();
		if(node != null){
			node.setId(13);
			
			node.setState(State.ILLEGAL);
			node.setOption("hehehe");
			node.setType(RouteStrategyType.ARBITER);
			node.setConfig("a Shit");			
			routestrategy.update(node);
			System.out.println("testupdatebynode():");
			System.out.println("routestrategy with id = " + node.getId()+"  update Success !");
					
		}
	}
	
	@Test
	public void testupdatestate(){
		
		int id = 8 ;
		int ret = routestrategy.update(id, State.ACTIVE);
		System.out.println("testupdatestate():");
		if(ret == 0)
			System.out.println("Update routenode state failed!");
		else
			System.out.println("Update routenode success : node with id = "+id);
	}
	
	@Test
	public void testQueryId() {
		int id = 21;
		RouteStrategy node = routestrategy.queryById(id);
		System.out.println("testQueryId():");
		if(node == null)
			System.out.println("query RouteStrategy with id = " + id + " not exisits!");
		else
			System.out.println("RouteStrategy with id = " + id); 
		
	}
	
	@Test
	public void testQueryByRouteId(){
		
		List<RouteStrategy> routestrategyList = routestrategy.queryByRouteId(21);
		System.out.println("QueryByRoutetID():");
		for(int i = 0; i < routestrategyList.size(); i++) {
			System.out.println(i + ": " + routestrategyList.get(i).toString());
		}
	}
	
	@Test
	public void testCriteira() {
		RouteStrategyQueryCriteria cr = new RouteStrategyQueryCriteria();
		cr.setState(State.DRAFT);
		List<RouteStrategy> routestrategyList = routestrategy.queryByCriteira(cr,5,0).getResultSet();
		System.out.println("testCriteira():");
		for(int i = 0; i < routestrategyList.size(); i++) {
			System.out.println(i + ": " + routestrategyList.get(i));
		}

	}
}
