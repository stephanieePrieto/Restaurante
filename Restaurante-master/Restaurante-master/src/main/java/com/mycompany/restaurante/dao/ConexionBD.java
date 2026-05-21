package com.mycompany.restaurante.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    public static Connection conectar() {
        try {
            String url = "jdbc:mysql://localhost:3306/restaurante";
            String user = "root";
            String password = "rayito28"; 

            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println("Error de conexión a la BD: " + e.getMessage());
            return null;
        }
    }

    public static Connection getConnection() {
        return conectar();
    }

    public static Connection obtenerConexion() {
        return conectar();
    }
}