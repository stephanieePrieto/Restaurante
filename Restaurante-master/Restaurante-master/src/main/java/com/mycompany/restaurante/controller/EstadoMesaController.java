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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.mycompany.restaurante.dao.MesaDAO;
import com.mycompany.restaurante.modelo.pojo.Mesa;

public class EstadoMesaController implements Initializable {

    @FXML
    private TableView<Mesa> tvMesas;

    @FXML
    private TableColumn<Mesa, Integer> colNumero;

    @FXML
    private TableColumn<Mesa, String> colEstado;

    @FXML
    private TableColumn<Mesa, String> colDetalles;

    @FXML
    private Label lblMesaSeleccionada;

    @FXML
    private Button btnLiberar;

    @FXML
    private Button btnVolver;

    private final MesaDAO mesaDAO = new MesaDAO();

    private final ObservableList<Mesa> listaObservable =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNumero.setCellValueFactory(
                new PropertyValueFactory<>("idMesa"));

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        colDetalles.setCellValueFactory(
                new PropertyValueFactory<>("detalles"));

        // COLOR ESTADOS
        colEstado.setCellFactory(column -> new TableCell<Mesa, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText(null);
                    setStyle("");

                } else {

                    setText(item);

                    if (item.equalsIgnoreCase("Ocupada")) {

                        setStyle(
                                "-fx-background-color: #C82333;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;"
                        );

                    } else if (item.equalsIgnoreCase("Libre")) {

                        setStyle(
                                "-fx-background-color: #218838;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;"
                        );

                    } else {

                        setStyle("");
                    }
                }
            }
        });

        refrescarTabla();

        tvMesas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, vieja, nueva) -> {

                    if (nueva != null) {

                        lblMesaSeleccionada.setText(
                                "Mesa " + nueva.getIdMesa());

                        if (nueva.getEstado()
                                .equalsIgnoreCase("Libre")) {

                            btnLiberar.setDisable(true);

                        } else {

                            btnLiberar.setDisable(false);
                        }
                    }
                });
    }

    private void refrescarTabla() {

        listaObservable.clear();

        listaObservable.addAll(mesaDAO.listarMesas());

        tvMesas.setItems(listaObservable);
    }

    @FXML
    private void btnLiberarMesaAction(ActionEvent event) {

        Mesa mesaSeleccionada =
                tvMesas.getSelectionModel().getSelectedItem();

        if (mesaSeleccionada != null) {

            mesaDAO.liberarMesa(
                    mesaSeleccionada.getIdMesa());

            refrescarTabla();

            lblMesaSeleccionada.setText("[ Ninguna ]");
        }
    }

    @FXML
    private void volverDashboard(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/Dashboard.fxml"));

            Parent root = loader.load();

            Stage nuevoStage = new Stage();

            nuevoStage.setScene(new Scene(root));

            nuevoStage.setTitle("Dashboard");

            nuevoStage.show();

            Stage actual = (Stage)
                    ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            actual.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}