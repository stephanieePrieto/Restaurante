package com.mycompany.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal unificada. 
 * Se encarga de iniciar la aplicación y gestionar el cambio de pantallas.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Configuramos la escena inicial con el Login
        // El tamaño 1024x768 se mantiene como base de tu diseño
        Parent root = loadFXML("Login"); 
        scene = new Scene(root, 1024, 768);
        
        stage.setTitle("Sistema Restaurante CP - Acceso");
        stage.setScene(scene);
        
        // Evitamos que se deforme el diseño de la UI
        stage.setResizable(false); 
        stage.show();
    }

    /**
     * Permite cambiar de pantalla desde cualquier controlador.
     * Ejemplo: App.setRoot("RegistroPlatillo");
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Localiza y carga archivos FXML.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Intenta buscar en la carpeta raíz de recursos o en /fxml/
        String path = "/fxml/" + fxml + ".fxml";
        var resource = App.class.getResource(path);
        
        // Si no lo encuentra en /fxml/, busca en el paquete actual
        if (resource == null) {
            resource = App.class.getResource(fxml + ".fxml");
        }

        if (resource == null) {
            throw new IOException("No se pudo encontrar el archivo FXML: " + fxml);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

    /**
     * Punto de entrada principal.
     */
    public static void main(String[] args) {
        launch(args);
    }
}