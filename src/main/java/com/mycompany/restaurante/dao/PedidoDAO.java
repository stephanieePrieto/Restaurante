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

// 1. Revisa si la mesa ya tiene un pedido sin cobrar
    public int obtenerPedidoActivoPorMesa(int idMesa) throws SQLException {
        String sql = "SELECT idPedido FROM pedidos WHERE idMesa = ? AND estado IN ('Pendiente', 'En preparación')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idPedido");
            }
        }
        return -1; // Retorna -1 si la mesa es nueva y no tiene pedido
    }

    // 2. Crea un pedido en blanco y te devuelve el número de ticket (idPedido)
    public int crearNuevoPedido(int idMesa) throws SQLException {
        String sql = "INSERT INTO pedidos (idMesa, estado) VALUES (?, 'Pendiente')";
        // Statement.RETURN_GENERATED_KEYS nos permite saber qué ID le asignó MySQL
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo generar el pedido.");
    }

// 3. Guarda el ticket en la BD (Borra el anterior y guarda el nuevo actualizado)
    public void guardarDetallesPedido(int idPedido, List<Platillo> carrito) throws SQLException {
        // Primero limpiamos los detalles anteriores para evitar duplicados
        try (PreparedStatement psDelete = conexion.prepareStatement("DELETE FROM detallePedidos WHERE idPedido = ?")) {
            psDelete.setInt(1, idPedido);
            psDelete.executeUpdate();
        }

        // Truco SQL: Insertamos buscando el idPlatillo a partir del nombre
        String sqlInsert = "INSERT INTO detallePedidos (idPedido, idPlatillo, cantidad) " +
                           "SELECT ?, idPlatillo, ? FROM platillos WHERE nombre = ?";
                           
        try (PreparedStatement psInsert = conexion.prepareStatement(sqlInsert)) {
            for (Platillo p : carrito) {
                psInsert.setInt(1, idPedido);
                psInsert.setInt(2, p.getCantidad());
                psInsert.setString(3, p.getNombre());
                
                // Ejecutamos y revisamos cuántas filas se guardaron
                int filasGuardadas = psInsert.executeUpdate(); 
                
                // Si guardó 0 filas, significa que el nombre no coincide con la BD
                if (filasGuardadas == 0) {
                    throw new SQLException("¡CUIDADO! El platillo '" + p.getNombre() + "' no existe en tu base de datos tal cual está escrito. Revisa mayúsculas, espacios o si le falta una letra.");
                }
            }
        }
    }
}
