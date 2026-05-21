package com.mycompany.restaurante.controller;
import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.DetalleFacturaDAO;
import com.mycompany.restaurante.dao.PagoDAO;
import com.mycompany.restaurante.dao.PedidoDAO;
import com.mycompany.restaurante.modelo.pojo.Pago;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class RegistrarPagoController implements Initializable {

    @FXML
    private TextField txtIdMesa;
    @FXML
    private TextField txtMonto;
    @FXML
    private ComboBox<String> cbMetodo;

    // Instancias de los DAO para acceder a la base de datos
    private PagoDAO pagoDAO = new PagoDAO();
    private DetalleFacturaDAO cuentaDAO = new DetalleFacturaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Llenamos el ComboBox con las opciones de pago al abrir la ventana
        cbMetodo.getItems().addAll("Efectivo", "Tarjeta de Crédito", "Transferencia");
    }

    @FXML
    private void btnRegistrarPago(ActionEvent event) {
        try {
            // 1. Validar que los campos no estén vacíos
            if (txtIdMesa.getText().isEmpty() || txtMonto.getText().isEmpty() || cbMetodo.getValue() == null) {
                mostrarAlerta("Campos vacíos", "Por favor rellena todos los campos.");
                return;
            }

            // 2. Capturar los datos
            int idMesa = Integer.parseInt(txtIdMesa.getText());
            String metodo = cbMetodo.getValue();
            double monto = Double.parseDouble(txtMonto.getText());
            
            // Obtenemos el id del pedido actual de esa mesa
            int idPedido = cuentaDAO.obtenerPedidoPorMesa(idMesa);

            if (idPedido == 0) {
                mostrarAlerta("Error", "No se encontró un pedido activo para la mesa " + idMesa);
                return;
            }

            // 3. Crear el POJO y mandarlo al DAO
            Pago nuevoPago = new Pago();
            nuevoPago.setTotal(monto);
            nuevoPago.setMetodo(metodo);
            nuevoPago.setIdPedido(idPedido);

            boolean exito = pagoDAO.registrarPago(nuevoPago, idMesa);

            // 4. Resultado
            if (exito) {
                mostrarAlerta("Éxito", "Pago registrado y mesa liberada correctamente.");
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Hubo un problema al guardar en la base de datos.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El ID de Mesa y el Monto deben ser números.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtIdMesa.clear();
        txtMonto.clear();
        cbMetodo.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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