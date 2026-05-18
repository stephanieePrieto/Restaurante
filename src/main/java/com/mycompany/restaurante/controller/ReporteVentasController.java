package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.modelo.pojo.ProductoMasVendido;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.modelo.pojo.VentaReporte;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import java.net.URL;
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
        // Vinculación de la tabla de ingresos
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Vinculación de la tabla de productos más vendidos
        colPlatillo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadVendida"));
        
        listaVentas = FXCollections.observableArrayList();
        listaProductos = FXCollections.observableArrayList();
        
        tblVentas.setItems(listaVentas);
        tblProductos.setItems(listaProductos);
    }

    // --- ACCIÓN: BUSCAR POR RANGO DE FECHAS ---
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

    // --- ACCIÓN: REPORTE MENSUAL (Mes Actual) ---
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
        
        ejecutarConsultas(sqlIngresos, sqlProductos, null, null);
    }

    // --- PROCESADOR DE CONSULTAS BASE DE DATOS ---
    private void ejecutarConsultas(String sqlIngresos, String sqlProductos, String inicio, String fin) {
        listaVentas.clear();
        listaProductos.clear();
        double totalPeriodo = 0;
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection()) {
            
            // 1. Obtener ingresos económicos
            try (PreparedStatement ps1 = con.prepareStatement(sqlIngresos)) {
                if (inicio != null && fin != null) {
                    ps1.setString(1, inicio);
                    ps1.setString(2, fin);
                }
                try (ResultSet rs1 = ps1.executeQuery()) {
                    while (rs1.next()) {
                        double suma = rs1.getDouble("sumaTotal");
                        // Sirve tanto para columna 'dia' como para columna 'mes'
                        String tiempo = (inicio != null) ? rs1.getString("dia") : rs1.getString("mes");
                        listaVentas.add(new VentaReporte(tiempo, suma));
                        totalPeriodo += suma;
                    }
                }
            }
            
            // 2. Obtener ranking de productos más vendidos
            try (PreparedStatement ps2 = con.prepareStatement(sqlProductos)) {
                if (inicio != null && fin != null) {
                    ps2.setString(1, inicio);
                    ps2.setString(2, fin);
                }
                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        listaProductos.add(new ProductoMasVendido(rs2.getString("nombre"), rs2.getInt("totalVendido")));
                    }
                }
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error de SQL", "Fallo en la BD: " + e.getMessage());
        } finally {
            mysql.close();
        }
        
        lblTotalPeriodo.setText("$" + String.format("%.2f", totalPeriodo));
    }

    // --- ACCIÓN: REGRESAR AL DASHBOARD ---
    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            URL url = App.class.getResource("/fxml/Dashboard.fxml");
            if (url == null) {
                url = App.class.getResource("Dashboard.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Mantenemos la sesión simulada regresando con el rol correspondiente
            DashboardController controller = loader.getController();
            Usuario admin = new Usuario();
            admin.setRol("Gerente");
            controller.configurarUsuario(admin);

            Node nodoOrigen = (Node) event.getSource();
            Scene escenaActual = nodoOrigen.getScene();
            escenaActual.setRoot(root);

        } catch (Exception ex) {
            mostrarAlerta("Error de navegación", "No se pudo cargar el Dashboard.");
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