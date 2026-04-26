package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.modelo.pojo.ServicioPago;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class ControlRegistrarPago {

    @FXML
    private TextField txtIdMesa;

    @FXML
    private TextField txtMonto;

    @FXML
    private TextField txtMetodo;

    private ServicioPago servicio = new ServicioPago();

    @FXML
    private void registrarPago() {
        try {
            int idMesa = Integer.parseInt(txtIdMesa.getText());
            double monto = Double.parseDouble(txtMonto.getText());
            String metodo = txtMetodo.getText();

            boolean resultado = servicio.registrarPago(idMesa, monto, metodo);

            if (resultado) {
                mostrarAlerta("Éxito", "Pago registrado correctamente");
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Monto insuficiente o error en BD");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Datos inválidos");
        }
    }

    private void limpiarCampos() {
        txtIdMesa.clear();
        txtMonto.clear();
        txtMetodo.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}