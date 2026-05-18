package com.mycompany.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.restaurante.modelo.pojo.Mesa;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;

public class MesaDAO {

    // metodo para mostrar las tablas 
    public List<Mesa> listarMesas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT idMesa, estado FROM mesa";
        
        MySQLConnect mysql = new MySQLConnect();
        Connection con = mysql.connection(); 
        
        if (con == null) {
            System.err.println("¡ERROR MESA_DAO! No se pudo conectar a la base de datos.");
            return lista;
        }
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Mesa m = new Mesa(
                    rs.getInt("idMesa"),
                    rs.getString("estado")
                );
                lista.add(m);
            }
            System.out.println("--- ÉXITO MESA_DAO: Mesas cargadas con mysql.connection() ---");
            
        } catch (Exception e) {
            System.err.println("Error en MesaDAO.listarMesas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mysql.close(); // Siempre cerramos la conexión al terminar
        }
        return lista;
    }

    //metodo para cambiar la mesa, (liberar)
    public void liberarMesa(int idMesa) {
        String sql = "UPDATE mesa SET estado = 'Libre' WHERE idMesa = ?";
        
        MySQLConnect mysql = new MySQLConnect();
        Connection con = mysql.connection();
        
        if (con == null) {
            System.err.println("¡ERROR MESA_DAO! No se pudo conectar a la base de datos para liberar.");
            return;
        }
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idMesa);
            ps.executeUpdate();
            System.out.println("--- ÉXITO MESA_DAO: Mesa " + idMesa + " liberada con éxito ---");
            
        } catch (Exception e) {
            System.err.println("Error en MesaDAO.liberarMesa: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mysql.close(); // Siempre cerramos la conexión al terminar
        }
    }
}