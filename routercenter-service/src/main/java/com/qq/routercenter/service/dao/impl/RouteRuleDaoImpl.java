package com.qq.routercenter.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.qq.routercenter.service.dao.RouteRuleDao;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteRule;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteRuleQueryCriteria;
import com.qq.routercenter.share.enums.RouteRuleOp;
import com.qq.routercenter.share.enums.RouteRuleType;
import com.qq.routercenter.share.enums.State;

public class RouteRuleDaoImpl implements RouteRuleDao {
	private static final Logger LOG = Logger.getLogger(RouteRuleDaoImpl.class);

	private DBPool pool = null;
	private QueryRunner queryRunner = new QueryRunner();

	private static final Map<String, String> FIELD_TO_COLUMN = new HashMap<String, String>();
	static {
		FIELD_TO_COLUMN.put("id", "rule_id");
		FIELD_TO_COLUMN.put("state", "rule_state");
		FIELD_TO_COLUMN.put("type", "rule_type");
		FIELD_TO_COLUMN.put("srcProp", "source_prop");
		FIELD_TO_COLUMN.put("srcOp", "source_op");
		FIELD_TO_COLUMN.put("srcValue", "source_value");
		FIELD_TO_COLUMN.put("destination", "destination");
		FIELD_TO_COLUMN.put("routeName", "route_name");
	}

	public RouteRuleDaoImpl(DBPool pool) {
		this.pool = pool;
	}

	private final String INSERT_RULE = "INSERT INTO route_rule(route_id, rule_state, rule_type, source_prop, "
			+ "source_op, source_value, destination, create_time, last_update) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, now(), now())";

	@Override
	public int insert(RouteRule rule) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(INSERT_RULE,
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, rule.getRoute().getId());
			ps.setString(2, rule.getState().toString());
			ps.setString(3, rule.getType().toString());
			ps.setString(4, rule.getSrcProp().toString());
			ps.setString(5, rule.getSrcOp().toString());
			ps.setString(6, rule.getSrcValue());
			ps.setString(7, rule.getDestination());
			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			int id = 0;
			if (rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		} catch (SQLException e) {
			LOG.error("Failed to insert routeRule:" + rule + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(ps);
		}
	}

