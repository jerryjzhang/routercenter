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
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.qq.routercenter.service.dao.RouteDao;
import com.qq.routercenter.service.dao.RouteNodeDao;
import com.qq.routercenter.service.dbpool.DBPool;
import com.qq.routercenter.share.domain.RouteNode;
import com.qq.routercenter.share.dto.QueryResult;
import com.qq.routercenter.share.dto.RouteNodeQueryCriteria;
import com.qq.routercenter.share.enums.RouteNodeType;
import com.qq.routercenter.share.enums.State;

public class RouteNodeDaoImpl implements RouteNodeDao {
	private static final Logger LOG = Logger.getLogger(RouteNodeDaoImpl.class);

	private DBPool pool = null;
	private final RouteDao routeDao;
	private QueryRunner queryRunner = new QueryRunner();

	private static final Map<String, String> FIELD_TO_COLUMN = new HashMap<String, String>();
	static {
		FIELD_TO_COLUMN.put("id", "node_id");
		FIELD_TO_COLUMN.put("type", "node_type");
		FIELD_TO_COLUMN.put("host", "node_host");
		FIELD_TO_COLUMN.put("port", "node_port");
		FIELD_TO_COLUMN.put("serviceURL", "service_url");
		FIELD_TO_COLUMN.put("state", "node_state");
		FIELD_TO_COLUMN.put("weight", "node_weight");
		FIELD_TO_COLUMN.put("set", "node_set");
		FIELD_TO_COLUMN.put("routeName", "route_name");
	}

	public RouteNodeDaoImpl(DBPool pool, RouteDao routeDao) {
		this.pool = pool;
		this.routeDao = routeDao;
	}

