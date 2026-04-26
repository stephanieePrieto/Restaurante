package com.mycompany.restaurante.dao;
import java.sql.*;

public class CuentaDAO {

    public double obtenerTotalPorMesa(int idMesa) {

        double total = 0;

        try {
            Connection con = ConexionBD.conectar();

            String sql = "SELECT SUM(subtotal) as total " +
                         "FROM detallepedidos dp " +
                         "JOIN pedidos p ON dp.idPedido = p.idPedido " +
                         "WHERE p.idMesa = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idMesa);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (Exception e) {
            System.out.println("Error al obtener total");
        }

        return total;
    }
    
    public int obtenerPedidoPorMesa(int idMesa) {

    int idPedido = 0;

    try {
        
        Connection con = ConexionBD.conectar();

        String sql = "SELECT idPedido FROM pedidos WHERE idMesa=? LIMIT 1";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idMesa);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idPedido = rs.getInt("idPedido");
        }

    } catch (Exception e) {
        System.out.println("Error al obtener pedido");
    }

    return idPedido;
}
}