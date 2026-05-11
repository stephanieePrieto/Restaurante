package com.mycompany.restaurante.dao;
import com.mycompany.restaurante.modelo.pojo.DetalleFactura;
import com.mycompany.restaurante.modelo.pojo.DetallePedido;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class CuentaDAO {

// 1. Corregido: Ahora solo suma el total del pedido que está 'Pendiente'
    public double obtenerTotalPorMesa(int idMesa) {
        double total = 0;
        String sql = "SELECT SUM(p.precio * d.cantidad) AS total " +
                     "FROM pedidos pe " +
                     "JOIN detallepedidos d ON pe.idPedido = d.idPedido " +
                     "JOIN platillos p ON d.idPlatillo = p.idPlatillo " +
                     "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

        try (Connection conn = ConexionBD.conectar(); // Usa el método que tengas en tu clase Conexion
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
    String sql = "SELECT idPedido FROM pedidos WHERE idMesa = ? AND estado = 'Pendiente' ORDER BY idPedido DESC LIMIT 1";

    // Instanciamos tu clase MySQLConnect
    MySQLConnect mysql = new MySQLConnect(); 
    
    // Usamos el método .connection() que me mostraste
    try (Connection conn = mysql.connection(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idPedido = rs.getInt("idPedido");
            System.out.println("¡Lo encontramos! ID: " + idPedido);
        } else {
            // Este mensaje saldrá en la consola de NetBeans si no hay match
            System.out.println("No hay pedidos 'Pendiente' para la mesa " + idMesa);
        }

    } catch (SQLException e) {
        System.err.println("Error al consultar: " + e.getMessage());
    } finally {
        mysql.close(); // Cerramos la conexión al terminar
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
                 "WHERE pe.id_mesa = ? AND pe.estado = 'Pendiente'";

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
    
    
    public double obtenerSubtotalMesa(int idMesa) {
    double subtotal = 0;
    String sql = "SELECT SUM(p.precio * dp.cantidad) as subtotal " +
                 "FROM detallepedidos dp " +
                 "JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                 "JOIN pedidos pe ON dp.idPedido = pe.idPedido " +
                 "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

    MySQLConnect mysql = new MySQLConnect();
    Connection con = mysql.connection();

    // VALIDACIÓN CRÍTICA:
    if (con == null) {
        System.err.println("¡Error! La conexión es nula. Revisa tus credenciales en MySQLConnect.");
        return 0.0; 
    }

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            subtotal = rs.getDouble("subtotal");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        mysql.close();
    }
    return subtotal;
}
    
   // Método para obtener el nombre del mesero 
    
    public String obtenerNombreMesero(int idMesa) {
    String nombre = "No asignado";
    String sql = "SELECT m.nombre FROM mesero m " +
                 "JOIN pedido p ON m.id_mesero = p.id_mesero " +
                 "WHERE p.id_mesa = ? AND p.estado = 'Pendiente' LIMIT 1";

   
    MySQLConnect mysql = new MySQLConnect();
    try (Connection con = mysql.connection(); 
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            nombre = rs.getString("nombre");
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener mesero: " + e.getMessage());
    } finally {
        mysql.close();
    }
    return nombre;
}
    
    public ObservableList<DetalleFactura> obtenerDetallesFactura(int idMesa) {
    ObservableList<DetalleFactura> lista = FXCollections.observableArrayList();
    String sql = "SELECT p.nombre, d.cantidad, p.precio, " +
                 "(p.precio * d.cantidad) as fila_subtotal " +
                 "FROM detallepedidos d " +
                 "JOIN platillos p ON d.idPlatillo = p.idPlatillo " +
                 "JOIN pedidos pe ON d.idPedido = pe.idPedido " +
                 "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

    MySQLConnect mysql = new MySQLConnect();
    try (Connection con = mysql.connection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idMesa);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            double sub = rs.getDouble("fila_subtotal");
            // Aquí calculamos el total de la fila (puedes sumarle IVA si gustas)
            double totalFila = sub; 

            lista.add(new DetalleFactura(
                rs.getString("nombre"),
                rs.getInt("cantidad"),
                rs.getDouble("precio"),
                sub,
                totalFila
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        mysql.close();
    }
    return lista;
}





}