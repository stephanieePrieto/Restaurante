
package com.mycompany.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDAO {

    public boolean generarTicket(int idPedido) {
        // SQL para insertar un ticket asociado a un pedido
        // Asumimos que tienes una tabla 'ticket' con 'id_pedido' y 'fecha_emision'
        String sql = "INSERT INTO ticket (id_pedido, fecha_emision) VALUES (?, NOW())";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al generar el ticket en BD: " + e.getMessage());
            return false;
        }
    }
}
