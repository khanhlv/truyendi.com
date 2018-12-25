package com.ttruyen.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public class BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);
    private Connection con = null;

    String serverName = "192.168.1.38";
    String portNumber = "1433";
    String databaseName = "TTruyen";
    String username = "sa";
    String password = "";

    String uuid = null;

    public Connection getConnection() {
        String connectionUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";" + "databaseName=" + databaseName + ";username="
                + username + ";password=" + password + ";";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            if (con != null) {
                return con;
            }
            uuid = UUID.randomUUID().toString();
            logger.info("OPEN [" + uuid + "]");
            con = DriverManager.getConnection(connectionUrl);
            con.setAutoCommit(false);
        } catch (Exception ex) {
            logger.error("getConnection", ex);
            return null;
        }

        return con;
    }

    public void commitAndClose() {
       commit();
       close();
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
                logger.info("CLOSE [" + uuid + "]");
                con = null;
            }
            catch (Exception e) {
            }
        }
    }

    public void commit() {
        if (con != null) {
            try {
                con.commit();
                logger.info("COMMIT [" + uuid + "]");
            }
            catch (Exception e) {
            }
        }
    }

    public void rollback() {
        if (con != null) {
            try {
                con.rollback();
                logger.info("ROLLBACK [" + uuid + "]");
                close();
            }
            catch (Exception e) {
            }
        }
    }
}
