package com.mycompany.restaurante.utils;

import java.sql.Connection;

public class ConexionBD {
    // Retornamos null para que el DAO no truene al recibir el parámetro
    public static Connection obtenerConexion() {
        System.out.println("[INFO] Simulando conexión a la base de datos...");
        return null; 
    }
}