	private final String DELETE_RULE = "delete from route_rule where rule_id = ?";
	@Override
	public int delete(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_RULE, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeRuleId:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String DELETE_ByRouteId = "delete from route_rule where route_id = ?";
	@Override
	public int deleteByRouteId(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ByRouteId, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeRule with routeId:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_RULE = "UPDATE route_rule SET rule_state = ?, rule_type = ?, source_prop = ?, "
			+ "source_op = ?, source_value = ?, destination = ?, "
			+ "last_update = now() " + "WHERE rule_id = ?";
	@Override
	public int update(RouteRule rule) {
		Connection conn = null;
		int id = rule.getId();
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_RULE, 
					rule.getState().toString(), rule.getType().toString(), 
					rule.getSrcProp(), rule.getSrcOp().toString(), 
					rule.getSrcValue(), rule.getDestination(), 
					id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeRule:" + rule + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_STATE = "UPDATE route_rule SET rule_state = ?, last_update = now() "
			+ "WHERE rule_id = ?";
	@Override
	public int update(int id, State state) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_STATE, state.toString(), id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeRule with id:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	@Override
	public QueryResult<RouteRule> queryByCriteira(
			RouteRuleQueryCriteria criteria, int limit, int offset) {
		// TODO Auto-generated method stub
		int id = criteria.getId();
		State state = criteria.getState();
		RouteRuleType type = criteria.getType();
		String srcProp = criteria.getSrcProp();
		RouteRuleOp srcOp = criteria.getSrcOp();
		String srcValue = criteria.getSrcValue();
		String destination = criteria.getDestination();
		String routeName = criteria.getRouteName();
		String searchKey = criteria.getSearchKey();

		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<Object> colValues = new ArrayList<Object>();
		if (id > 0) {
			colNames.add("rule_id =");
			colValues.add(id);
		}
		if (state != null) {
			colNames.add("rule_state =");
			colValues.add(state.toString());
		}
		if(type != null){
			colNames.add("rule_type =");
			colValues.add(type.toString());
		}
		if (srcProp != null) {
			colNames.add("source_prop LIKE");
			colValues.add("%" + srcProp.toString() + "%");
		}
		if (srcOp != null) {
			colNames.add("source_op =");
			colValues.add(srcOp.toString());
		}
		if (srcValue != null) {
			colNames.add("source_value LIKE");
			colValues.add("%" + srcValue + "%");
		}
		if (destination != null) {
			colNames.add("destination LIKE");
			colValues.add("%" + destination + "%");
		}

		if (routeName != null) {
			colNames.add("route_name LIKE");
			colValues.add("%" + routeName + "%");
		}

		// 关键字
		if (searchKey != null) {
			colNames.add("concat(source_prop, source_value, destination) LIKE");
			colValues.add("%" + searchKey + "%");
		}

		String sql = "SELECT SQL_CALC_FOUND_ROWS * FROM route, route_rule WHERE route.route_id = route_rule.route_id AND 1 = 1";
		for (int i = 0; i < colNames.size(); i++) {
			sql += " AND " + colNames.get(i) + " ?";
		}
		// 分组
		if (criteria.getOrderField() != null && criteria.getOrderType() != null) {
			sql += " order by " + FIELD_TO_COLUMN.get(criteria.getOrderField())
					+ " " + criteria.getOrderType();
		} else {
			sql += " order by route.route_name asc";
		}

		if (limit > 0) {
			sql += " limit " + limit + " offset " + offset;
		}

		String sqlCount = "select FOUND_ROWS()";
		Connection conn = null;
		List<RouteRule> routeList = new ArrayList<RouteRule>();
		int count = 0;
		try {
			conn = pool.getConnection();
			routeList = queryRunner.query(conn, sql, new RuleListHandler(),
					colValues.toArray());
			count = queryRunner
					.query(conn, sqlCount, new ScalarHandler<Long>())
					.intValue();
		} catch (SQLException e) {
			LOG.error("Failed to query routeRule with criteria:" + criteria + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
		QueryResult<RouteRule> queryResultRouteRule = new QueryResult<RouteRule>(
				routeList, count);
		return queryResultRouteRule;
	}

	private final String QUERY_ID = "SELECT * FROM route, route_rule WHERE route.route_id = route_rule.route_id AND rule_id = ?";

	@Override
	public RouteRule queryById(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_ID, new RuleHandler(), id);
		} catch (SQLException e) {
			LOG.error("Failed to query routeRule with id:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String QUERY_ROUTE_ID = "SELECT * FROM route, route_rule WHERE route.route_id = route_rule.route_id AND route.route_id = ?";

	@Override
	public List<RouteRule> queryByRouteId(int routeId) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_ROUTE_ID,
					new RuleListHandler(), routeId);
		} catch (SQLException e) {
			LOG.error("Failed to query routeRule with routeId:" + routeId + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	/**
	 * 
	 * @ClassName: RuleListHandler
	 * @Description: This result set handler parses the query results of
	 *               route_rule table into a ArrayList of RouteRule object
	 * @author: florianfan
	 * @date: 2014骞�鏈�鏃�涓嬪崍5:16:40
	 */
	private class RuleListHandler implements ResultSetHandler<List<RouteRule>> {

		@Override
		public List<RouteRule> handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<RouteRule> ruleList = new LinkedList<RouteRule>();
			while (rs.next()) {
				Route route = new Route();
				route.setId(rs.getInt("route_id"));
				route.setName(rs.getString("route_name"));
				route.setDesc(rs.getString("route_desc"));
				route.setState(State.getServiceState((rs
						.getString("route_state"))));
				route.setIncharge(rs.getString("route_incharge"));
				route.setCreateTime(rs.getTimestamp("route.create_time"));
				route.setLastUpdate(rs.getTimestamp("route.last_update"));

				RouteRule rule = new RouteRule();
				rule.setId(rs.getInt("rule_id"));
				rule.setRoute(route);
				rule.setState(State.getServiceState((rs.getString("rule_state"))));
				rule.setType(RouteRuleType.valueOf(rs.getString("rule_type")));
				rule.setSrcProp(rs.getString("source_prop"));
				rule.setSrcOp(RouteRuleOp.valueOf(rs.getString("source_op")));
				rule.setSrcValue(rs.getString("source_value"));
				rule.setDestination(rs.getString("destination"));
				rule.setCreateTime(rs.getTimestamp("route_rule.create_time"));
				rule.setLastUpdate(rs.getTimestamp("route_rule.last_update"));
				ruleList.add(rule);
			}
			return ruleList;
		}

	}

	/**
	 * @ClassName: RuleHandler
	 * @Description: This handler wraps RouteListHandler and return the unique
	 *               first element of Route list
	 * @author: florianfan
	 * @date: 2014骞�鏈�鏃�涓嬪崍5:17:40
	 */
	private class RuleHandler implements ResultSetHandler<RouteRule> {

		@Override
		public RouteRule handle(ResultSet rs) throws SQLException {
			// TODO Auto-generated method stub
			List<RouteRule> ruleList = new RuleListHandler().handle(rs);
			if (ruleList.size() > 0) {
				return ruleList.get(0);
			} else
				return null;
		}

	}

}
