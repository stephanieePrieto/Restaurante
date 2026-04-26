package com.mycompany.restaurante.dao;
import com.mycompany.restaurante.modelo.pojo.Pago;
import java.sql.*;

public class PagoDAO {

    public boolean insertarPago(Pago pago) {
        String sql = "INSERT INTO pagos (total, metodoPago, idPedido) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, pago.getMonto());
            ps.setString(2, pago.getMetodo());
            ps.setInt(3, pago.getIdPedido());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}