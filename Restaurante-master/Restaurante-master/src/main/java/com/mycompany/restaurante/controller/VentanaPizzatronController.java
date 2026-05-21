package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.utils.ConexionBD;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaPizzatronController implements Initializable {

    @FXML private GridPane gridMesas;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHora;
    @FXML private Spinner<Integer> spPersonas;

    // VARIABLE PARA LA IMAGEN (Ruta más directa y segura)
    private final String RUTA_PINGUINO = "/img/pinguinomesa.png";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Llenar datos de reserva
        cbHora.setItems(FXCollections.observableArrayList("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"));
        spPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));
        actualizarMapaMesas();
    }

    public void actualizarMapaMesas() {
        gridMesas.getChildren().clear();
        String sql = "SELECT idMesa, numero, estado FROM mesa ORDER BY numero ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int col = 0, row = 0;
            while (rs.next()) {
                int id = rs.getInt("idMesa");
                int num = rs.getInt("numero");
                String estado = rs.getString("estado");

                Button mesaBtn = new Button("Mesa " + num);
                mesaBtn.setPrefSize(140, 120); // Un poco más grandes para que quepa el pingu

                // Configuración de visualización (Importante para integrar imagen y texto)
                mesaBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                mesaBtn.setAlignment(Pos.CENTER);

                if (estado.equalsIgnoreCase("Libre")) {
                    mesaBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
                    mesaBtn.setText("Mesa " + num + "\n(LIBRE)"); // Usamos texto simple
                    mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                    mesaBtn.setOnAction(e -> ejecutarReserva(id, num));
                    mesaBtn.setGraphic(null); 
                } else if (estado.equalsIgnoreCase("Ocupada")) {
                    // AQUÍ ESTÁ LA MAGIA CORREGIDA
                    try {
                        // CARGA SEGURA: Buscamos la imagen en /src/main/resources/img/
                        URL imageUrl = getClass().getResource(RUTA_PINGUINO);
                        if (imageUrl != null) {
                            Image img = new Image(imageUrl.toString());
                            ImageView view = new ImageView(img);
                            
                            // Ajustes de tamaño y proporción para que el pingu no se vea estirado
                            view.setFitHeight(90); 
                            view.setFitWidth(90);
                            view.setPreserveRatio(true);
                            view.setSmooth(true); // Suaviza los bordes

                            // VBox para centrar imagen y texto dentro del botón
                            VBox content = new VBox(5); // 5px de espacio
                            content.setAlignment(Pos.CENTER);
                            Label lblMesa = new Label("Mesa " + num);
                            lblMesa.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            
                            content.getChildren().addAll(view, lblMesa);
                            mesaBtn.setGraphic(content);
                            mesaBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                        } else {
                            System.out.println("Error Crítico: No se halló la imagen en: " + RUTA_PINGUINO);
                            mesaBtn.setText("Mesa " + num + "\n(RESERVADA)");
                            mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                        }
                    } catch (Exception e) {
                        System.out.println("Excepción al cargar imagen: " + e.getMessage());
                        mesaBtn.setText("Mesa " + num + "\n(RESERVADA)");
                        mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                    mesaBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
                    mesaBtn.setDisable(true); 
                } else {
                    mesaBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
                    mesaBtn.setDisable(true);
                }

                gridMesas.add(mesaBtn, col, row);
                col++; if (col > 3) { col = 0; row++; }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ejecutarReserva(int idMesa, int numMesa) {
        if (dpFecha.getValue() == null || cbHora.getValue() == null) {
            mostrarAlerta("Datos incompletos", "El pingüino necesita saber día y hora.");
            return;
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            // Actualizar la mesa seleccionada
            PreparedStatement ps = con.prepareStatement("UPDATE mesa SET estado = 'Ocupada' WHERE idMesa = ?");
            ps.setInt(1, idMesa);
            ps.executeUpdate();

            // Insertar registro en reservaciones
            PreparedStatement psRes = con.prepareStatement("INSERT INTO reservaciones (folioUnico, id_cliente, idMesa, fecha, hora, num_personas, estado) VALUES (?,?,?,?,?,?,'Confirmada')");
            psRes.setString(1, UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            psRes.setString(2, "CP001"); 
            psRes.setInt(3, idMesa);
            psRes.setString(4, dpFecha.getValue().toString());
            psRes.setString(5, cbHora.getValue());
            psRes.setInt(6, spPersonas.getValue());
            psRes.executeUpdate();

            actualizarMapaMesas(); // ¡ESTO REDIBUJA TODO Y HARÁ APARECER AL PINGÜINO!
            mostrarAlerta("¡Reserva Confirmada!", "¡La mesa " + numMesa + " ya es tuya! Mira, ya llegó tu pingüino.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleRegresar() {
        try {
            FXMLLoader loader = App.getFXMLLoader("Login");
            Parent root = loader.load();
            Stage stage = (Stage) gridMesas.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarReserva() {
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.prepareStatement("UPDATE mesa SET estado = 'Libre' WHERE estado = 'Ocupada'").executeUpdate();
            actualizarMapaMesas();
            mostrarAlerta("Sistema Reiniciado", "Todas las mesas vuelven a estar libres.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); 
        a.setTitle(t); 
        a.setHeaderText(null); 
        a.setContentText(m); 
        a.showAndWait();
    }
}