package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.AsistenciaDAO;
import com.mycompany.restaurante.modelo.pojo.Asistencia;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.io.IOException;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AsistenciaController {

    @FXML private TextField lblUsuario; 
    @FXML private ComboBox<String> CmbRol;
    @FXML private ComboBox<String> CmbHoraEntrada, CmbHoraSalida;
    @FXML private TextField txtHorasTotales;
    @FXML private Button btnRegistrarAsistencia;

    @FXML private TableView<Asistencia> tablaAsistencia; 
    @FXML private TableColumn<Asistencia, String> columnaUsuario, columnaEntrada, columnaSalida, columnaEstado, columnaHorasTrabajadas; 

    private AsistenciaDAO asistenciaDAO;
    private ObservableList<Asistencia> listaAsistencias = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // Inicializamos el DAO de forma simple ya que el DAO maneja su propia conexión interna
        asistenciaDAO = new AsistenciaDAO(); 

        configurarTabla();
        cargarDatosIniciales();
        actualizarTablaDesdeBD(); // Cargamos los datos de la tabla al iniciar

        // Listeners para que el cálculo sea automático al cambiar el ComboBox
        CmbHoraEntrada.setOnAction(e -> calcularHorasAutomatico());
        CmbHoraSalida.setOnAction(e -> calcularHorasAutomatico());
    }

    private void configurarTabla() {
        columnaUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        columnaEntrada.setCellValueFactory(new PropertyValueFactory<>("entrada"));
        columnaSalida.setCellValueFactory(new PropertyValueFactory<>("salida"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        columnaHorasTrabajadas.setCellValueFactory(new PropertyValueFactory<>("horasTrabajadas"));
        tablaAsistencia.setItems(listaAsistencias);
    }
    
    private void actualizarTablaDesdeBD() {
        if (asistenciaDAO != null) {
            listaAsistencias.setAll(asistenciaDAO.obtenerAsistenciasHoy());
        }
    }

    private void cargarDatosIniciales() {
        CmbRol.getItems().addAll("Mesero", "Cajero", "Gerente", "Chef", "Recepcionista");
        LocalTime tiempo = LocalTime.of(7, 0);
        while (tiempo.isBefore(LocalTime.of(23, 1))) {
            String hora = tiempo.format(formatter);
            CmbHoraEntrada.getItems().add(hora);
            CmbHoraSalida.getItems().add(hora);
            tiempo = tiempo.plusMinutes(30);
        }
    }

    private void calcularHorasAutomatico() {
        String entradaStr = CmbHoraEntrada.getValue();
        String salidaStr = CmbHoraSalida.getValue();

        if (entradaStr != null && salidaStr != null) {
            LocalTime entrada = LocalTime.parse(entradaStr, formatter);
            LocalTime salida = LocalTime.parse(salidaStr, formatter);

            if (salida.isAfter(entrada)) {
                Duration duracion = Duration.between(entrada, salida);
                txtHorasTotales.setText(String.format("%02d:%02d hrs", duracion.toHours(), duracion.toMinutesPart()));
            } else {
                txtHorasTotales.setText("00:00 hrs");
            }
        }
    }

    @FXML
    private void registrarAsistencia(ActionEvent event) {
        try {
            String usuario = lblUsuario.getText().trim();
            String rol = CmbRol.getValue(); 
            String entrada = CmbHoraEntrada.getValue();
            String salida = CmbHoraSalida.getValue();

            if (usuario.isEmpty() || rol == null || (entrada == null && salida == null)) {
                mostrarAlerta("Campos incompletos", "Por favor completa el nombre, rol y al menos una hora.");
                return;
            }

            if (asistenciaDAO.procesarAsistenciaCompleta(usuario, rol, entrada, salida, txtHorasTotales.getText())) {
                actualizarTablaDesdeBD();
                mostrarAlertaExito("Registro Exitoso", "Asistencia de " + usuario + " guardada.");
                limpiarCampos();
            } else {
                mostrarAlerta("Error de Validación", "El usuario no existe o el rol es incorrecto.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al procesar: " + e.getMessage());
        }
    }

    @FXML
    private void volverDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = App.getFXMLLoader("Dashboard");
            Parent root = loader.load();
            
            // PASO DE SEGURIDAD: Re-configurar permisos al volver
            DashboardController dashCtrl = loader.getController();
            dashCtrl.configurarUsuario(App.usuarioLogueado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Panel Principal");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        lblUsuario.clear();
        CmbRol.getSelectionModel().clearSelection();
        CmbHoraEntrada.getSelectionModel().clearSelection();
        CmbHoraSalida.getSelectionModel().clearSelection();
        txtHorasTotales.setText("00:00 hrs");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); 
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
            @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            // 1. Cargamos el FXML de tu Login usando tu clase App
            // Asegúrate de que el nombre coincida exactamente con tu archivo Login.fxml (sin la extensión)
            FXMLLoader loader = App.getFXMLLoader("Login"); 
            Parent root = loader.load();

            // 2. Obtenemos la ventana (Stage) actual a partir del botón que se presionó
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Cambiamos la escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - Restaurante"); // Puedes poner el título que prefieras
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error al intentar volver al Login: " + ex.getMessage());
        }
    }
}

