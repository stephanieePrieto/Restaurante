package com.mycompany.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.restaurante.modelo.pojo.Mesa;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;

public class MesaDAO {
    public List<Mesa> listarMesas() {

    List<Mesa> lista = new ArrayList<>();

    String sql =
        "SELECT m.idMesa, m.estado, " +
        "GROUP_CONCAT(pl.nombre SEPARATOR ', ') AS detalles " +
        "FROM mesa m " +
        "LEFT JOIN pedidos p ON m.idMesa = p.idMesa " +
        "LEFT JOIN detallepedidos dp ON p.idPedido = dp.idPedido " +
        "LEFT JOIN platillos pl ON dp.idPlatillo = pl.idPlatillo " +
        "GROUP BY m.idMesa, m.estado";

    MySQLConnect mysql = new MySQLConnect();

    Connection con = mysql.connection();

    if (con == null) {
        System.err.println("¡ERROR! No se pudo conectar.");
        return lista;
    }

    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            String detalles = rs.getString("detalles");

            if (detalles == null) {
                detalles = "Sin pedidos";
            }

            Mesa m = new Mesa(
                rs.getInt("idMesa"),
                rs.getString("estado"),
                detalles
            );

            lista.add(m);
        }

        System.out.println("Mesas cargadas correctamente");

    } catch (Exception e) {

        System.err.println("Error en MesaDAO.listarMesas()");
        e.printStackTrace();

    } finally {

        mysql.close();
    }

    return lista;
}

    public void liberarMesa(int idMesa) {

        String sql =
                "UPDATE mesa SET estado = 'Libre' WHERE idMesa = ?";

        MySQLConnect mysql = new MySQLConnect();

        Connection con = mysql.connection();

        if (con == null) {
            System.err.println("No se pudo conectar.");
            return;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMesa);

            ps.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            mysql.close();
        }
    }
}