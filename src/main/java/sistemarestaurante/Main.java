package sistemarestaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        // Aquí le decimos que cargue tu diseño chingón
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene escena = new Scene(root);
        
        // Configuramos la ventana
        escenarioPrincipal.setTitle("Pizzería CP - Acceso Restringido");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.setResizable(false); // Para que no deformen tu diseño
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        // Este comando es el que enciende JavaFX
        launch(args);
    }
}