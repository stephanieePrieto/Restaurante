package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.modelo.pojo.VentaReporte;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class ReporteVentasController {

    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TableView<VentaReporte> tblVentas;
    @FXML private TableColumn<VentaReporte, String> colFecha;
    @FXML private TableColumn<VentaReporte, Double> colTotal;
    @FXML private Label lblTotalPeriodo;

    private ObservableList<VentaReporte> listaVentas;

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        listaVentas = FXCollections.observableArrayList();
        tblVentas.setItems(listaVentas);
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        if (inicio == null || fin == null) {
            mostrarAlerta("Campos vacíos", "Por favor selecciona una fecha de inicio y fin.");
            return;
        }

        String sql = "SELECT DATE(fecha) as dia, SUM(total) as sumaTotal FROM pagos " +
                     "WHERE DATE(fecha) BETWEEN ? AND ? GROUP BY DATE(fecha) ORDER BY DATE(fecha) DESC";
        
        ejecutarConsulta(sql, inicio.toString(), fin.toString());
    }

    @FXML
    private void clicGenerarMensual(ActionEvent event) {
        String sql = "SELECT DATE_FORMAT(fecha, '%Y-%m') as mes, SUM(total) as sumaTotal FROM pagos " +
                     "WHERE MONTH(fecha) = MONTH(CURRENT_DATE()) AND YEAR(fecha) = YEAR(CURRENT_DATE()) " +
                     "GROUP BY DATE_FORMAT(fecha, '%Y-%m')";
        
        ejecutarConsultaMensual(sql);
    }

    private void ejecutarConsulta(String sql, String inicio, String fin) {
        listaVentas.clear();
        double totalPeriodo = 0;
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, inicio);
            ps.setString(2, fin);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dia = rs.getString("dia");
                    double suma = rs.getDouble("sumaTotal");
                    listaVentas.add(new VentaReporte(dia, suma));
                    totalPeriodo += suma;
                }
            }
        } catch (SQLException e) {
            // AQUI ESTÁ LA MAGIA: Te dirá exactamente qué falla en MySQL
            mostrarAlerta("Error de SQL", "Fallo en la BD: " + e.getMessage());
        } finally {
            mysql.close();
        }
        lblTotalPeriodo.setText(String.format("%.2f", totalPeriodo));
    }

    private void ejecutarConsultaMensual(String sql) {
        listaVentas.clear();
        double totalPeriodo = 0;
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String mes = rs.getString("mes");
                double suma = rs.getDouble("sumaTotal");
                listaVentas.add(new VentaReporte(mes, suma));
                totalPeriodo += suma;
            }
        } catch (SQLException e) {
            // AQUI ESTÁ LA MAGIA: Te dirá exactamente qué falla en MySQL
            mostrarAlerta("Error de SQL", "Fallo en la BD: " + e.getMessage());
        } finally {
            mysql.close();
        }
        lblTotalPeriodo.setText(String.format("%.2f", totalPeriodo));
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            java.net.URL url = com.mycompany.restaurante.App.class.getResource("/fxml/Dashboard.fxml");
            if (url == null) {
                url = com.mycompany.restaurante.App.class.getResource("Dashboard.fxml");
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
            javafx.scene.Parent root = loader.load();

            // Le decimos al Dashboard que somos el Gerente para que vuelva a mostrar tus botones
            DashboardController controller = loader.getController();
            Usuario admin = new Usuario();
            admin.setRol("Gerente");
            controller.configurarUsuario(admin);

            javafx.scene.Node nodoOrigen = (javafx.scene.Node) event.getSource();
            javafx.scene.Scene escenaActual = nodoOrigen.getScene();
            escenaActual.setRoot(root);

        } catch (Exception ex) {
            System.out.println("Error al volver al Dashboard.");
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}