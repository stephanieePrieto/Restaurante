package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Platillo;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Platillo.
 * Centraliza las operaciones de base de datos.
 */
public class PlatilloDAO {
    
    private Connection conexion;

    // Constructor que recibe la conexión para mayor flexibilidad y control de transacciones
    public PlatilloDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Registra un nuevo platillo en la base de datos.
     * @param platillo Objeto con la información a guardar.
     * @return true si el registro fue exitoso.
     * @throws SQLException Si ocurre un error técnico.
     */
    // En PlatilloDAO.java
    public boolean registrarPlatillo(Platillo platillo) {
        String sql = "INSERT INTO platillos (nombre, descripcion, precio, categoria, imagen, esBebida, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, platillo.getNombre());
            ps.setString(2, platillo.getDescripcion());
            ps.setDouble(3, platillo.getPrecio());
            ps.setString(4, platillo.getCategoria());
            ps.setString(5, platillo.getImagen());
            ps.setBoolean(6, platillo.isEsBebida());
            ps.setString(7, "Disponible");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar: " + e.getMessage());
            return false;
        } finally {
            mysql.close();
        }
    }

    /**
     * Recupera la lista de platillos asociados a un pedido específico.
     * @param idOrden El ID del pedido/orden.
     * @return Lista de objetos Platillo con nombre y cantidad.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public List<Platillo> obtenerPlatillosPorOrden(int idOrden) throws SQLException {
        List<Platillo> listaPlatillos = new ArrayList<>();
        // Relaciona detallePedidos con platillos para traer la información completa
        String sql = "SELECT p.nombre, p.descripcion, p.precio, dp.cantidad " +
                     "FROM detallePedidos dp " +
                     "JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                     "WHERE dp.idPedido = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Platillo platillo = new Platillo();
                    platillo.setNombre(rs.getString("nombre"));
                    platillo.setDescripcion(rs.getString("descripcion"));
                    platillo.setPrecio(rs.getDouble("precio"));
                    platillo.setCantidad(rs.getInt("cantidad"));
                    
                    listaPlatillos.add(platillo);
                }
            }
        } catch (SQLException e) {
            System.err.println(">> EXCEPCIÓN TÉCNICA (Obtener por Orden): " + e.getMessage());
            throw new SQLException("Error al recuperar detalles del platillo: " + e.getMessage());
        }
        return listaPlatillos;
    }
}