	private final String INSERT_ROUTE = "INSERT INTO route_node(route_id, node_type, node_set, node_host, node_port,"
			+ " service_url, node_state, node_weight, create_time, last_update) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, now(), now())";
	@Override
	public int insert(RouteNode node) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(INSERT_ROUTE,
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, node.getRoute().getId());
			ps.setString(2, node.getType().toString());
			ps.setString(3, node.getSet());
			ps.setString(4, node.getHost());
			ps.setInt(5, node.getPort());
			ps.setString(6, node.getServiceURL());
			ps.setString(7, node.getState().toString());
			ps.setInt(8, node.getWeight());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				node.setId(rs.getInt(1));
			}
			return node.getId();
		} catch (SQLException e) {
			LOG.error("Failed to insert routeNode:" + node + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(ps);
		}
	}

	private final String DELETE_ROUTE = "delete from route_node where node_id = ?";
	@Override
	public int delete(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ROUTE, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeNodeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	public int[] deleteBatch(Set<RouteNode> nodes) {
		Connection conn = null;
		int nodeId = 0;
		try {
			conn = pool.getConnection();
			Object[][] params = new Object[nodes.size()][1];
			int pos = 0;
			for(RouteNode node : nodes){
				nodeId = node.getId();
				params[pos++][0] = node.getId();
			}
			return queryRunner.batch(conn, DELETE_ROUTE, params);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeNodeId:" + nodeId + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String DELETE_ByRouteId = "delete from route_node where route_id = ?";
	@Override
	public int deleteByRouteId(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, DELETE_ByRouteId, id);
		} catch (SQLException e) {
			LOG.error("Failed to delete routeNode with routeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_ROUTE = "UPDATE route_node SET  node_host = ?, node_port = ?,node_type = ?, node_state = ?, "
			+ "service_url = ?, node_set = ?, node_weight = ?, last_update = now() "
			+ "WHERE node_id = ?";
	@Override
	public int update(RouteNode node) {
		Connection conn = null;
		int id = node.getId();
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_ROUTE, node.getHost(), node
					.getPort(), node.getType().toString(), node.getState()
					.toString(), node.getServiceURL(), node.getSet(), node.getWeight(), 
					id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeNode:" + node + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String UPDATE_STATE = "UPDATE route_node SET node_state = ?,last_update = now()"
			+ "WHERE node_id = ?";
	@Override
	public int update(int id, State state) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.update(conn, UPDATE_STATE, state.toString(), id);
		} catch (SQLException e) {
			LOG.error("Failed to update routeNodeId:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}

	}

	@Override
	public QueryResult<RouteNode> queryByCriteira(
			RouteNodeQueryCriteria criteria, int limit, int offset) {
		int id = criteria.getId();
		String host = criteria.getHost();
		State state = criteria.getState();
		RouteNodeType type = criteria.getType();
		int port = criteria.getPort();
		String searchKey = criteria.getSearchKey();
		String routeName = criteria.getRouteName();
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<Object> colValues = new ArrayList<Object>();
		if (id > 0) {
			colNames.add("node_id =");
			colValues.add(id);
		}
		if (host != null) {
			colNames.add("node_host LIKE");
			colValues.add(host + "%");
		}
		if (state != null) {
			colNames.add("node_state =");
			colValues.add(state.toString());
		}
		if(type != null){
			colNames.add("node_type =");
			colValues.add(type.toString());
		}
		if (port > 0) {
			colNames.add("route_incharge LIKE");
			colValues.add("%" + port + "%");
		}
		if (routeName != null) {
			colNames.add("route_name LIKE");
			colValues.add("%" + routeName + "%");
		}
		if (searchKey != null) {
			colNames.add("concat(node_host,node_port,service_url,node_set) LIKE");
			colValues.add("%" + searchKey + "%");
		}
		String sql = "select SQL_CALC_FOUND_ROWS * from route, route_node where route.route_id = route_node.route_id AND 1 = 1";
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
		List<RouteNode> routenodeList = new ArrayList<RouteNode>();
		int count = 0;
		try {
			conn = pool.getConnection();
			routenodeList = queryRunner.query(conn, sql,
					new RouteNodeListHandler(), colValues.toArray());
			count = queryRunner
					.query(conn, sqlCount, new ScalarHandler<Long>())
					.intValue();
		} catch (SQLException e) {
			LOG.error("Failed to query routeNode with criteria:" + criteria + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally{
			DbUtils.closeQuietly(conn);
		}
		QueryResult<RouteNode> queryResultNode = new QueryResult<RouteNode>(
				routenodeList, count);
		return queryResultNode;

	}

	private final String QUERY_ID = "SELECT * FROM route, route_node WHERE route.route_id = route_node.route_id AND node_id = ?";

	@Override
	public RouteNode queryById(int id) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner
					.query(conn, QUERY_ID, new RouteNodeHandler(), id);
		} catch (SQLException e) {
			LOG.error("Failed to query routeNode with id:" + id + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

	private final String QUERY_URL = "SELECT * FROM route, route_node WHERE route.route_id = route_node.route_id AND service_url = ?";
	@Override
	public RouteNode queryByUrl(String url) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			return queryRunner.query(conn, QUERY_URL, new RouteNodeHandler(),
					url);
		} catch (SQLException e) {
			LOG.error("Failed to query routeNode with url:" + url + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally{
			DbUtils.closeQuietly(conn);
		}
	}

	private class RouteNodeListHandler implements
			ResultSetHandler<List<RouteNode>> {

		@Override
		public List<RouteNode> handle(ResultSet rs) throws SQLException {
			List<RouteNode> routenodeList = new LinkedList<RouteNode>();

			while (rs.next()) {
				RouteNode node = new RouteNode();
				node.setId(rs.getInt("node_id"));
				node.setRoute(routeDao.queryById(rs.getInt("route_id")));
				node.setType(RouteNodeType.valueOf(rs.getString("node_type")));
				node.setSet(rs.getString("node_set"));
				node.setHost(rs.getString("node_host"));
				node.setPort(rs.getInt("node_port"));
				node.setServiceURL(rs.getString("service_url"));
				node.setState(State.getServiceState((rs.getString("node_state"))));
				node.setWeight(rs.getInt("node_weight"));
				node.setCreateTime(rs.getTimestamp("create_time"));
				node.setLastUpdate(rs.getTimestamp("last_update"));
				routenodeList.add(node);
			}
			return routenodeList;
		}

	}

	private class RouteNodeHandler implements ResultSetHandler<RouteNode> {

		@Override
		public RouteNode handle(ResultSet rs) throws SQLException {
			List<RouteNode> routenodeList = new RouteNodeListHandler()
					.handle(rs);
			if (routenodeList.size() > 0) {
				return routenodeList.get(0);
			} else
				return null;
		}

	}

	private final String QUERY_ROUTEID = "SELECT * FROM route,route_node WHERE route.route_id = route_node.route_id AND route_node.route_id = ?";
	@Override
	public List<RouteNode> queryByRouteId(int routeId) {
		Connection conn = null;
		List<RouteNode> routenodeList = new ArrayList<RouteNode>();
		try {
			conn = pool.getConnection();
			routenodeList = queryRunner.query(conn, QUERY_ROUTEID,
					new RouteNodeListHandler(), routeId);
		} catch (SQLException e) {
			LOG.error("Failed to query routeNode with routeId:" + routeId + "," + e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally{
			DbUtils.closeQuietly(conn);
		}
		return routenodeList;

	}

}