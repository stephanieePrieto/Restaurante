package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.*;
import com.mycompany.restaurante.modelo.pojo.Pago;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegistrarPagoController {

    // IDs que deben coincidir con Scene Builder
    @FXML private TextField txtMesa;
    @FXML private TextField txtMetodo;
    @FXML private Button btnRegistrar;
    @FXML private TextField txtMonto;

    // Instancias de DAOs para la base de datos
    private CuentaDAO cuentaDAO = new CuentaDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    private MesaDAO mesaDAO = new MesaDAO();

@FXML
    private void btnRegistrarPago(ActionEvent event) {
        try {
            // Validar que los campos no estén vacíos
            if (txtMesa.getText().isEmpty() || txtMonto.getText().isEmpty() || txtMetodo.getText().isEmpty()) {
                mostrarAlerta("Error", "Por favor, rellena todos los campos.");
                return;
            }

            // 2. Obtener los datos ingresados
            int idMesa = Integer.parseInt(txtMesa.getText());
            double montoRecibido = Double.parseDouble(txtMonto.getText());
            String metodo = txtMetodo.getText();

            // 3. Consultar la deuda total de la mesa en la BD
            double totalDeuda = cuentaDAO.obtenerTotalPorMesa(idMesa);
            int idPedido = cuentaDAO.obtenerPedidoPorMesa(idMesa);

            // Validar si hay un pedido activo
            if (idPedido == 0) {
                mostrarAlerta("Atención", "No se encontró un pedido activo para la mesa " + idMesa);
                return;
            }

            // 4. Validar si el monto pagado alcanza para cubrir la deuda
            if (montoRecibido < totalDeuda) {
                mostrarAlerta("Pago Insuficiente", "El total es $" + totalDeuda + ". El monto ingresado no alcanza.");
                return;
            }

            // 5. Registrar el pago en la BD
            Pago pago = new Pago();
            pago.setTotal(totalDeuda); // Guardamos lo que se debía
            pago.setMetodo(metodo);
            pago.setIdPedido(idPedido);

            int idPago = pagoDAO.insertarPago(pago);

            if (idPago > 0) {
                // 6. Si el pago se guardó, liberamos la mesa en MySQL
                mesaDAO.liberarMesa(idMesa);
                
                double cambio = montoRecibido - totalDeuda;
                mostrarAlerta("Éxito", "Pago registrado.\nID Pago: " + idPago + "\nCambio: $" + cambio);
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo registrar el pago en la base de datos.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Revisa que ID Mesa y Monto sean números válidos.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtMesa.clear();
        txtMonto.clear();
        txtMetodo.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}