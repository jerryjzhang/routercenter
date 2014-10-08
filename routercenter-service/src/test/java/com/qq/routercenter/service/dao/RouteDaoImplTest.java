package com.qq.routercenter.service.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.qq.routercenter.service.dao.impl.RouteDaoImpl;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.service.dbpool.H2DBPool;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.dto.RouteQueryCriteria;
import com.qq.routercenter.share.enums.State;

public class RouteDaoImplTest {
	private DBPool pool = new H2DBPool();
	private RouteDaoImpl routeDao = new RouteDaoImpl(pool, null, null);
	
	@Test
	public void testInsert() {
		Route route = new Route();
		route.setName("!once "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		route.setDesc("The only once route!");
		route.setState(State.DRAFT);
		route.setIncharge("florianfan");
		routeDao.insert(route);
		System.out.println("INSERT success ! id = " + route.getId());
		System.out.println();
	}
	
	@Test
	public void testDelete() {
		int id = Math.abs((new Random().nextInt())%50);
		int ret = routeDao.delete(id);
		if(ret == 0)
			System.out.println("DELETing route with id = " + id + " not exisits!");
		else
			System.out.println("DELETE success ! id = " + id);
		System.out.println();
	}
	
	
	@Test
	public void testUpdate() {
		int id = Math.abs((new Random().nextInt())%50);
		Route route = routeDao.queryById(id);
		if(route != null) {
			route.setIncharge("QQQ");
			routeDao.update(route);
			System.out.print("UPDATE route id = " + route.getId() + " 's name = QQQ");
		}
		System.out.println();
	}
	
	@Test
	public void testUpdateState() {
		int id = Math.abs((new Random().nextInt())%50);
		int ret = routeDao.update(id,State.ACTIVE);
		if(ret == 0)
			System.out.println("UPDATing route with id = " + id + " not exisits!");
		else
			System.out.println("ACTIVE route success ! id = " + id); 
		System.out.println();
	}
	
	@Test
	public void testQueryId() {
		int id = Math.abs((new Random().nextInt())%50);
		Route route = routeDao.queryById(id);
		if(route == null)
			System.out.println("QUERY route with id = " + id + " not exisits!");
		else
			System.out.println("QUERY-ID route with id = " + id + ":\n\t-> " + route.toString());
		System.out.println();
	}
	
	@Test
	public void testQueryName() {
		Route route = routeDao.queryByName("teg.tdw.query");
		if(route != null)
			System.out.println("QUERY-NAME route with name = teg.tdw.query:\n\t-> " + route.toString());
		else
			System.out.println("QUERY-NAME route with name = teg.tdw.query not exists!");
		System.out.println();
	}
	
	@Test
	public void testCriteira() {
		RouteQueryCriteria cr = new RouteQueryCriteria();
		//cr.setId(15);
		cr.setName("once");
		System.out.println("QUERY-CRITERIA \"*once*\":");
		List<Route> routeList = routeDao.queryByCriteria(cr,5,0).getResultSet();
		if(routeList.isEmpty())
			System.out.println("\tNo Records hit !");
		for(int i = 0; i < routeList.size(); i++) {
			System.out.println(i + ": " + routeList.get(i));
		}
		System.out.println();
	}
	
	@Test
	public void testRouteInfo() {
		System.out.println(routeDao.queryRouteInfoByName("yyyy"));
	}

}
