package com.theone.a_levelwallet.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by lh on 2015/8/18.
 */
public class DBUtil
{

    public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
            conn= DriverManager.getConnection("jdbc:microsoft:sqlserver://192.168.191.1:1433;DatabaseName=mypetstore;user=sa;password=988761");
        } catch (Exception e) {
            throw e;
        }
        return conn;
    }
    public static void closeStatement(Statement statement) throws Exception {
        statement.close();
    }
    public static void closePreparedStatement(PreparedStatement pStatement)
            throws Exception {
        pStatement.close();
    }
    public static void closeResultSet(ResultSet resultSet) throws Exception {
        resultSet.close();
    }
    public static void closeConnection(Connection connection) throws Exception {
        connection.close();
    }

}

