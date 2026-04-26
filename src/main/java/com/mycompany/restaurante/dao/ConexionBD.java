package com.mycompany.restaurante.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    public static Connection conectar() {
        try {
            String url = "jdbc:mysql://localhost:3306/restaurante";
            String user = "root";
            String password = "1234";

            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println("Error de conexión");
            return null;
        }
    }

    static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}