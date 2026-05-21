package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.dao.UsuarioDAO;
import com.mycompany.restaurante.utils.ConexionBD;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    
    // Estas son las bolitas que vamos a leer desde la pantalla
    @FXML private RadioButton rbEmpleado;
    @FXML private RadioButton rbCliente;

    @FXML
    void clicIngresar(ActionEvent event) {
        String user = txtUsuario.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Campos requeridos", "Por favor, ingrese sus datos.");
            return;
        }

        // Si la bolita de Cliente está conectada y seleccionada, busca en la BD de clientes
        if (rbCliente != null && rbCliente.isSelected()) {
            validarClienteBD(user, pass);
        } else {
            // Si es Empleado, usa el DAO de tu equipo 4
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuarioValidado = dao.validarLogin(user, pass);

            if (usuarioValidado != null) {
                App.usuarioLogueado = usuarioValidado; 
                entrarAlDashboard(usuarioValidado);
            } else {
                mostrarAlerta("Acceso Denegado", "Usuario o contraseña de empleado incorrectos.");
            }
        }
    }

    private void validarClienteBD(String idCliente, String telefono) {
        try {
            Connection con = ConexionBD.conectar();
            String sql = "SELECT * FROM clientes WHERE id_cliente = ? AND telefono = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, idCliente);
            ps.setString(2, telefono);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("¡Acceso de Cliente exitoso!");
                FXMLLoader loader = App.getFXMLLoader("ventanacliente"); // Abre tu pantalla
                Parent root = loader.load();
                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                mostrarAlerta("Acceso Denegado", "ID de Cliente o Teléfono incorrectos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Problema al conectar a la base de datos.");
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