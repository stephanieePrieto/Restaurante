package com.mycompany.restaurante.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.mycompany.restaurante.dao.MesaDAO;
import com.mycompany.restaurante.modelo.pojo.Mesa;

public class EstadoMesaController implements Initializable {

    @FXML private TableView<Mesa> tvMesas;
    @FXML private TableColumn<Mesa, Integer> colNumero; // Mostrará el ID de la mesa
    @FXML private TableColumn<Mesa, String> colEstado;
    @FXML private Label lblMesaSeleccionada;
    @FXML private Button btnLiberar;

    private MesaDAO mesaDAO = new MesaDAO();
    private ObservableList<Mesa> listaObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Vinculamos las columnas con los Getters (idMesa y estado)
        colNumero.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        refrescarTabla();

        // Listener para saber qué mesa tocó el mesero
        tvMesas.getSelectionModel().selectedItemProperty().addListener((obs, viejaSeleccion, nuevaSeleccion) -> {
            if (nuevaSeleccion != null) {
                lblMesaSeleccionada.setText("Mesa " + nuevaSeleccion.getIdMesa());
                
                // Si la mesa ya dice 'Libre', apagamos el botón
                if (nuevaSeleccion.getEstado().equalsIgnoreCase("Libre")) {
                    btnLiberar.setDisable(true);
                } else {
                    btnLiberar.setDisable(false);
                }
            }
        });
    }

    private void refrescarTabla() {
        listaObservable.clear();
        listaObservable.addAll(mesaDAO.listarMesas()); // Llama al nuevo método del DAO
        tvMesas.setItems(listaObservable);
    }

    @FXML
    private void btnLiberarMesaAction() {
        Mesa mesaSeleccionada = tvMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada != null) {
            mesaDAO.liberarMesa(mesaSeleccionada.getIdMesa());
            
            lblMesaSeleccionada.setText("[ Ninguna ]");
            refrescarTabla(); // Recarga los datos para ver el cambio en vivo
        }
    }

    @FXML
    private void volverDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            Stage nuevoStage = new Stage();
            nuevoStage.setScene(new Scene(root));
            nuevoStage.setTitle("Dashboard - Pizzatron CP");
            nuevoStage.show();

            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActual.close();
        } catch (IOException e) {
            System.out.println("Error al volver al Dashboard: " + e.getMessage());
        }
    }
}
//HOLA