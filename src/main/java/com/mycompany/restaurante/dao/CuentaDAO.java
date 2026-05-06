package com.mycompany.restaurante.dao;
import com.mycompany.restaurante.modelo.pojo.DetallePedido;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class CuentaDAO {

    public double obtenerTotalPorMesa(int idMesa) {
        double total = 0;

        String sql = "SELECT SUM(p.precio * d.cantidad) AS total " +
                     "FROM pedidos pe " +
                     "JOIN detallepedidos d ON pe.idPedido = d.idPedido " +
                     "JOIN platillos p ON d.idPlatillo = p.idPlatillo " +
                     "WHERE pe.idMesa = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return total;
    }

    public int obtenerPedidoPorMesa(int idMesa) {
        int idPedido = 0;

        String sql = "SELECT idPedido FROM pedidos WHERE idMesa=?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idPedido = rs.getInt("idPedido");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return idPedido;
    }
    
    // Método para obtener la lista de lo que se consumió (para la tabla)
public ObservableList<DetallePedido> obtenerDetallesPorMesa(int idMesa) {
    ObservableList<DetallePedido> lista = FXCollections.observableArrayList();
    // Este SQL une tus tablas de pedidos y platillos
    String sql = "SELECT p.nombre, dp.cantidad, p.precio, (dp.cantidad * p.precio) AS subtotal " +
                 "FROM detalle_pedido dp " +
                 "JOIN platillo p ON dp.id_platillo = p.id_platillo " +
                 "JOIN pedido pe ON dp.id_pedido = pe.id_pedido " +
                 "WHERE pe.id_mesa = ? AND pe.estado = 'pendiente'";

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new DetallePedido(
                rs.getString("nombre"),
                rs.getInt("cantidad"),
                rs.getDouble("precio"),
                rs.getDouble("subtotal")
            ));
        }
    } catch (Exception e) {
        System.err.println("Error al obtener detalles: " + e.getMessage());
    }
    return lista;
}

// Método para obtener el nombre del mesero (para el cuadrito de texto)
public String obtenerNombreMesero(int idMesa) {
    String nombre = "No asignado";
    String sql = "SELECT m.nombre FROM mesero m " +
                 "JOIN pedido p ON m.id_mesero = p.id_mesero " +
                 "WHERE p.id_mesa = ? AND p.estado = 'pendiente' LIMIT 1";

    try (Connection con = ConexionBD.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            nombre = rs.getString("nombre");
        }
    } catch (Exception e) {
        System.err.println("Error al obtener mesero: " + e.getMessage());
    }
    return nombre;
}
}