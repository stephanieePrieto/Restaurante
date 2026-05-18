package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.AsistenciaDAO;
import com.mycompany.restaurante.modelo.pojo.Asistencia;
import java.io.IOException;
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
    @FXML private ComboBox<String> CmbHoraEntrada;
    @FXML private ComboBox<String> CmbHoraSalida;
    @FXML private TextField txtHorasTotales;
    @FXML private Button btnRegistrarAsistencia;

    @FXML private TableView<Asistencia> tablaAsistencia; 
    @FXML private TableColumn<Asistencia, String> columnaUsuario;
    @FXML private TableColumn<Asistencia, String> columnaEntrada;
    @FXML private TableColumn<Asistencia, String> columnaSalida;
    @FXML private TableColumn<Asistencia, String> columnaEstado;
    @FXML private TableColumn<Asistencia, String> columnaHorasTrabajadas; 

    private AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private ObservableList<Asistencia> listaAsistencias = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatosIniciales();
        actualizarTablaDesdeBD();
        
        CmbHoraEntrada.setOnAction(e -> calcularHorasAutomatico());
        CmbHoraSalida.setOnAction(e -> calcularHorasAutomatico());

        btnRegistrarAsistencia.setOnAction(this::registrarAsistencia);
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
        listaAsistencias.clear();
        listaAsistencias.addAll(asistenciaDAO.obtenerAsistenciasHoy());
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
                long horas = duracion.toHours();
                long minutos = duracion.toMinutesPart();
                txtHorasTotales.setText(String.format("%02d:%02d hrs", horas, minutos));
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
            String totales = txtHorasTotales.getText();

            // --- VALIDACIONES DE INTERFAZ ---
            if (usuario.isEmpty()) {
                throw new IllegalArgumentException("¡Falta el nombre del pingüino!");
            }
            
            if (rol == null || rol.isEmpty()) {
                throw new IllegalArgumentException("Debes seleccionar un Rol.");
            }

            if (entrada == null && salida == null) {
                throw new IllegalArgumentException("Selecciona al menos una hora para registrar.");
            }

            // Pasamos el ROL como parámetro para validar contra la BD
            if (asistenciaDAO.procesarAsistenciaCompleta(usuario, rol, entrada, salida, totales)) {
                actualizarTablaDesdeBD();
                mostrarAlertaExito("¡Pingüino listo!", "La jornada de " + usuario + " se guardó correctamente.");
                limpiarCampos();
            } else {
                // Si el DAO regresa false, es porque el rol no coincide o el usuario no existe
                mostrarAlerta("Acceso Denegado", "El usuario '" + usuario + "' no existe o no tiene el rol de '" + rol + "'.");
            }

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Datos incompletos", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Error inesperado", "Ocurrió un problema: " + e.getMessage());
            e.printStackTrace();
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
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;"); 
        
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