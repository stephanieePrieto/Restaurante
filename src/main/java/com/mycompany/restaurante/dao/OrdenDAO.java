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
  /**  public List<Orden> buscarOrdenesPorEstado(String estado) throws SQLException {
        List<Orden> listaOrdenes = new ArrayList<>(); 
        String sql = "SELECT id_orden, estado, fecha_hora_llegada, id_mesa, id_mesero, total, metodo_pago " +
                     "FROM Orden WHERE estado = ? ORDER BY fecha_hora_llegada ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                 
                    Orden orden = new Orden(); 
                    orden.setIdOrden(rs.getInt("id_orden"));
                    orden.setEstado(rs.getString("estado"));
                    orden.setFechaHoraLlegada(rs.getTimestamp("fecha_hora_llegada").toLocalDateTime());
                    orden.setIdMesa(rs.getInt("id_mesa"));
                    orden.setIdMesero(rs.getInt("id_mesero"));
                    orden.setTotal(rs.getDouble("total"));
                    orden.setMetodoPago(rs.getString("metodo_pago"));
                    
                    listaOrdenes.add(orden);
                }
            }
        } catch (SQLException e) {
            // Lanza la excepción para que el GestorOrdenes muestre la PantallaError (Ex-01) 
            throw new SQLException("Error de conexión con el Servidor: " + e.getMessage());
        }
        return listaOrdenes;
    } **/
    public List<Orden> buscarOrdenesPorEstado (String estado) throws SQLException{
    List<Orden> listaPrueba = new ArrayList<>();

    // Creamos una orden manual para simular la DB
    Orden orden1 = new Orden();
    orden1.setIdOrden(101);
    orden1.setEstado("Pendiente"); // 
    orden1.setFechaHoraLlegada(LocalDateTime.now());
    orden1.setIdMesa(5); // [cite: 17]

    // Le añadimos platillos de prueba
    List<Platillo> platillos = new ArrayList<>();
    Platillo p1 = new Platillo();
    p1.setNombre("Enchiladas");
    p1.setCantidad(2);
    
    platillos.add(p1);
    orden1.setListaPlatillo(platillos);

    listaPrueba.add(orden1);
    return listaPrueba;
}


    public boolean actualizarEstadoOrden(int idOrden, String nuevoEstado) throws SQLException {
        String sql = "UPDATE Orden SET estado = ? WHERE id_orden = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idOrden);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            throw new SQLException("No se pudo actualizar el estado de la orden: " + e.getMessage());
        }
    }
}