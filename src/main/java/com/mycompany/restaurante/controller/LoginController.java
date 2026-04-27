package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App; 
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.dao.UsuarioDAO;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

        // 1. Validación de campos vacíos
        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            mostrarAlerta("Campos requeridos", "Por favor, ingrese su usuario y contraseña.");
            return;
        }

        // 2. Validación contra la base de datos mediante el DAO 
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass);

        if (usuarioLogueado != null) {
            // El inicio de sesión es exitoso para cualquiera de los roles permitidos 
            entrarAlDashboard(usuarioLogueado);
        } else {
            // Si el objeto es null, las credenciales no coinciden en la BD
            mostrarAlerta("Acceso Denegado", "Usuario o contraseña incorrectos.");
        }
    }

private void entrarAlDashboard(Usuario usuario) {
    try {
        String fxmlParaCargar;
        
        // 1. Decidimos qué pantalla cargar según el rol
        if (usuario.getRol().equalsIgnoreCase("Chef")) {
            fxmlParaCargar = "PantallaVerOrdenesPendientes"; // Nombre de tu FXML para el Chef
        } else {
            fxmlParaCargar = "Dashboard"; 
        }

        // 2. Cargamos el FXML correspondiente
        FXMLLoader loader = App.getFXMLLoader(fxmlParaCargar);
        Parent root = loader.load();

        // 3. Configuramos el controlador según la pantalla cargada
        if (fxmlParaCargar.equals("Dashboard")) {
            DashboardController dashboardCtrl = loader.getController();
            dashboardCtrl.configurarUsuario(usuario);
        } else if (fxmlParaCargar.equals("PantallaVerOrdenesPendientes")) {
            PantallaCocinaController cocinaCtrl = loader.getController();
            // Aquí podrías pasarle datos al chef si fuera necesario, ej:
            // cocinaCtrl.setNombreChef(usuario.getNombre());
        }

        // 4. Cambiamos la escena en la ventana actual
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        mostrarAlerta("Error de Navegación", "No se pudo cargar la pantalla: " + e.getMessage());
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