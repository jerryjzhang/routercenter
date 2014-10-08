package com.qq.routercenter.service.dbpool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;



import com.qq.routercenter.service.config.Configuration;

import snaq.db.ConnectionPool;

public class MysqlDBPool implements DBPool{
	private static final Logger LOG = Logger.getLogger(MysqlDBPool.class);
	private ConnectionPool pools = null;

	static {
		try {
			Class<?> c = Class.forName("com.mysql.jdbc.Driver");
			Driver driver = (Driver) c.newInstance();
			DriverManager.registerDriver(driver);
		}
		catch (Exception e) {
			LOG.error("Failed to create db connection pool: " + e.getMessage());
		}
	}
	
	public MysqlDBPool(){
		String connStr = Configuration.getProperty("db.connection.url");
		String user = Configuration.getProperty("db.user");
		String passwd = Configuration.getProperty("db.password");
		int minPool = Integer.valueOf(Configuration.getProperty("db.pool.min"));
		int maxPool = Integer.valueOf(Configuration.getProperty("db.pool.max"));
		int maxSize = Integer.valueOf(Configuration.getProperty("db.pool.max.size"));
		long idleTimeout = Integer.valueOf(Configuration.getProperty("db.pool.idle.timeout"));
		
		pools = new ConnectionPool("RouterServicePool", minPool, maxPool, 
				maxSize, idleTimeout, connStr, user, passwd);
	}

	public Connection getConnection() throws SQLException {
		return pools.getConnection();
	}
}
