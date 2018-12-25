package com.ttruyen.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
//        InputStream in = StringUtil.class.getClassLoader().getResourceAsStream("config.properties");
//        Properties properties = new Properties();
//        properties.load(new InputStreamReader(in));

//        config.setJdbcUrl("jdbc:sqlserver://101.99.17.214:7001;databaseName=TNT");
//        config.setUsername("sa");
//        config.setPassword("AecaykheSpeed123456");

        config.setJdbcUrl("jdbc:sqlserver://ttruyen.com:1433;databaseName=");
        config.setUsername("sieuthitretho_vn_ttruyen");
        config.setPassword("@");

//        config.setJdbcUrl("jdbc:sqlserver://192.168.1.38:1433;databaseName=TTruyen");
//        config.setUsername("sa");
//        config.setPassword("");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        ds = new HikariDataSource(config);
    }

    private ConnectionPool() { }

    public static Connection getTransactional() throws SQLException {
        return ds.getConnection();
    }
}

