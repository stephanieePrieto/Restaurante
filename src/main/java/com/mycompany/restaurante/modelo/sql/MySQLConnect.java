package com.mycompany.restaurante.modelo.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnect {

    public static Connection getConexion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private Connection conn;
    private String host = "localhost";
    private String port = "3306";
    private String db = "restaurante"; // Asegúrate que coincida con tu dump (minúsculas)
    private String username = "root";
    private String password = "rayito28";
    private static MySQLConnect connect;

    public MySQLConnect() {
        String driver = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(driver);
            // Intentamos la conexión inicial
            connection(); 
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connect = this;
    }

    public Connection connection() {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useTimezone=true&serverTimezone=UTC";
        try {
            // CORRECCIÓN: Si la conexión es nula o se cerró, la reabrimos automáticamente
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            System.err.println("Error Crítico de Conexión: " + e.getMessage());
        }
        return conn;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mantener los demás métodos (query, update, etc.) igual...
    public static MySQLConnect getConnect() { return connect; }
}