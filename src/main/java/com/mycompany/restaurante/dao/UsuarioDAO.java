package com.mycompany.restaurante.dao;

import java.sql.*;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.modelo.sql.MySQLConnect; 

public class UsuarioDAO {

    public Usuario validarLogin(String user, String pass) {
        // La consulta usa INNER JOIN para traer el nombre del rol desde la tabla 'rol'
        // Extraemos 'usuario' para el login y 'rolNombre' para los permisos del Dashboard
        String sql = "SELECT e.usuario, e.password, r.nombre AS nombreRol " +
                     "FROM empleados e " +
                     "INNER JOIN rol r ON e.idRol = r.idRol " +
                     "WHERE e.usuario = ? AND e.password = ?";
        
        MySQLConnect mysql = new MySQLConnect();
        
        try (Connection con = mysql.connection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user);
            ps.setString(2, pass);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtenemos los datos tal cual están en tu base de datos
                    String username = rs.getString("usuario");
                    String password = rs.getString("password");
                    String rol = rs.getString("nombreRol");
                    
                    // Retornamos el objeto Usuario usando tu constructor:
                    // public Usuario(String username, String password, String rol)
                    return new Usuario(username, password, rol);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO: " + e.getMessage());
        } finally {
            // Cerramos la conexión para liberar recursos de la base de datos
            mysql.close();
        }
        return null; // Si las credenciales no existen o son incorrectas
    }
}