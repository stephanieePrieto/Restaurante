package sistemarestaurante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @FXML
    void clicIngresar(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        // Validar que no dejen nd en blanco
        if (user.trim().isEmpty() || pass.trim().isEmpty()) {
            mostrarAlerta("Error", "¡El Chef exige que llenes todos los datos!");
            return;
        }

        //Conectar a la BD 
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogueado = dao.validarLogin(user, pass);

        //Verificar si existe y hacer EL SALTO
        if (usuarioLogueado != null && usuarioLogueado.getRol().equals("Gerente")) {
            
            mostrarAlerta("Acceso Concedido", "¡Bienvenido a la Pizzería de Club Penguin!");
            
            // se abre la nueva pantalla
            try {
                //Cerramos la ventana del Login
                javafx.stage.Stage stageActual = (javafx.stage.Stage) txtUsuario.getScene().getWindow();
                stageActual.close();

                // esto arga el diseño de la app 
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("RegistroPlatillo.fxml"));
                javafx.stage.Stage stageNuevo = new javafx.stage.Stage();
                stageNuevo.setTitle("Pizzatron 3000 - Nueva Receta");
                stageNuevo.setScene(new javafx.scene.Scene(root));
                stageNuevo.setResizable(false);
                stageNuevo.show();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir la ventana de Registro.");
            }
           
            
        } else {
            // mensaje si la contraseña esat mal 
            mostrarAlerta("Acceso Denegado", "Credenciales incorrectas o no eres Gerente.");
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