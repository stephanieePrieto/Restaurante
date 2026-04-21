package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Pedido;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private Connection conexion;

    public PedidoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Pedido> buscarPedidosPorEstado(String estado) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT idPedido, estado, fechaHora FROM pedidos WHERE estado = ? ORDER BY fechaHora ASC";
        
        PlatilloDAO platilloDao = new PlatilloDAO(this.conexion);
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = new Pedido();
                    int id = rs.getInt("idPedido");
                    pedido.setIdPedido(id);
                    pedido.setEstado(rs.getString("estado"));
                    pedido.setFechaHora(rs.getTimestamp("fechaHora").toLocalDateTime());
                    
                    // Armamos el texto para la tabla
                    List<Platillo> platos = platilloDao.obtenerPlatillosPorOrden(id);
                    StringBuilder sb = new StringBuilder();
                    for (Platillo p : platos) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(p.getCantidad()).append(" ").append(p.getNombre());
                    }
                    pedido.setDetalleTexto(sb.toString());
                    lista.add(pedido);
                }
            }
        }
        return lista;
    }
    // Asegúrate de que este método esté dentro de PedidoDAO.java
public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) throws SQLException {
    // Usamos los nombres exactos de tu tabla 'pedidos' e 'idPedido'
    String sql = "UPDATE pedidos SET estado = ? WHERE idPedido = ?";
    
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
        ps.setString(1, nuevoEstado);
        ps.setInt(2, idPedido);
        
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;
    } catch (SQLException e) {
        throw new SQLException("No se pudo actualizar el estado en la BD: " + e.getMessage());
    }
}
}
