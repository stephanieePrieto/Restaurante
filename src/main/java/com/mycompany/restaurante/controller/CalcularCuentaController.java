package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.CuentaDAO;
import com.mycompany.restaurante.modelo.pojo.DetallePedido;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CalcularCuentaController {

    @FXML private TextField txtMesa;
    @FXML private TextField txtMesero;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtIva;
    @FXML private TextField txtTotal;
    
    // Configuración de la Tabla
    @FXML private TableView<DetallePedido> tblConsumo;
    @FXML private TableColumn<DetallePedido, String> colPlatillo;
    @FXML private TableColumn<DetallePedido, Integer> colCantidad;
    @FXML private TableColumn<DetallePedido, Double> colPrecio;
    @FXML private TableColumn<DetallePedido, Double> colSubtotal;

    private CuentaDAO dao = new CuentaDAO();

    @FXML
    private void btnGenerarTicket(ActionEvent event) {
        try {
            int idMesa = Integer.parseInt(txtMesa.getText());
            
            // 1. Obtener datos del consumo desde el DAO
            // (Asumiendo que tu DAO devuelve una lista de objetos)
            ObservableList<DetallePedido> detalles = dao.obtenerDetallesPorMesa(idMesa);
            
            if (detalles.isEmpty()) {
                mostrarAlerta("Sin Consumo", "No hay platillos registrados para la mesa " + idMesa);
                return;
            }

            // 2. Llenar la tabla
            colPlatillo.setCellValueFactory(new PropertyValueFactory<>("platillo"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
            tblConsumo.setItems(detalles);

            // 3. Hacer los cálculos
            double subtotal = detalles.stream().mapToDouble(d -> d.getSubtotal()).sum();
            double iva = subtotal * 0.16;
            double total = subtotal + iva;

            // 4. Mostrar en los TextField
            txtSubtotal.setText(String.format("%.2f", subtotal));
            txtIva.setText(String.format("%.2f", iva));
            txtTotal.setText(String.format("%.2f", total));
            
            // Aquí podrías obtener también el nombre del mesero desde el DAO
            txtMesero.setText(dao.obtenerNombreMesero(idMesa));

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingresa un número de mesa válido.");
        }
    }

    private void mostrarAlerta(String titulo, String msj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}