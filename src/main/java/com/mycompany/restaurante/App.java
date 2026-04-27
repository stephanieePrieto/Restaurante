package com.mycompany.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("Login"); 
        scene = new Scene(root, 1024, 768);
        
        stage.setTitle("Sistema Restaurante CP - Acceso");
        stage.setScene(scene);
        stage.setResizable(false); 
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // --- NUEVO MÉTODO PARA COMUNICACIÓN ENTRE CONTROLADORES ---
    public static FXMLLoader getFXMLLoader(String fxml) throws IOException {
        String path = "/fxml/" + fxml + ".fxml";
        var resource = App.class.getResource(path);
        if (resource == null) {
            resource = App.class.getResource(fxml + ".fxml");
        }
        return new FXMLLoader(resource);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        return getFXMLLoader(fxml).load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}