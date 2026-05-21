package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.DetalleFactura;
import com.mycompany.restaurante.modelo.pojo.DetallePedido;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DetalleFacturaDAO {

    // 1. OBTENER TOTAL POR MESA
    public double obtenerTotalPorMesa(int idMesa) {
        double total = 0;
        String sql = "SELECT SUM(p.precio * d.cantidad) AS total " +
                     "FROM pedidos pe " +
                     "JOIN detallepedidos d ON pe.idPedido = d.idPedido " +
                     "JOIN platillos p ON d.idPlatillo = p.idPlatillo " +
                     "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

        MySQLConnect mysql = new MySQLConnect();
        try (Connection conn = mysql.connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.close();
        }
        return total;
    }
    
    // 2. OBTENER PEDIDO POR MESA
    public int obtenerPedidoPorMesa(int idMesa) {
        int idPedido = 0;
        String sql = "SELECT idPedido FROM pedidos WHERE idMesa = ? AND estado = 'Pendiente' ORDER BY idPedido DESC LIMIT 1";

        MySQLConnect mysql = new MySQLConnect(); 
        try (Connection conn = mysql.connection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                idPedido = rs.getInt("idPedido");
                System.out.println("¡Lo encontramos! ID: " + idPedido);
            } else {
                System.out.println("No hay pedidos 'Pendiente' para la mesa " + idMesa);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar: " + e.getMessage());
        } finally {
            mysql.close(); 
        }
        return idPedido;
    }

    // 3. OBTENER DETALLES POR MESA (MANTENIDO PARA COMPATIBILIDAD)
    public ObservableList<DetallePedido> obtenerDetallesPorMesa(int idMesa) {
        ObservableList<DetallePedido> lista = FXCollections.observableArrayList();
        String sql = "SELECT p.nombre, dp.cantidad, p.precio, (dp.cantidad * p.precio) AS subtotal " +
                     "FROM detallepedidos dp " +
                     "JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                     "JOIN pedidos pe ON dp.idPedido = pe.idPedido " +
                     "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

        MySQLConnect mysql = new MySQLConnect();
        try (Connection con = mysql.connection();
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
        } finally {
            mysql.close();
        }
        return lista;
    }
    
    // 4. OBTENER SUBTOTAL MESA
    public double obtenerSubtotalMesa(int idMesa) {
        double subtotal = 0;
        String sql = "SELECT SUM(p.precio * dp.cantidad) as subtotal " +
                     "FROM detallepedidos dp " +
                     "JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                     "JOIN pedidos pe ON dp.idPedido = pe.idPedido " +
                     "WHERE pe.idMesa = ? AND pe.estado = 'Pendiente'";

        MySQLConnect mysql = new MySQLConnect();
        Connection con = mysql.connection();

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
    
    // 5. OBTENER NOMBRE MESERO
    public String obtenerNombreMesero(int idMesa) {
        String nombre = "No asignado";
        String sql = "SELECT m.nombre FROM mesero m " +
                     "JOIN pedidos p ON m.id_mesero = p.id_mesero " +
                     "WHERE p.idMesa = ? AND p.estado = 'Pendiente' LIMIT 1";

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
    
    // 6. ADAPTADO PARA TU NUEVO FXML (Llena los 6 atributos del POJO DetalleFactura)
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

                // Mandamos los 6 parámetros exactos que requiere tu constructor de DetalleFactura
                lista.add(new DetalleFactura(
                    "90101501",               // Clave Prod/Serv del SAT
                    rs.getInt("cantidad"),    // Cantidad
                    "E48",                    // Unidad de servicio SAT
                    rs.getString("nombre"),   // Descripción / Platillo
                    rs.getDouble("precio"),   // Precio Unitario
                    sub                       // Importe total del renglón
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