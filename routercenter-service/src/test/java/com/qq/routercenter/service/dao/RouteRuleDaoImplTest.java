package com.qq.routercenter.service.dao;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dao.impl.RouteDaoImpl;
import com.qq.routercenter.service.dao.impl.RouteRuleDaoImpl;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.service.dbpool.H2DBPool;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.service.RouteRuleQueryCriteria;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.State;

public class RouteRuleDaoImplTest {
	private DBPool pool = new H2DBPool();
	private RouteDao routeDao = new RouteDaoImpl(pool, null, null);
	private RouteRuleDao routeRuleDao = new RouteRuleDaoImpl(pool);

	@Test
	public void testInsert() {
		Route route = routeDao.queryById(47);
		// route.setId(1001); //Test foreign key constrain
		RouteRule rule = new RouteRule();
		rule.setRoute(route);
		rule.setState(State.DRAFT);
		rule.setType(RouteRuleType.HOST);
		rule.setSrcProp("IP");
		rule.setSrcOp(RouteRuleOp.EQUAL);
		rule.setSrcValue("127.0.0.1");
		rule.setDestination("192.168.0.1");
		int ruleId = routeRuleDao.insert(rule);
		Assert.assertNotSame(ruleId, 0);
	}

	@Test
	public void testDelete() {
		routeRuleDao.delete(1);
		RouteRule rule = routeRuleDao.queryById(1);
		assertEquals(rule, null);
	}

	@Test
	public void testUpdate() {
		RouteRule rule = routeRuleDao.queryById(2);
		if (rule != null) {
			rule.setSrcOp(RouteRuleOp.INEQUAL);
			routeRuleDao.update(rule);
			rule = routeRuleDao.queryById(2);
			assertEquals(rule.getSrcOp(), RouteRuleOp.INEQUAL);
		}
	}

	@Test
	public void testUpdateState() {
		routeRuleDao.update(2, State.FREEZE);
		RouteRule rule = routeRuleDao.queryById(2);
		assertEquals(rule.getState(), State.FREEZE);
	}

	@Test
	public void testQueryByRouteId() {
		List<RouteRule> routeRuleList = routeRuleDao.queryByRouteId(47);
		System.out.println(routeRuleList.size());
		for (int i = 0; i < routeRuleList.size(); i++) {
			assertEquals(routeRuleList.get(i).getRoute().getId(), 47);
			System.out.println(routeRuleList.get(i).getId() + " | "
					+ routeRuleList.get(i).getRoute().getName() + " | "
					+ routeRuleList.get(i).getCreateTime() + " | "
					+ routeRuleList.get(i).getLastUpdate() + " | "
					+ routeRuleList.get(i).getRoute().getCreateTime() + " | "
					+ routeRuleList.get(i).getRoute().getLastUpdate());
		}
	}

	@Test
	public void testCriteria() {
		RouteRuleQueryCriteria criteria = new RouteRuleQueryCriteria();
		criteria.setSearchKey("128");
		criteria.setRouteName("yy");
		List<RouteRule> routeRuleList = routeRuleDao.queryByCriteira(criteria,10, 0).getResultSet();
		for (int i = 0; i < routeRuleList.size(); i++) {
			RouteRule rule = routeRuleList.get(i);
			System.out.println(rule.getRoute().getName() + " | "
					+ rule.getSrcProp() + " | " + rule.getSrcValue() + " | "
					+ rule.getDestination());
			assertEquals(rule.getRoute().getName().contains("yy"), true);
			assertEquals(rule.getSrcValue().contains("128")
					|| rule.getDestination().contains("128"), true);
		}
	}

}
