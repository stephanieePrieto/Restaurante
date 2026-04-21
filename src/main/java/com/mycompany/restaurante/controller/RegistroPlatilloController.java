package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.PlatilloDAO;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import com.mycompany.restaurante.modelo.sql.MySQLConnect; // IMPORTANTE: Usar tu clase real
import java.sql.Connection;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroPlatilloController {

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<String> cmbCategoria;

    @FXML
    public void initialize() {
        cmbCategoria.getItems().addAll("Plato Fuerte", "Entrada", "Postre", "Bebida");
    }

    @FXML
    void clicGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String desc = txtDescripcion.getText();
        String precioTexto = txtPrecio.getText();
        String categoria = cmbCategoria.getValue();

        if (nombre.trim().isEmpty() || precioTexto.trim().isEmpty() || categoria == null) {
            mostrarAlerta("Datos Incompletos", "⚠️ Nombre, Precio y Categoría son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);
            boolean esBebida = categoria.equalsIgnoreCase("Bebida");

            // Crear el objeto Platillo
            Platillo nuevo = new Platillo(0, nombre, desc, precio, categoria, "default.png", esBebida, true, 0);

            // --- CAMBIO CLAVE AQUÍ ---
            MySQLConnect mysql = new MySQLConnect(); // Instanciamos tu clase de conexión
            Connection conexion = mysql.connection(); // Obtenemos la conexión real

            if (conexion != null) {
                // Pasamos la conexión al DAO
                PlatilloDAO dao = new PlatilloDAO(conexion);
                
                if (dao.registrarPlatillo(nuevo)) {
                    mostrarAlerta("Éxito", "¡Platillo guardado en la base de datos!", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                } else {
                    mostrarAlerta("Error", "No se pudo guardar el registro.", Alert.AlertType.ERROR);
                }
                mysql.close(); // Siempre cerrar después de usar
            } else {
                mostrarAlerta("Error de Conexión", "No se pudo conectar a MySQL. Revisa tu contraseña.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El precio debe ser un número.", Alert.AlertType.ERROR);
        }
    }

@FXML
    void clicCancelar(ActionEvent event) {
        try {
            // Regresamos a la pantalla de Login usando la clase App
            com.mycompany.restaurante.App.setRoot("Login"); 
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al Login.", Alert.AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        cmbCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}