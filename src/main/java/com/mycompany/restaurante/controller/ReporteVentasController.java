package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.modelo.pojo.ProductoMasVendido;
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
    @FXML private Label lblTotalPeriodo;
    
    // Tabla y columnas de ingresos
    @FXML private TableView<VentaReporte> tblVentas;
    @FXML private TableColumn<VentaReporte, String> colFecha;
    @FXML private TableColumn<VentaReporte, Double> colTotal;
    
    // Tabla y columnas de productos
    @FXML private TableView<ProductoMasVendido> tblProductos;
    @FXML private TableColumn<ProductoMasVendido, String> colPlatillo;
    @FXML private TableColumn<ProductoMasVendido, Integer> colCantidad;

    private ObservableList<VentaReporte> listaVentas;
    private ObservableList<ProductoMasVendido> listaProductos;

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        colPlatillo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        
        listaVentas = FXCollections.observableArrayList();
        listaProductos = FXCollections.observableArrayList();
        
        tblVentas.setItems(listaVentas);
        tblProductos.setItems(listaProductos);
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        if (inicio == null || fin == null) {
            mostrarAlerta("Campos vacíos", "Por favor selecciona una fecha de inicio y fin.");
            return;
        }

        String sqlIngresos = "SELECT DATE(fecha) as dia, SUM(total) as sumaTotal FROM pagos " +
                             "WHERE DATE(fecha) BETWEEN ? AND ? GROUP BY DATE(fecha) ORDER BY DATE(fecha) DESC";
                             
        String sqlProductos = "SELECT p.nombre, SUM(dp.cantidad) as totalVendido FROM detallepedidos dp " +
                              "INNER JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                              "INNER JOIN pagos pa ON dp.idPedido = pa.idPedido " +
                              "WHERE DATE(pa.fecha) BETWEEN ? AND ? " +
                              "GROUP BY p.nombre ORDER BY totalVendido DESC";
        
        ejecutarConsultas(sqlIngresos, sqlProductos, inicio.toString(), fin.toString());
    }

    @FXML
    private void clicGenerarMensual(ActionEvent event) {
        String sqlIngresos = "SELECT DATE_FORMAT(fecha, '%Y-%m') as mes, SUM(total) as sumaTotal FROM pagos " +
                             "WHERE MONTH(fecha) = MONTH(CURRENT_DATE()) AND YEAR(fecha) = YEAR(CURRENT_DATE()) " +
                             "GROUP BY DATE_FORMAT(fecha, '%Y-%m')";
                             
        String sqlProductos = "SELECT p.nombre, SUM(dp.cantidad) as totalVendido FROM detallepedidos dp " +
                              "INNER JOIN platillos p ON dp.idPlatillo = p.idPlatillo " +
                              "INNER JOIN pagos pa ON dp.idPedido = pa.idPedido " +
                              "WHERE MONTH(pa.fecha) = MONTH(CURRENT_DATE()) AND YEAR(pa.fecha) = YEAR(CURRENT_DATE()) " +
                              "GROUP BY p.nombre ORDER BY totalVendido DESC";
        
        ejecutarConsultasMensuales(sqlIngresos, sqlProductos);
    }

    private void ejecutarConsultas(String sqlIngresos, String sqlProductos, String inicio, String fin) {
        listaVentas.clear();
        listaProductos.clear();
        double totalPeriodo = 0;
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection()) {
            
            // 1. Obtener ingresos
            try (PreparedStatement ps1 = con.prepareStatement(sqlIngresos)) {
                ps1.setString(1, inicio);
                ps1.setString(2, fin);
                ResultSet rs1 = ps1.executeQuery();
                while (rs1.next()) {
                    double suma = rs1.getDouble("sumaTotal");
                    listaVentas.add(new VentaReporte(rs1.getString("dia"), suma));
                    totalPeriodo += suma;
                }
            }
            
            // 2. Obtener productos más vendidos
            try (PreparedStatement ps2 = con.prepareStatement(sqlProductos)) {
                ps2.setString(1, inicio);
                ps2.setString(2, fin);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    listaProductos.add(new ProductoMasVendido(rs2.getString("nombre"), rs2.getInt("totalVendido")));
                }
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error de SQL", "Fallo en la BD: " + e.getMessage());
        } finally {
            mysql.close();
        }
        lblTotalPeriodo.setText("$" + String.format("%.2f", totalPeriodo));
    }

    private void ejecutarConsultasMensuales(String sqlIngresos, String sqlProductos) {
        listaVentas.clear();
        listaProductos.clear();
        double totalPeriodo = 0;
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection()) {
            
            // 1. Obtener ingresos
            try (PreparedStatement ps1 = con.prepareStatement(sqlIngresos);
                 ResultSet rs1 = ps1.executeQuery()) {
                while (rs1.next()) {
                    double suma = rs1.getDouble("sumaTotal");
                    listaVentas.add(new VentaReporte(rs1.getString("mes"), suma));
                    totalPeriodo += suma;
                }
            }
            
            // 2. Obtener productos más vendidos
            try (PreparedStatement ps2 = con.prepareStatement(sqlProductos);
                 ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    listaProductos.add(new ProductoMasVendido(rs2.getString("nombre"), rs2.getInt("totalVendido")));
                }
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error de SQL", "Fallo en la BD: " + e.getMessage());
        } finally {
            mysql.close();
        }
        lblTotalPeriodo.setText("$" + String.format("%.2f", totalPeriodo));
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

            DashboardController controller = loader.getController();
            Usuario admin = new Usuario();
            admin.setRol("Gerente");
            controller.configurarUsuario(admin);

            javafx.scene.Node nodoOrigen = (javafx.scene.Node) event.getSource();
            javafx.scene.Scene escenaActual = nodoOrigen.getScene();
            escenaActual.setRoot(root);

        } catch (Exception ex) {
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