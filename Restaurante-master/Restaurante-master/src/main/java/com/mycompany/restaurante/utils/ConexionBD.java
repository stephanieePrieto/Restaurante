package com.mycompany.restaurante.utils;

import java.sql.Connection;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;

public class ConexionBD {
    
    public static Connection obtenerConexion() {
        // Ahora sí usamos tu conexión real a MySQL
        MySQLConnect conexionReal = new MySQLConnect();
        return conexionReal.connection();
    }
}