package com.mycompany.restaurante.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;

public class UsuarioDAO {

    public Usuario validarLogin(String user, String pass) {
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
                    return new Usuario(rs.getString("usuario"), rs.getString("password"), rs.getString("nombreRol"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            mysql.close();
        }
        return null;
    }

    public List<Usuario> obtenerEmpleados() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT e.idEmpleado, e.nombre, e.usuario, e.password, e.idRol, r.nombre AS nombreRol " +
                     "FROM empleados e INNER JOIN rol r ON e.idRol = r.idRol";
                     
        MySQLConnect mysql = new MySQLConnect();
        
        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                lista.add(new Usuario(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("password"),
                    rs.getInt("idRol"),
                    rs.getString("nombreRol")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            mysql.close();
        }
        return lista;
    }

    public boolean registrarEmpleado(Usuario u) {
        String sql = "INSERT INTO empleados (nombre, usuario, password, idRol) VALUES (?, ?, ?, ?)";
        MySQLConnect mysql = new MySQLConnect();
        
        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPassword());
            ps.setInt(4, u.getIdRol());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            mysql.close();
        }
    }

    public boolean actualizarEmpleado(Usuario u) {
        String sql = "UPDATE empleados SET nombre=?, usuario=?, password=?, idRol=? WHERE idEmpleado=?";
        MySQLConnect mysql = new MySQLConnect();
        
        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPassword());
            ps.setInt(4, u.getIdRol());
            ps.setInt(5, u.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            return false;
        } finally {
            mysql.close();
        }
    }
}