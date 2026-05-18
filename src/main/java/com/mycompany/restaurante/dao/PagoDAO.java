package com.mycompany.restaurante.dao;
import com.mycompany.restaurante.modelo.pojo.Pago;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;




public class PagoDAO {
    
public boolean registrarPago(Pago pago, int idMesa) {
    String sqlPago = "INSERT INTO pagos (total, metodoPago, idPedido) VALUES (?, ?, ?)";
    String sqlMesa = "UPDATE mesa SET estado = 'Libre' WHERE idMesa = ?";
    String sqlPedido = "UPDATE pedidos SET estado = 'Pagado' WHERE idPedido = ?";

    // 1. IMPORTANTE: Usamos tu nueva clase MySQLConnect
    MySQLConnect mysql = new MySQLConnect();
    Connection con = mysql.connection(); 

    // 2. Verificamos que la conexión no sea nula antes de hacer nada
    if (con == null) {
        System.err.println("¡ERROR! No se pudo establecer la conexión con MySQL.");
        return false;
    }

    try {
        con.setAutoCommit(false); // Ahora sí funcionará porque 'con' no es null

        try (PreparedStatement psPago = con.prepareStatement(sqlPago);
             PreparedStatement psMesa = con.prepareStatement(sqlMesa);
             PreparedStatement psPedido = con.prepareStatement(sqlPedido)) {
            
            // Insertar el Pago
            psPago.setDouble(1, pago.getTotal());
            psPago.setString(2, pago.getMetodo());
            psPago.setInt(3, pago.getIdPedido());
            psPago.executeUpdate();

            // Liberar la Mesa
            psMesa.setInt(1, idMesa);
            psMesa.executeUpdate();

            // Finalizar el Pedido
            psPedido.setInt(1, pago.getIdPedido());
            psPedido.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            e.printStackTrace();
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    } finally {
        mysql.close(); // Siempre cerramos la conexión al terminar
    }
}
}

