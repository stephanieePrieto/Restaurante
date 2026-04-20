package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Orden;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenDAO {
    private Connection conexion;

    public OrdenDAO(Connection conexion) {
        this.conexion = conexion;
    }
public List<Orden> buscarOrdenesPorEstado(String estado) throws SQLException {
        List<Orden> listaOrdenes = new ArrayList<>(); 
        String sql = "SELECT idPedido, estado, fechaHora FROM pedidos WHERE estado = ? ORDER BY fechaHora ASC";
                     
        
        PlatilloDAO platilloDao = new PlatilloDAO(this.conexion);
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orden orden = new Orden(); 
                    int id = rs.getInt("idPedido");
                    
                    orden.setIdOrden(id);
                    orden.setEstado(rs.getString("estado"));
                    orden.setFechaHoraLlegada(rs.getTimestamp("fechaHora").toLocalDateTime());
                    
                    List<Platillo> platos = platilloDao.obtenerPlatillosPorOrden(id);
                    
                    StringBuilder detalle = new StringBuilder();
                    for (Platillo p : platos) {
                        if (detalle.length() > 0) detalle.append(", ");
                        detalle.append(p.getCantidad()).append(" ").append(p.getNombre());
                    }
                    orden.setMetodoPago(detalle.toString());
                    listaOrdenes.add(orden);
                }
            }
        } catch (SQLException e) {
            // Lanza la excepción para que el GestorOrdenes muestre la PantallaError (Ex-01) 
            throw new SQLException("Error de conexión con el Servidor: " + e.getMessage());
        }
        return listaOrdenes;
    }
}

