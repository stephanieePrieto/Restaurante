package com.mycompany.restaurante.dao;
import java.sql.*;

public class MesaDAO {

    public void liberarMesa(int idMesa) {

        try {
            Connection con = ConexionBD.conectar();

            String sql = "UPDATE mesa SET estado='Libre' WHERE idMesa=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idMesa);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error al actualizar mesa");
        }
    }
}