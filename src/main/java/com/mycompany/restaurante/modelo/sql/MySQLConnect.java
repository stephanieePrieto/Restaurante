package com.mycompany.restaurante.modelo.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnect {
    private Connection conn;
    private String host = "localhost";
    private String port = "3306";
    private String db = "restaurante";
    private String username = "root";
    private String password = "mandala1406S.";
    private static MySQLConnect connect;
    public MySQLConnect() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useTimezone=true&serverTimezone=UTC";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect = this;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getDb() {
        return db;
    }
    public void setDb(String db) {
        this.db = db;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Connection connection() {
        return conn;
    }
    public ResultSet query(String sQuery) throws SQLException {
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sQuery);
        return rs;
    }
    public int update(String sQuery) throws SQLException {
        Statement s = conn.createStatement();
        int filas = s.executeUpdate(sQuery);
        s.close();
        return filas;
    }
    public void close(Statement s) {
        try {
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static MySQLConnect getConnect() {
        return connect;
    }
    public static void setConnect(MySQLConnect connect) {
        MySQLConnect.connect = connect;
    }
}