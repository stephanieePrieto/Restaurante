package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.PlatilloDAO;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        // 1. Opciones sincronizadas con la pantalla del mesero
        cmbCategoria.getItems().addAll("Pizzas", "Bebidas", "Pasteles", "Extras", "Especiales");
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
            boolean esBebida = categoria.equals("Bebidas");

            // 2. Traductor: Convertimos el texto a su idCategoria para MySQL
            int idCategoria = 1; // Por defecto
            switch (categoria) {
                case "Pizzas": idCategoria = 1; break;
                case "Bebidas": idCategoria = 2; break;
                case "Pasteles": idCategoria = 3; break;
                case "Extras": idCategoria = 4; break;
                case "Especiales": idCategoria = 5; break;
            }

            // Creamos el objeto pasando el idCategoria correcto al final
            Platillo nuevo = new Platillo(0, nombre, desc, precio, categoria, "default.png", esBebida, true, idCategoria);

            // Conexión real a la Base de Datos
            MySQLConnect mysql = new MySQLConnect(); 
            Connection conexion = mysql.connection(); 

            if (conexion != null) {
                PlatilloDAO dao = new PlatilloDAO(conexion);
                
                if (dao.registrarPlatillo(nuevo)) {
                    mostrarAlerta("Éxito", "¡El platillo '" + nombre + "' fue guardado en el menú oficial!", Alert.AlertType.INFORMATION);
                    limpiarCampos();
                } else {
                    mostrarAlerta("Error", "No se pudo guardar el registro en MySQL.", Alert.AlertType.ERROR);
                }
                mysql.close(); 
            } else {
                mostrarAlerta("Error de Conexión", "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El precio debe ser un número válido (Ej. 120.50).", Alert.AlertType.ERROR);
        }
    }

@FXML
    void clicCancelar(ActionEvent event) {
        try {
            // 1. Cargamos el FXML del Dashboard usando tu clase App
            // Asegúrate de que el nombre coincida exactamente con tu archivo Dashboard.fxml
            FXMLLoader loader = App.getFXMLLoader("Dashboard"); 
            Parent root = loader.load();

            // 2. Obtenemos la ventana (Stage) actual a partir del botón que se presionó
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Cambiamos la escena al Dashboard
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard - Sistema Restaurante");
            stage.show();

        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al intentar regresar al Dashboard: " + ex.getMessage());
            mostrarAlerta("Error", "No se pudo cargar la pantalla de inicio.", Alert.AlertType.ERROR);
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