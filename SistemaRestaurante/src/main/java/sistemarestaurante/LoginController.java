package sistemarestaurante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // Validar que no dejen nada en blanco
        if (user.trim().isEmpty()) {
            mostrarAlerta("Error", "¡El Chef exige que pongas tu ID!");
            return;
        }

        // 1. INTENTAR LOGIN COMO GERENTE 
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass);

        if (usuarioLogueado != null && usuarioLogueado.getRol().equals("Gerente")) {
            mostrarAlerta("Acceso Concedido", "¡Bienvenido a la Pizzería FEI, Jefe!");
            abrirVentana("RegistroPlatillo.fxml", "PIZZERIA FEI - Nueva Receta");
        } 
        // 2. SI NO ES GERENTE, CHECAMOS SI ES UN CLIENTE 
        else {
            if (verificarSiEsCliente(user)) {
                mostrarAlerta("¡Bienvenido Pingüino!", "Entrando a la zona de reservaciones...");
                abrirVentana("ventanacliente.fxml", "Pizzatron 3000 - Reservaciones");
            } else {
                mostrarAlerta("Acceso Denegado", "Credenciales incorrectas o ID de cliente no encontrado.");
            }
        }
    }

    // Método para mostrar los mensajitos en pantalla
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para cambiar de pantalla sin repetir código
    private void abrirVentana(String fxml, String titulo) {
        try {
            Stage stageActual = (Stage) txtUsuario.getScene().getWindow();
            stageActual.close();

            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stageNuevo = new Stage();
            stageNuevo.setTitle(titulo);
            stageNuevo.setScene(new Scene(root));
            stageNuevo.setResizable(false);
            stageNuevo.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + fxml);
        }
    }

    // Método que va a XAMPP a buscar al cliente
    private boolean verificarSiEsCliente(String id) {
        boolean existe = false;
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        
        // OJO AQUÍ ABAJO: Asegúrate de que "restaurante_db" sea el nombre real de tu base de datos en XAMPP
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante_db", "root", "Goku2004");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                existe = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return existe;
    }
}