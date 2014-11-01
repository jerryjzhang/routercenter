package com.qq.routercenter.service.dao;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.qq.routercenter.service.dao.impl.RouteNodeDaoImpl;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.service.dbpool.H2DBPool;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNodeDaoImplTest {
	private DBPool pool = new H2DBPool();
	private RouteNodeDaoImpl routenode = new RouteNodeDaoImpl(pool, null);
	
	@Test
	public void testInsert() {
			
		RouteNode node = new RouteNode();
		node.setHost("hishost");
		node.setPort(19800);
		node.setState(State.DRAFT);
		node.setServiceURL("WWW.Tencent.com");
		node.setType(RouteNodeType.DYNAMIC);
		node.setSet("209");
		node.setWeight(100);
		routenode.insert(node);
		System.out.println("testInsert():*****************************************************" );
		System.out.println("Insert success : routenode with id = " + node.getId());
		System.out.println("\n\n");
	}
	
	@Test
	public void testdelete(){
		
		int id = 7;
		int ret = routenode.delete(id);
		System.out.println("testdelete():*****************************************************");
		if(ret == 0)
			System.out.println("Delete routenode failed!");
		else
			System.out.println("Delete routenode success : routenode with id = "+id);
		System.out.println("\n\n");	
		
	}
	
	@Test
	public void testupdate(){
		
		RouteNode node = new RouteNode();
		if(node != null){
			node.setId(13);
			node.setHost("herhost");
			node.setPort(19800);
			node.setServiceURL("WWW.baidu.com");
			node.setState(State.DRAFT);
			node.setSet("weak");
			node.setType(RouteNodeType.DYNAMIC);
			node.setSet("209");
			node.setWeight(100);
			routenode.update(node);
			System.out.println("testupdate():*****************************************************");
			System.out.println("routenode with id = " + node.getId()+"  update Success !");
			System.out.println("\n\n");
			
		}
		
	}
	
	@Test
	public void testupdatestate(){
		
		int id = 9 ;
		int ret = routenode.update(id, State.ACTIVE);
		System.out.println("testupdatestate():*****************************************************");
		if(ret == 0)
			System.out.println("Update routenode state failed!");
		else
			System.out.println("Update routenode success : node with id = "+id);
		System.out.println("\n\n");
		
	}
	
	@Test
	public void testQueryId() {
		int id = Math.abs((new Random().nextInt())%50);
		RouteNode node = routenode.queryById(id);
		System.out.println("testQueryId():*****************************************************");
		if(node == null)
			System.out.println("query routenode with id = " + id + " not exisits!");
		else
			System.out.println("routenode with id = " + id); 
		System.out.println("\n\n");
	}
	
	@Test
	public void testQueryUrl() {
		System.out.println("testQueryUrl():*****************************************************");
		RouteNode node = routenode.queryByUrl("WWW.Tencent.com");
		System.out.println("routenode with url = WWW.Tencent.com: with id = " + node.getId()); 
		
		System.out.println("\n\n");
	}
	
	@Test
	public void testCriteira() {
		RouteNodeQueryCriteria cr = new RouteNodeQueryCriteria();
		cr.setState(State.DRAFT);
		List<RouteNode> routenodeList = routenode.queryByCriteira(cr,5,0).getResultSet();
		System.out.println("testCriteira():*****************************************************");
		for(int i = 0; i < routenodeList.size(); i++) {
			System.out.println(i + ": " + routenodeList.get(i));
		}
		
		System.out.println("\n\n");
	}
	
	@Test
	public void testQueryByRouteId(){
		
		List<RouteNode> routenodeList = routenode.queryByRouteId(21);
		for(int i = 0; i < routenodeList.size(); i++) {
			System.out.println(i + ": " + routenodeList.get(i));
		}
	}

}
