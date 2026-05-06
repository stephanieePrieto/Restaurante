package com.mycompany.restaurante.dao;
import com.mycompany.restaurante.modelo.pojo.Pago;
import java.sql.*;


public class PagoDAO {
    
    public int insertarPago(Pago pago) {
    String sql = "INSERT INTO pagos (total, metodoPago, idPedido) VALUES (?, ?, ?)";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setDouble(1, pago.getTotal());
        ps.setString(2, pago.getMetodo());
        ps.setInt(3, pago.getIdPedido());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); // id PagoAsegurado 
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1; // error
}
}

