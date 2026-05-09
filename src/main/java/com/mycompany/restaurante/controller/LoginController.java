package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App; 
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.dao.UsuarioDAO;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
            mostrarAlerta("Campos requeridos", "Por favor, ingrese su usuario y contraseña.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioValidado = dao.validarLogin(user, pass);

        if (usuarioValidado != null) {
            // ASIGNAMOS EL USUARIO A LA APP PARA QUE TODO EL SISTEMA SEPA QUIÉN ES
            App.usuarioLogueado = usuarioValidado; 
            entrarAlDashboard(usuarioValidado);
        } else {
            mostrarAlerta("Acceso Denegado", "Usuario o contraseña incorrectos.");
        }
    }

    private void entrarAlDashboard(Usuario usuario) {
        try {
            String fxmlParaCargar;
            if (usuario.getRol().equalsIgnoreCase("Chef")) {
                fxmlParaCargar = "PantallaVerOrdenesPendientes";
            } else {
                fxmlParaCargar = "Dashboard"; 
            }

            FXMLLoader loader = App.getFXMLLoader(fxmlParaCargar);
            Parent root = loader.load();

            if (fxmlParaCargar.equals("Dashboard")) {
                DashboardController dashboardCtrl = loader.getController();
                dashboardCtrl.configurarUsuario(usuario);
            }

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Navegación", "No se pudo cargar la pantalla.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}