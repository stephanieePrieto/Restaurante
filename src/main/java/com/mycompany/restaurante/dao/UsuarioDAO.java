package com.mycompany.restaurante.dao;

import java.sql.*;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.modelo.sql.MySQLConnect; // Importamos tu clase de conexión

public class UsuarioDAO {

    public Usuario validarLogin(String user, String pass) {
        // El Query busca en empleados y obtiene el nombre del Rol
        String sql = "SELECT e.usuario, r.nombre AS rolNombre " +
                     "FROM empleados e " +
                     "INNER JOIN rol r ON e.idRol = r.idRol " +
                     "WHERE e.usuario = ? AND e.password = ?";
        
        // 1. Instanciamos tu clase de conexión
        MySQLConnect mysql = new MySQLConnect();
        
        // 2. Usamos el método connection() que es el que devuelve el objeto Connection
        try (Connection con = mysql.connection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user);
            ps.setString(2, pass);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreUsuario = rs.getString("usuario");
                    String nombreRol = rs.getString("rolNombre");
                    
                    // Retornamos el objeto Usuario con los datos de la DB
                    return new Usuario(nombreUsuario, null, nombreRol);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error técnico en UsuarioDAO: " + e.getMessage());
        } finally {
            // Cerramos la conexión al terminar
            mysql.close();
        }
        return null; 
    }
}