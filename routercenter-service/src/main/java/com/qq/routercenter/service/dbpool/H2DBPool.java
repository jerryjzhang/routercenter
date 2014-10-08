package com.qq.routercenter.service.dbpool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import snaq.db.ConnectionPool;

/*This DBPool is used for local testing*/
public class H2DBPool implements DBPool {
	private ConnectionPool pools;

	public H2DBPool() {
		try {
			Class<?> c = Class.forName("org.h2.Driver");
			Driver driver = (Driver) c.newInstance();
			DriverManager.registerDriver(driver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pools = new ConnectionPool("RouterServicePool", 10, 20, 50, 30, "jdbc:h2:./test", null);
	}

	public Connection getConnection() throws SQLException {
		return pools.getConnection();
	}
}
