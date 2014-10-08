package com.qq.routercenter.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteQueryCriteria;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class RouteDaoImpl implements RouteDao {
	private static final Logger LOG = Logger.getLogger(RouteDaoImpl.class);
	
	private DBPool pool = null ;
	QueryRunner queryRunner = new QueryRunner();
	
    private final RouteRuleDao routeRuleDao;
    private final RouteStrategyDao routeStrategyDao;
	
	private static final Map<String,String> FIELD_TO_COLUMN = new HashMap<String,String>();
	static{
		FIELD_TO_COLUMN.put("id", "route_id");
		FIELD_TO_COLUMN.put("name", "route_name");
		FIELD_TO_COLUMN.put("desc", "route_desc");
		FIELD_TO_COLUMN.put("state", "route_state");
		FIELD_TO_COLUMN.put("incharge", "route_incharge");
		FIELD_TO_COLUMN.put("createTime", "create_time");
		FIELD_TO_COLUMN.put("lastUpdate", "last_update");
	}
	
	public RouteDaoImpl(DBPool pool, RouteRuleDao routeRuleDao, RouteStrategyDao routeStrategyDao) {
		this.pool=pool;
		this.routeRuleDao = routeRuleDao;
		this.routeStrategyDao = routeStrategyDao;
	}
	
	private final String INSERT_ROUTE = 
		"INSERT INTO route(route_name, route_desc, route_state, route_incharge, create_time, last_update) "
		+ "VALUES(?, ?, ?, ?, now(), now())";
	@Override
	public int insert(Route route) {
		Connection conn = null;
		PreparedStatement ps = null;
		int id =0;
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(INSERT_ROUTE,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, route.getName());
			ps.setString(2,route.getDesc());
			ps.setString(3,route.getState().toString());
			ps.setString(4,route.getIncharge());
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			
			if(rs.next()) {
				id = rs.getInt(1);
				route.setId(id);
			}
		} catch (SQLException e) {
			LOG.error("Failed to insert route:" + route + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(ps);
		}	
		
		for(RouteRule rule : route.getRules()){
			routeRuleDao.insert(rule);
		}
		for(RouteStrategy strategy : route.getStrategies()){
			routeStrategyDao.insert(strategy);
		}
		
		return id;
	}
	
	private final String DELETE_ROUTE = "delete from route where route_id = ?";
	@Override
	public int delete(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ROUTE,id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	private final String UPDATE_ROUTE = "UPDATE route SET route_name = ?, route_desc = ?, route_state = ?, "
			+ "route_incharge = ?, last_update = now() "
			+ "WHERE route_id = ?";
	@Override
	public int update(Route route) {
		Connection conn = null;
		int id = route.getId();
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_ROUTE,route.getName(),route.getDesc(),
					route.getState().toString(),route.getIncharge(),id);
		} catch (SQLException e) {
			LOG.error("Failed to update route:" + route + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	private final String UPDATE_STATE = "UPDATE route SET route_state = ?, last_update = now() "
			+ "WHERE route_id = ?";
	@Override
	public int update(int id, State state) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_STATE,state.toString(),id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	@Override
	public QueryResult<Route> queryByCriteria(RouteQueryCriteria criteria, int limit,
			int offset) {
		
		int id = criteria.getId();
		String name = criteria.getName();
		State state = criteria.getState();
		String incharge = criteria.getIncharge();
		String searchKey = criteria.getSearchKey();
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<Object> colValues = new ArrayList<Object>();
		if(id > 0) {
			colNames.add("route_id =");
			colValues.add(id);
		}
		if(name != null) {
			colNames.add("route_name LIKE");
			colValues.add("%" + name + "%");
		}
		if(state != null) {
			colNames.add("route_state =");
			colValues.add(state.toString());
		}
		if(incharge != null) {
			colNames.add("route_incharge LIKE");
			colValues.add("%" + incharge + "%");
		}
		
		//鍏抽敭瀛�
		if(searchKey != null) {
			colNames.add("concat(route_name, route_state) LIKE");
			colValues.add("%"+ searchKey + "%");
		}
		
		String sql = "select SQL_CALC_FOUND_ROWS * from route where 1 = 1";
		for(int i = 0; i < colNames.size(); i++) {
			sql += " AND " + colNames.get(i) + " ?";
		}
		
		//鍒嗙粍
		if(criteria.getOrderField() != null && criteria.getOrderType() != null){
			sql += " order by " + FIELD_TO_COLUMN.get(criteria.getOrderField()) + " " + criteria.getOrderType();
		}else{
			sql +=" order by route_name asc";
		}
		
		if(limit > 0){
			sql += " limit " + limit + " offset " + offset ;
		}
		
		String sqlCount = "select FOUND_ROWS()";
		Connection conn = null;
		
		List<Route> routeList= new ArrayList<Route>();
		int count = 0;
		try {
			conn = pool.getConnection();
			routeList = queryRunner.query(conn, sql, new RouteListHandler(), colValues.toArray());
			count = queryRunner.query(conn, sqlCount, new ScalarHandler<Long>()).intValue();
		} catch (SQLException e) {
			LOG.error("Failed to query route with criteria:" + criteria + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
		
		QueryResult<Route> queryResultRoute = new QueryResult<Route>(routeList,count);
		return queryResultRoute;
	}
	
	private final String QUERY_ID = "SELECT * FROM route WHERE route_id = ?";
	@Override
	public Route queryById(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_ID, new RouteHandler(), id);
		} catch (SQLException e) {
			LOG.error("Failed to query routeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	private final String QUERY_NAME = "SELECT * FROM route WHERE route_name = ?";
	@Override
	public Route queryByName(String name) {
		Connection conn = null;		
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_NAME, new RouteHandler(),name);
		} catch (SQLException e) {
			LOG.error("Failed to query routeName:" + name + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	private final String QUERY_ROUTE_INFO = "SELECT * FROM route left join route_node on route.route_id = route_node.route_id left join "
			+ "route_rule on route.route_id = route_rule.route_id left join route_strategy on route.route_id = route_strategy.route_id "
			+ "WHERE route_name = ?";
	/**
	* @Title: queryRouteInfoByName 
	* @Description: Query all the Route information by route_name, including RouteNode, RouteRule 
	* 				and RouteStrategy corresponding with Route
	* @param name is route_name field n route table
	* @return Route object with RouteNode list, RouteRule list and RouteStrategy list in it
	* @see com.qq.routercenter.service.dao.RouteDao#queryRouteInfoByName(java.lang.String) 
	*/
	@Override
	public Route queryRouteInfoByName(String name) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_ROUTE_INFO, new RouteInfoHandler(),name);
		} catch (SQLException e) {
			LOG.error("Failed to query routeName:" + name + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	/** 
	* @ClassName: RouteListHandler 
	* @Description: This result set handler parses the query results of route table into a 
	* 				ArrayList of Route object
	* @author: florianfan 
	* @date: 2014楠烇拷閺堬拷閺冿拷娑撳宕�:09:55
	*/
	private class RouteListHandler implements ResultSetHandler<List<Route>> {

		@Override
		public List<Route> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<Route> routeList = new LinkedList<Route>(); //use LinkedList to preserve order
			while(rs.next()) {
				Route route = new Route();
				route.setId(rs.getInt("route_id"));
				route.setName(rs.getString("route_name"));
				route.setDesc(rs.getString("route_desc"));
				route.setState(State.getServiceState((rs.getString("route_state"))));
				route.setIncharge(rs.getString("route_incharge"));
				route.setCreateTime(rs.getTimestamp("create_time"));
				route.setLastUpdate(rs.getTimestamp("last_update"));
				routeList.add(route);
			}
			return routeList;
		}
		
	}

	/** 
	* @ClassName: RouteHandler 
	* @Description: This handler wraps RouteListHandler and return the unique first element of Route list 
	* @author: florianfan 
	* @date: 2014楠烇拷閺堬拷閺冿拷娑撳宕�:13:22
	*/
	private class RouteHandler implements ResultSetHandler<Route> {

		@Override
		public Route handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<Route> routeList = new RouteListHandler().handle(rs);
			if(routeList.size() > 0) {
				return routeList.get(0);
			}
			else
				return null;
		}
		
	}
	
	
	/** 
	* @ClassName: RouteInfoHandler 
	* @Description: Result set handler of queryRouteInfoByName, which return a Route object within RouteNode list, RouteRule list 
	* 				and RouteStrategy list. The raw result set is generated by a join sql-statement, and RouteInfoHandler parses it 
	* 				to be a Route object
	* @author: florianfan 
	* @date: 2014楠烇拷閺堬拷閺冿拷娑撳宕�:05:01
	*/
	private class RouteInfoHandler implements ResultSetHandler<Route> {

		@SuppressWarnings("rawtypes")
		@Override
		public Route handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			Route route = null;
			
			//map id to concrete record
			HashMap<Integer, RouteNode> nodes = new HashMap<Integer, RouteNode>();
			HashMap<Integer, RouteRule> rules = new HashMap<Integer, RouteRule>();
			HashMap<Integer, RouteStrategy> strategies = new HashMap<Integer, RouteStrategy>();
			
			while(rs.next()) {
				//Initialize route @ the 1st time
				if(route == null){
					route = new Route();
					route.setId(rs.getInt("route.route_id"));
					route.setName(rs.getString("route_name"));
					route.setDesc(rs.getString("route_desc"));
					route.setState(State.getServiceState((rs.getString("route_state"))));
					route.setIncharge(rs.getString("route_incharge"));
					route.setCreateTime(rs.getTimestamp("route.create_time"));
					route.setLastUpdate(rs.getTimestamp("route.last_update"));
				}
				
				//get id value from the result set table
				int nodeId = rs.getInt("node_id");
				int ruleId = rs.getInt("rule_id");
				int strategyId = rs.getInt("strategy_id");
				
				//create node when its id not exists
				if(nodeId !=0 && nodes.get(nodeId) == null) {
					RouteNode node = new RouteNode();
					node.setId(nodeId);
					node.setRoute(route);
					node.setType(RouteNodeType.valueOf(rs.getString("node_type")));
					node.setSet(rs.getString("node_set"));
					node.setHost(rs.getString("node_host"));
					node.setPort(rs.getInt("node_port"));
					node.setServiceURL(rs.getString("service_url"));
					node.setState(State.getServiceState((rs.getString("node_state"))));
					node.setWeight(rs.getInt("node_weight"));
					node.setCreateTime(rs.getTimestamp("route_rule.create_time"));
					node.setLastUpdate(rs.getTimestamp("route_rule.last_update"));
					nodes.put(nodeId,node);
				}
				
				//create rule when its id not exists
				if(ruleId !=0 && rules.get(ruleId) == null) {
					RouteRule rule = new RouteRule();
					rule.setId(ruleId);
					rule.setRoute(route);
					rule.setState(State.getServiceState((rs.getString("rule_state"))));
					rule.setType(RouteRuleType.valueOf(rs.getString("rule_type")));
					rule.setSrcProp(rs.getString("source_prop"));
					rule.setSrcOp(RouteRuleOp.valueOf(rs.getString("source_op")));
					rule.setSrcValue(rs.getString("source_value"));
					rule.setDestination(rs.getString("destination"));
					rule.setCreateTime(rs.getTimestamp("route_rule.create_time"));
					rule.setLastUpdate(rs.getTimestamp("route_rule.last_update"));
					rules.put(ruleId, rule);
				}
				
				//create strategy when its id not exists
				if(strategyId !=0 && strategies.get(strategyId) == null) {
					RouteStrategy strategy = new RouteStrategy();
					strategy.setId(strategyId);
					strategy.setRoute(route);
					strategy.setState(State.getServiceState((rs.getString("strategy_state"))));
					strategy.setType(RouteStrategyType.valueOf(rs.getString("strategy_type")));
					strategy.setOption(rs.getString("strategy_option"));
					strategy.setConfig(rs.getString("strategy_config"));
					strategy.setCreateTime(rs.getTimestamp("route_strategy.create_time"));
					strategy.setLastUpdate(rs.getTimestamp("route_strategy.last_update"));
					strategies.put(strategyId,strategy);
				}
			}
			
			//lists in route to be filled
			ArrayList<RouteNode> nodeList = new ArrayList<RouteNode>();
			ArrayList<RouteRule> ruleList = new ArrayList<RouteRule>();
			ArrayList<RouteStrategy> strategyList = new ArrayList<RouteStrategy>();
			
			//filling the lists
			Iterator iter = nodes.entrySet().iterator();
			while(iter.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<Integer, RouteNode> entry = (Map.Entry<Integer, RouteNode>)iter.next();
				RouteNode node = entry.getValue();
				nodeList.add(node);
			}
			iter = rules.entrySet().iterator();
			while(iter.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<Integer, RouteRule> entry = (Map.Entry<Integer, RouteRule>)iter.next();
				RouteRule rule = entry.getValue();
				ruleList.add(rule);
			}
			iter = strategies.entrySet().iterator();
			while(iter.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<Integer, RouteStrategy> entry = (Map.Entry<Integer, RouteStrategy>)iter.next();
				RouteStrategy strategy = entry.getValue();
				strategyList.add(strategy);
			}
			
			if(route != null) {
				route.setNodes(nodeList);
				route.setRules(ruleList);
				for(RouteStrategy strategy : strategyList){
					route.getStrategies().add(strategy);
				}
			}
			
			return route;
		}
		
	}
}