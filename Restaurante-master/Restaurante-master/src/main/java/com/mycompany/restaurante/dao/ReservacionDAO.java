package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Reservacion;
import com.mycompany.restaurante.utils.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservacionDAO {
    private Connection conexion;

    public ReservacionDAO() {
        this.conexion = ConexionBD.conectar(); 
    }

    public ReservacionDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Trae todas las reservaciones uniendo la tabla clientes.
     * Con LEFT JOIN nos aseguramos de ver todo pase lo que pase.
     */
    public List<Reservacion> obtenerTodasLasReservaciones() throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.idReservacion, r.folioUnico, r.id_cliente, c.nombre AS nombre_cliente, " +
                     "r.idMesa, r.fecha, r.hora, r.num_personas, r.estado " +
                     "FROM reservaciones r " +
                     "LEFT JOIN clientes c ON r.id_cliente = c.id_cliente " +
                     "ORDER BY r.idReservacion DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reservacion r = new Reservacion(
                    rs.getInt("idReservacion"),
                    rs.getString("folioUnico"),
                    rs.getString("id_cliente"),
                    rs.getString("nombre_cliente"),
                    rs.getInt("idMesa"),
                    rs.getString("fecha"),
                    rs.getString("hora"),
                    rs.getInt("num_personas"),
                    rs.getString("estado")
                );
                lista.add(r);
            }
        }
        return lista;
    }

    /**
     * Busca un cliente por su nombre exacto. Si existe, regresa su id_cliente.
     * Si no existe, calcula el siguiente ID (CP00X), lo registra y regresa ese nuevo ID.
     */
    public String obtenerOGenerarIdCliente(String nombreCliente) throws SQLException {
        // 1. Buscar si el cliente ya existe
        String sqlBuscar = "SELECT id_cliente FROM clientes WHERE nombre = ? LIMIT 1";
        try (PreparedStatement psBuscar = conexion.prepareStatement(sqlBuscar)) {
            psBuscar.setString(1, nombreCliente);
            try (ResultSet rs = psBuscar.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id_cliente"); // Ya existe, regresamos su ID
                }
            }
        }

        // 2. Si no existe, calculamos el siguiente ID automático (ej. de CP001 pasa a CP002)
        String sqlMax = "SELECT id_cliente FROM clientes WHERE id_cliente LIKE 'CP%' ORDER BY id_cliente DESC LIMIT 1";
        String nuevoId = "CP001"; // Por defecto si la tabla estuviera limpia
        
        try (PreparedStatement psMax = conexion.prepareStatement(sqlMax);
             ResultSet rsMax = psMax.executeQuery()) {
            if (rsMax.next()) {
                String maxId = rsMax.getString("id_cliente"); // Ejemplo: "CP014"
                try {
                    int numero = Integer.parseInt(maxId.substring(2)); // Extrae el 14
                    numero++; // Incrementa a 15
                    nuevoId = String.format("CP%03d", numero); // Lo vuelve a armar como "CP015"
                } catch (Exception e) {
                    nuevoId = "CP" + String.valueOf(System.currentTimeMillis()).substring(10);
                }
            }
        }

        // 3. Insertamos al cliente nuevo en la tabla de clientes para que no truene la llave foránea
        String sqlInsertarCliente = "INSERT INTO clientes (id_cliente, nombre) VALUES (?, ?)";
        try (PreparedStatement psIns = conexion.prepareStatement(sqlInsertarCliente)) {
            psIns.setString(1, nuevoId);
            psIns.setString(2, nombreCliente);
            psIns.executeUpdate();
            System.out.println("Se registró un nuevo cliente automático: " + nombreCliente + " con ID: " + nuevoId);
        }

        return nuevoId;
    }

    /**
     * Inserta una nueva reservación obteniendo primero el ID del cliente de forma automática.
     */
    public boolean insertarReservacion(Reservacion r) throws SQLException {
        // CORRECCIÓN CLAVE: Mandamos a llamar la automatización del ID usando el nombre que viene de la caja de texto
        String idRealCliente = obtenerOGenerarIdCliente(r.getNombreCliente());
        
        String sql = "INSERT INTO reservaciones (folioUnico, id_cliente, idMesa, fecha, hora, num_personas, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, r.getFolioUnico());
            ps.setString(2, idRealCliente); // Usamos el ID recuperado o creado automáticamente
            ps.setInt(3, r.getIdMesa());
            ps.setString(4, r.getFecha());
            ps.setString(5, r.getHora()); 
            ps.setInt(6, r.getNumPersonas());
            ps.setString(7, r.getEstado());
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarReservacion(Reservacion r) throws SQLException {
        // Al modificar también actualizamos el ID si es que le cambiaron el nombre en la caja de texto
        String idRealCliente = obtenerOGenerarIdCliente(r.getNombreCliente());
        
        String sql = "UPDATE reservaciones SET id_cliente = ?, idMesa = ?, fecha = ?, hora = ?, num_personas = ? WHERE idReservacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, idRealCliente);
            ps.setInt(2, r.getIdMesa());
            ps.setString(3, r.getFecha());
            ps.setString(4, r.getHora()); 
            ps.setInt(5, r.getNumPersonas());
            ps.setInt(6, r.getIdReservacion());
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cancelarReservacion(int idReservacion) throws SQLException {
        String sql = "UPDATE reservaciones SET estado = 'Cancelada' WHERE idReservacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idReservacion);
            return ps.executeUpdate() > 0;
        }
    }
}