package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App; 
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.dao.UsuarioDAO;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @FXML
    void clicIngresar(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            mostrarAlerta("Error", "¡Llene los campos!");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass);

        if (usuarioLogueado != null && "Gerente".equals(usuarioLogueado.getRol())) {
            mostrarAlerta("Acceso Concedido", "¡Bienvenido!");

            try {
                // Solo deja esta línea para el cambio de pantalla
                App.setRoot("RegistroPlatillo"); 

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se encontró el archivo FXML");
            }
        } else {
            mostrarAlerta("Acceso Denegado", "Credenciales incorrectas.");
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