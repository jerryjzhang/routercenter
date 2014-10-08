package com.qq.routercenter.service.config;

import java.util.HashMap;
import java.util.Map;

import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.service.dao.impl.RouteDaoImpl;
import com.qq.routercenter.service.dao.impl.RouteNodeDaoImpl;
import com.qq.routercenter.service.dao.impl.RouteRuleDaoImpl;
import com.qq.routercenter.service.dao.impl.RouteStrategyDaoImpl;
import com.qq.routercenter.service.dao.mem.RouteDaoMem;
import com.qq.routercenter.service.dao.mem.RouteNodeDaoMem;
import com.qq.routercenter.service.dao.mem.RouteRuleDaoMem;
import com.qq.routercenter.service.dao.mem.RouteStrategyDaoMem;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.service.dbpool.MysqlDBPool;
import com.qq.routercenter.service.registry.RouteRegistry;
import com.qq.routercenter.service.registry.SimpleRouteRegistry;

public class BeanFactory {
    private static Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();
    
    static {
    	beans.put(RouteRegistry.class, new SimpleRouteRegistry());
    	if("LOCAL".equals(System.getProperty("CONFIG_MODE"))){
    		beans.put(RouteDao.class, new RouteDaoMem());
	        beans.put(RouteNodeDao.class, new RouteNodeDaoMem());
	        beans.put(RouteRuleDao.class, new RouteRuleDaoMem());
	        beans.put(RouteStrategyDao.class, new RouteStrategyDaoMem());
    	}
    	else{
	    	DBPool pool = new MysqlDBPool();
	    	RouteRuleDao ruleDao = new RouteRuleDaoImpl(pool);
	    	RouteStrategyDao strategyDao = new RouteStrategyDaoImpl(pool);
	    	RouteDao routeDao = new RouteDaoImpl(pool, ruleDao, strategyDao);
	        beans.put(RouteRuleDao.class, ruleDao);
	        beans.put(RouteStrategyDao.class, strategyDao);	
	        beans.put(RouteDao.class, routeDao);
	        beans.put(RouteNodeDao.class, new RouteNodeDaoImpl(pool, routeDao));
    	}
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByType(Class<T> clazz){
    	Object obj = beans.get(clazz);
    	if(obj != null){
    		return (T) obj;
    	}
    	
    	return null;
    }
}
