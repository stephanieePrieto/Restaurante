
package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.CuentaDAO;
import com.mycompany.restaurante.modelo.pojo.DetalleFactura;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class FacturaController implements Initializable {

    @FXML private ComboBox<Integer> cbMesa;
    @FXML private TableView<DetalleFactura> tvFactura;
    @FXML private TableColumn<DetalleFactura, String> colPlatillo;
    @FXML private TableColumn<DetalleFactura, Integer> colCantidad;
    @FXML private TableColumn<DetalleFactura, Double> colPrecio;
    @FXML private TableColumn<DetalleFactura, Double> colSubtotal;
    @FXML private TableColumn<DetalleFactura, Double> colTotal;

    private CuentaDAO cuentaDAO = new CuentaDAO();
    
    @FXML
    void volverDashboard(ActionEvent event) {
        try {
            Parent root = App.getFXMLLoader("Dashboard").load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Llenar el ComboBox
        for (int i = 1; i <= 10; i++) cbMesa.getItems().add(i);

        // Configurar las columnas para que sepan qué dato del POJO mostrar
        colPlatillo.setCellValueFactory(new PropertyValueFactory<>("platillo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
    Alert alerta = new Alert(Alert.AlertType.WARNING);
    alerta.setTitle(titulo);
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);
    alerta.showAndWait();
}
    
    

    @FXML
    private void btnGenerarFactura() {
        if (cbMesa.getValue() != null) {
            int idMesa = cbMesa.getValue();
            ObservableList<DetalleFactura> datos = cuentaDAO.obtenerDetallesFactura(idMesa);
            
            if (datos.isEmpty()) {
                mostrarAlerta("Sin datos", "No hay consumos pendientes para la mesa " + idMesa);
            } else {
                tvFactura.setItems(datos);
            }
        }
    }
}