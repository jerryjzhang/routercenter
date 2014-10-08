package com.qq.routercenter.service.dbpool;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBPool {
	public Connection getConnection() throws SQLException;
}
