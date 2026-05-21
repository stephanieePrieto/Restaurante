package com.mycompany.restaurante.dao;

import com.mycompany.restaurante.modelo.pojo.Asistencia;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class AsistenciaDAO {
    
    // Constructor vacío para que encaje perfectamente con tu controlador actual
    public AsistenciaDAO() {
    }

    // Constructor con conexión para mantener el estándar de los otros DAOs del proyecto
    public AsistenciaDAO(Connection con) {
    }

    /**
     * Obtiene el historial completo de registros de asistencia sin filtrar por el día de hoy,
     * permitiendo visualizar todos los registros de prueba en la tabla.
     */
    public ObservableList<Asistencia> obtenerAsistenciasHoy() {
        ObservableList<Asistencia> lista = FXCollections.observableArrayList();
        MySQLConnect mysql = new MySQLConnect();
        
        String sql = "SELECT a.idAsistencia, e.usuario, a.fechaEntrada, a.fechaSalida, a.estado, a.horas_trabajadas " +
                     "FROM asistencias a INNER JOIN empleados e ON a.idEmpleado = e.idEmpleado " +
                     "ORDER BY a.fechaEntrada DESC";
                     
        try (Connection con = mysql.connection()) {
            if (con == null) throw new SQLException("No se pudo conectar a la base de datos.");
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Asistencia(
                        rs.getInt("idAsistencia"),
                        rs.getString("usuario"),
                        rs.getString("fechaEntrada"),
                        rs.getString("fechaSalida") == null ? "Pendiente" : rs.getString("fechaSalida"),
                        rs.getString("estado") == null ? "En turno" : rs.getString("estado"),
                        rs.getString("horas_trabajadas") == null ? "00:00 hrs" : rs.getString("horas_trabajadas")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerAsistenciasHoy: " + e.getMessage());
        } finally {
            mysql.close();
        }
        return lista;
    }

    /**
     * Procesa la asistencia validando que el ROL coincida con el usuario.
     * Determina de manera automática si cumplió la jornada basándose en las horas calculadas.
     */
    public boolean procesarAsistenciaCompleta(String username, String rolSeleccionado, String horaEntrada, String horaSalida, String horasTotalesTexto) {
        MySQLConnect mysql = new MySQLConnect();
        String fechaHoy = LocalDate.now().toString();
        
        // Lógica inteligente para determinar el estado final del turno
        String estadoFinal = "Incompleto";
        try {
            if (horasTotalesTexto != null && !horasTotalesTexto.isEmpty() && !horasTotalesTexto.equals("00:00 hrs")) {
                String soloNumero = horasTotalesTexto.replace(" hrs", "").split(":")[0];
                if (Integer.parseInt(soloNumero) >= 8) {
                    estadoFinal = "Cumplió";
                }
            } else {
                estadoFinal = (horaSalida != null) ? "Finalizado" : "En turno";
            }
        } catch (Exception e) {
            estadoFinal = "Error Formato";
        }

        try (Connection con = mysql.connection()) {
            if (con == null) throw new SQLException("Conexión perdida con el servidor.");

            // 1. Validar Usuario y Rol con la estructura correcta de la BD
            String sqlValidarRol = "SELECT e.idEmpleado FROM empleados e " +
                                   "INNER JOIN rol r ON e.idRol = r.idRol " +
                                   "WHERE e.usuario = ? AND r.nombre = ?";
                                   
            try (PreparedStatement psVal = con.prepareStatement(sqlValidarRol)) {
                psVal.setString(1, username);
                psVal.setString(2, rolSeleccionado);
                try (ResultSet rsVal = psVal.executeQuery()) {
                    if (!rsVal.next()) {
                        System.err.println("Validación fallida: El usuario no coincide con el rol.");
                        return false; 
                    }
                }
            }

            // 2. Revisar si ya existe registro hoy para decidir entre UPDATE o INSERT
            String sqlCheck = "SELECT idAsistencia FROM asistencias a " +
                              "INNER JOIN empleados e ON a.idEmpleado = e.idEmpleado " +
                              "WHERE e.usuario = ? AND DATE(a.fechaEntrada) = CURDATE()";
            
            int idExistente = -1;
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setString(1, username);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) idExistente = rs.getInt("idAsistencia");
                }
            }

            if (idExistente != -1) {
                // UPDATE: El usuario ya registró entrada hoy, procedemos a actualizar datos (como la salida)
                String sqlUpdate = "UPDATE asistencias SET fechaEntrada = ?, fechaSalida = ?, estado = ?, horas_trabajadas = ? WHERE idAsistencia = ?";
                try (PreparedStatement psUp = con.prepareStatement(sqlUpdate)) {
                    psUp.setString(1, fechaHoy + " " + (horaEntrada != null ? horaEntrada : "00:00") + ":00");
                    psUp.setString(2, horaSalida != null ? fechaHoy + " " + horaSalida + ":00" : null);
                    psUp.setString(3, estadoFinal);
                    psUp.setString(4, horasTotalesTexto);
                    psUp.setInt(5, idExistente);
                    return psUp.executeUpdate() > 0;
                }
            } else {
                // INSERT: Primer registro del día para este empleado
                String sqlInsert = "INSERT INTO asistencias (idEmpleado, fechaEntrada, fechaSalida, estado, horas_trabajadas) " +
                                   "VALUES ((SELECT idEmpleado FROM empleados WHERE usuario = ?), ?, ?, ?, ?)";
                try (PreparedStatement psIn = con.prepareStatement(sqlInsert)) {
                    psIn.setString(1, username);
                    psIn.setString(2, fechaHoy + " " + (horaEntrada != null ? horaEntrada : "00:00") + ":00");
                    psIn.setString(3, horaSalida != null ? fechaHoy + " " + horaSalida + ":00" : null);
                    psIn.setString(4, estadoFinal);
                    psIn.setString(5, horasTotalesTexto);
                    return psIn.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en AsistenciaDAO: " + e.getMessage());
            return false;
        } finally {
            mysql.close();
        }
    }

    /**
     * Enlace de compatibilidad requerido por las llamadas del controlador.
     */
    public boolean registrarTurnoCompleto(String username, String rol, String horaEntrada, String horaSalida, String horasTotalesTexto) {
        return procesarAsistenciaCompleta(username, rol, horaEntrada, horaSalida, horasTotalesTexto);
    }
}