package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.utils.ConexionBD;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AsignarMesaController implements Initializable {

    @FXML private GridPane gridMesas;
    @FXML private Label lblMesaSeleccionada;
    @FXML private TextField txtNombreCliente;
    @FXML private Spinner<Integer> spPersonas;
    @FXML private ComboBox<String> cbEstadoMesa;

    private int idMesaSeleccionada = -1;
    private int numeroMesaSeleccionada = -1;
    private final String RUTA_PINGUINO = "/img/pinguinomesa.png";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spPersonas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));
        cbEstadoMesa.setItems(FXCollections.observableArrayList("Libre", "Ocupada"));
        cbEstadoMesa.setValue("Ocupada");
        actualizarMapaMesas();
    }

    public void actualizarMapaMesas() {
        gridMesas.getChildren().clear();
        String sql = "SELECT idMesa, numero, estado FROM mesa ORDER BY numero ASC";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int col = 0, row = 0;
            while (rs.next()) {
                int id = rs.getInt("idMesa");
                int num = rs.getInt("numero");
                String estado = rs.getString("estado");

                Button mesaBtn = new Button();
                mesaBtn.setPrefSize(140, 120);

                if (estado.equalsIgnoreCase("Libre")) {
                    mesaBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
                    mesaBtn.setText("Mesa " + num + "\n(LIBRE)");
                    mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                    mesaBtn.setOnAction(e -> seleccionarMesa(id, num, "Libre"));

                } else if (estado.equalsIgnoreCase("Ocupada")) {
                    mesaBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
                    
                    try {
                        URL imageUrl = getClass().getResource(RUTA_PINGUINO);
                        if (imageUrl != null) {
                            Image img = new Image(imageUrl.toString());
                            ImageView view = new ImageView(img);
                            view.setFitHeight(80); 
                            view.setFitWidth(80);
                            view.setPreserveRatio(true);

                            VBox content = new VBox(2);
                            content.setAlignment(Pos.CENTER);
                            Label lblMesa = new Label("Mesa " + num);
                            lblMesa.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            
                            content.getChildren().addAll(view, lblMesa);
                            mesaBtn.setGraphic(content);
                            mesaBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        } else {
                            mesaBtn.setText("Mesa " + num + "\n(OCUPADA)");
                            mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                        }
                    } catch (Exception ex) {
                        mesaBtn.setText("Mesa " + num + "\n(OCUPADA)");
                        mesaBtn.setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                    mesaBtn.setOnAction(e -> seleccionarMesa(id, num, "Ocupada"));
                }

                gridMesas.add(mesaBtn, col, row);
                col++; 
                if (col > 3) { col = 0; row++; }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void seleccionarMesa(int idMesa, int numMesa, String estadoActual) {
        this.idMesaSeleccionada = idMesa;
        this.numeroMesaSeleccionada = numMesa;
        lblMesaSeleccionada.setText("Mesa " + numMesa + " (" + estadoActual.toUpperCase() + ")");
        cbEstadoMesa.setValue(estadoActual.equalsIgnoreCase("Ocupada") ? "Ocupada" : "Libre");
    }

    @FXML
    private void asignarMesaActual(ActionEvent event) {
        if (idMesaSeleccionada == -1) {
            mostrarAlerta("Atención", "Selecciona una mesa primero.");
            return;
        }

        String nuevoEstado = cbEstadoMesa.getValue();
        String nombreCliente = txtNombreCliente.getText().trim();

        if (nuevoEstado.equals("Ocupada") && nombreCliente.isEmpty()) {
            mostrarAlerta("Datos faltantes", "Escribe el nombre del cliente.");
            return;
        }

        String sql = "UPDATE mesa SET estado = ? WHERE idMesa = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesaSeleccionada);
            ps.executeUpdate();

            mostrarAlerta("Éxito", "Mesa " + numeroMesaSeleccionada + " actualizada.");
            limpiarFormulario();
            actualizarMapaMesas();

        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void liberarTodasLasMesas(ActionEvent event) {
        try (Connection con = ConexionBD.conectar();
             Statement st = con.createStatement()) {
            st.executeUpdate("UPDATE mesa SET estado = 'Libre'");
            actualizarMapaMesas();
            limpiarFormulario();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // CONEXIÓN DE REGRESO AL DASHBOARD
    @FXML
    private void handleRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = App.getFXMLLoader("Dashboard");
            Parent root = loader.load();
            
            // Forzamos al Dashboard a cargarse con los permisos del usuario activo
            DashboardController dc = loader.getController();
            dc.configurarUsuario(App.usuarioLogueado);

            Stage stage = (Stage) gridMesas.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Recepción");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void limpiarFormulario() {
        idMesaSeleccionada = -1;
        lblMesaSeleccionada.setText("Ninguna mesa seleccionada");
        txtNombreCliente.clear();
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m);
        a.showAndWait();
    }
    
        @FXML
    void volverDashboard(ActionEvent event) {
        try {
            Parent root = App.getFXMLLoader("Dashboard").load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}