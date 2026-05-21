package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.DetalleFacturaDAO;
import com.mycompany.restaurante.modelo.pojo.DetallePedido;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CalcularCuentaController implements Initializable {

    @FXML private ComboBox<Integer> cbMesa;
    @FXML private ComboBox<String> cbMesero;
    @FXML private TextField txtSubtotal;
    @FXML private TextField txtIva;
    @FXML private TextField txtTotal;

    private DetalleFacturaDAO cuentaDAO = new DetalleFacturaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Llenar mesas del 1 al 10
        for (int i = 1; i <= 6; i++) {
            cbMesa.getItems().add(i);
        }

        // 2. Llenar meseros con los IDs que me pasaste (2 y 6)
        cbMesero.getItems().addAll("Juan Mesero (ID: 2)", "BegoPro (ID: 6)");
    }
    
    

    @FXML
    private void btnGenerarTicket() {
        if (cbMesa.getValue() != null) {
            int idMesa = cbMesa.getValue();
            
            // Obtener el subtotal de la base de datos
            double subtotal = cuentaDAO.obtenerSubtotalMesa(idMesa);
            
            if (subtotal > 0) {
                // Cálculos
                
                double iva = subtotal * 0.16;
                double total = subtotal + iva;
                
                
                
                // Mostrar en la pantalla (Resumen de Pago)
                txtSubtotal.setText(String.format("%.2f", subtotal));
                txtIva.setText(String.format("%.2f", iva));
                txtTotal.setText(String.format("%.2f", total));
            } else {
                mostrarAlerta("Sin Consumo", "La mesa " + idMesa + " no tiene pedidos pendientes.");
                
            }
        } else {
            mostrarAlerta("Atención", "Por favor selecciona una mesa.");
        }
    }

    private void mostrarAlerta(String titulo, String msj) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(msj);
        alert.showAndWait();
    }
        @FXML
    void volverDashboard(ActionEvent event) {
        try {
            Parent root = App.getFXMLLoader("Dashboard").load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}