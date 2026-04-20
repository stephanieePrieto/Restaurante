package com.mycompany.restaurante.dao;


import com.mycompany.restaurante.modelo.pojo.Platillo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatilloDAO {
    private Connection conexion;

    public PlatilloDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Recupera platillos asociados a cada orden (Paso 3 del Flujo Normal) 
    public List<Platillo> obtenerPlatillosPorOrden(int idOrden) throws SQLException {
        List<Platillo> listaPlatillos = new ArrayList<>();
        // Relaciona la tabla detalle con la tabla platillo para obtener el nombre [cite: 2, 23]
        String sql = "SELECT p.nombre, dp.cantidad FROM detallePedidos dp " +
                     "JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                     "WHERE dp.idPedido = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Platillo platillo = new Platillo();
                    platillo.setNombre(rs.getString("nombre"));
                    platillo.setCantidad(rs.getInt("cantidad"));
                    listaPlatillos.add(platillo);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al recuperar detalles del platillo: " + e.getMessage());
        }
        return listaPlatillos;
    }
}
    
