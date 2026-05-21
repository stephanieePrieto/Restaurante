package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Pedido;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private Connection conexion;

    // Constructor vacío
    public PedidoDAO() {
        this.conexion = ConexionBD.conectar();
    }

    // Constructor con parámetro
    public PedidoDAO(Connection conexion) {
        this.conexion = conexion;
    }

    public int obtenerPedidoActivoPorMesa(int idMesa) throws SQLException {
        String sql = "SELECT idPedido FROM pedidos WHERE idMesa = ? AND estado IN ('Pendiente', 'Listo')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("idPedido");
            }
        }
        return -1;
    }

    public int crearNuevoPedido(int idMesa, int idEmpleado) throws SQLException {
        String sql = "INSERT INTO pedidos (idMesa, idEmpleado, estado) VALUES (?, ?, 'Pendiente')";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idMesa);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Error al crear el pedido.");
    }

    public void guardarDetallesPedido(int idPedido, List<Platillo> carrito) throws SQLException {
        // Ponemos el estado en Pendiente para que el Chef vea la actualización
        actualizarEstadoPedido(idPedido, "Pendiente");

        try (PreparedStatement psDelete = conexion.prepareStatement("DELETE FROM detallepedidos WHERE idPedido = ?")) {
            psDelete.setInt(1, idPedido);
            psDelete.executeUpdate();
        }

        String sqlInsert = "INSERT INTO detallepedidos (idPedido, idPlatillo, cantidad) " +
                           "SELECT ?, idPlatillo, ? FROM platillos WHERE nombre = ?";
                           
        try (PreparedStatement psInsert = conexion.prepareStatement(sqlInsert)) {
            for (Platillo p : carrito) {
                psInsert.setInt(1, idPedido);
                psInsert.setInt(2, p.getCantidad());
                psInsert.setString(3, p.getNombre());
                if (psInsert.executeUpdate() == 0) {
                    throw new SQLException("El platillo '" + p.getNombre() + "' no existe en la BD.");
                }
            }
        }
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
    
    

    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE idPedido = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        }
    }
    
    public int obtenerIdPedidoActivo(int idMesa) {
        int idPedido = -1;
        // Buscamos el pedido cuya mesa coincida y que NO esté pagado
        String sql = "SELECT idPedido FROM pedidos WHERE idMesa = ? AND estado != 'Pagado' " +
                     "ORDER BY fechaHora DESC LIMIT 1";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idPedido = rs.getInt("idPedido");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido activo: " + e.getMessage());
        }
        return idPedido;
    }

}