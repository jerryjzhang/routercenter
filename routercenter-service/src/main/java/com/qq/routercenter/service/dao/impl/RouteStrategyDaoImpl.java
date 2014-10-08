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

import com.qq.routercenter.service.dao.RouteStrategyDao;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.share.domain.Route;
import com.qq.routercenter.share.domain.RouteStrategy;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteStrategyQueryCriteria;
import com.qq.routercenter.share.enums.RouteStrategyType;
import com.qq.routercenter.share.enums.State;

public class RouteStrategyDaoImpl implements RouteStrategyDao {
	private static final Logger LOG = Logger
			.getLogger(RouteStrategyDaoImpl.class);

	private DBPool pool = null;
	private QueryRunner queryRunner = new QueryRunner();

	private static final Map<String, String> FIELD_TO_COLUMN = new HashMap<String, String>();
	static {
		FIELD_TO_COLUMN.put("id", "strategy_id");
		FIELD_TO_COLUMN.put("type", "strategy_type");
		FIELD_TO_COLUMN.put("option", "strategy_option");
		FIELD_TO_COLUMN.put("config", "strategy_config");
		FIELD_TO_COLUMN.put("state", "strategy_state");
		FIELD_TO_COLUMN.put("routeName", "route_name");
	}

	public RouteStrategyDaoImpl(DBPool pool) {
		this.pool = pool;
	}

	private final String INSERT_ROUTE = "INSERT INTO route_strategy( route_id, strategy_state, strategy_type,"
			+ "strategy_option, strategy_config, create_time, last_update) " + "VALUES(?, ?, ?,  ?, ?, now(), now())";

	@Override
	public int insert(RouteStrategy strategy) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(INSERT_ROUTE,
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, strategy.getRoute().getId());
			ps.setString(2, strategy.getState().toString());
			ps.setString(3, strategy.getType().toString());
			ps.setString(4, strategy.getOption());
			ps.setString(5, strategy.getConfig());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				strategy.setId(rs.getInt(1));
			}
			return strategy.getId();
		} catch (SQLException e) {
			LOG.error("Failed to insert routeStrategy:" + strategy + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(ps);
		}
	}

	private final String DELETE_ROUTE = "delete from route_strategy where strategy_id = ?";

	@Override
	public int delete(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ROUTE, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeStrategy with id:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String DELETE_ByRouteId = "delete from route_strategy where route_id = ?";

	@Override
	public int deleteByRouteId(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ByRouteId, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeStrategy with routeId:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_ROUTE = "UPDATE route_strategy SET"
			+ " strategy_option = ? , strategy_config = ?, strategy_state = ?, strategy_type = ?, "
			+ "last_update = now() "
			+ " WHERE strategy_id = ?";

	@Override
	public int update(RouteStrategy strategy) {
		Connection conn = null;
		int id = strategy.getId();
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_ROUTE, strategy.getOption(),
					strategy.getConfig(), strategy.getState().toString(),
					strategy.getType().toString(), id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeStrategy:" + strategy + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_STATE = "UPDATE route_strategy SET strategy_state = ?, "
			+ "last_update = now() "
			+ "WHERE strategy_id = ?";

	@Override
	public int update(int id, State state) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_STATE, state.toString(), id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeStrategy with id:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	@Override
	public QueryResult<RouteStrategy> queryByCriteira(
			RouteStrategyQueryCriteria criteria, int limit, int offset) {
		int id = criteria.getId();

		State state = criteria.getState();
		String option = criteria.getOption();
		String searchKey = criteria.getSearchKey();
		String routeName = criteria.getRouteName();
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<Object> colValues = new ArrayList<Object>();
		if (id > 0) {
			colNames.add("strategy_id =");
			colValues.add(id);
		}
		if (state != null) {
			colNames.add("strategy_state LIKE");
			colValues.add(state.toString());
		}
		if (routeName != null) {
			colNames.add("route_name LIKE");
			colValues.add("%" + routeName + "%");
		}
		if (option != null) {

			colNames.add("strategy_option LIKE");
			colValues.add("%" + option + "%");
		}
		if (searchKey != null) {
			colNames.add("concat(strategy_option, strategy_config) LIKE");
			colValues.add("%" + searchKey + "%");
		}
		String sql = "select SQL_CALC_FOUND_ROWS * from route, route_strategy where route.route_id = route_strategy.route_id AND 1 = 1";
		for (int i = 0; i < colNames.size(); i++) {
			sql += " AND " + colNames.get(i) + " ?";
		}

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
		List<RouteStrategy> routestrategyList = new ArrayList<RouteStrategy>();
		int count = 0;
		try {
			conn = pool.getConnection();
			routestrategyList = queryRunner.query(conn, sql,
					new RouteStrastegyListHandler(), colValues.toArray());
			count = queryRunner
					.query(conn, sqlCount, new ScalarHandler<Long>())
					.intValue();
		} catch (SQLException e) {
			LOG.error("Failed to query routeStrategy with criteria:" + criteria
					+ "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
		QueryResult<RouteStrategy> queryResultRouteStrategy = new QueryResult<RouteStrategy>(
				routestrategyList, count);
		return queryResultRouteStrategy;
	}

	private final String QUERY_ID = "SELECT * FROM route, route_strategy WHERE route.route_id = route_strategy.route_id AND strategy_id = ?";

	@Override
	public RouteStrategy queryById(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_ID,
					new RouteStrategyHandler(), id);
		} catch (SQLException e) {
			LOG.error("Failed to query routeStrategy with id:" + id + ","
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private class RouteStrastegyListHandler implements
			ResultSetHandler<List<RouteStrategy>> {

		@Override
		public List<RouteStrategy> handle(ResultSet rs) throws SQLException {

			List<RouteStrategy> routenodeList = new LinkedList<RouteStrategy>();

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

				RouteStrategy strategy = new RouteStrategy();
				strategy.setId(rs.getInt("strategy_id"));
				strategy.setRoute(route);
				strategy.setState(State.getServiceState((rs
						.getString("strategy_state"))));
				strategy.setType(RouteStrategyType.valueOf(rs
						.getString("strategy_type")));
				strategy.setOption(rs.getString("strategy_option"));
				strategy.setConfig(rs.getString("strategy_config"));
				strategy.setCreateTime(rs.getTimestamp("route_strategy.create_time"));
				strategy.setLastUpdate(rs.getTimestamp("route_strategy.last_update"));
				routenodeList.add(strategy);
			}
			return routenodeList;
		}

	}

	private final String QUERY_ROUTEID = "SELECT * FROM route, route_strategy WHERE route.route_id = route_strategy.route_id AND route_strategy.route_id = ?";

	@Override
	public List<RouteStrategy> queryByRouteId(int routeId) {
		Connection conn = null;
		List<RouteStrategy> routenodeList = new ArrayList<RouteStrategy>();
		try {
			conn = pool.getConnection();
			routenodeList = queryRunner.query(conn, QUERY_ROUTEID,
					new RouteStrastegyListHandler(), routeId);
		} catch (SQLException e) {
			LOG.error("Failed to query routeStrategy with routeId:" + routeId
					+ "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return routenodeList;

	}

	private class RouteStrategyHandler implements
			ResultSetHandler<RouteStrategy> {

		@Override
		public RouteStrategy handle(ResultSet rs) throws SQLException {

			List<RouteStrategy> routenodeList = new RouteStrastegyListHandler()
					.handle(rs);
			if (routenodeList.size() > 0) {
				return routenodeList.get(0);
			} else
				return null;
		}

	}

}
