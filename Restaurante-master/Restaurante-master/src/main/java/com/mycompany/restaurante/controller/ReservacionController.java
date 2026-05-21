package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.ReservacionDAO;
import com.mycompany.restaurante.modelo.pojo.Reservacion;
import com.mycompany.restaurante.utils.ConexionBD;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReservacionController implements Initializable {

    @FXML private TextField txtCliente;
    @FXML private DatePicker dpFecha; 
    @FXML private ComboBox<String> cbHora;
    @FXML private ComboBox<Integer> cbPinguinos;
    @FXML private ComboBox<Integer> cbMesa;
    @FXML private TextField txtBuscador;

    @FXML private TableView<Reservacion> tablaReservaciones;
    @FXML private TableColumn<Reservacion, String> colCliente;
    @FXML private TableColumn<Reservacion, String> colFecha;
    @FXML private TableColumn<Reservacion, String> colHora;
    @FXML private TableColumn<Reservacion, Integer> colPinguinos;
    @FXML private TableColumn<Reservacion, Integer> colMesa;
    @FXML private TableColumn<Reservacion, String> colEstado;
    @FXML private TableColumn<Reservacion, String> colID;

    private ObservableList<Reservacion> listaReservaciones = FXCollections.observableArrayList();
    private Reservacion reservacionSeleccionada = null;
    private ReservacionDAO reservacionesDao = new ReservacionDAO();
    
    // Bandera para desactivar el listener temporalmente durante recargas de tabla
    private boolean modificandoTabla = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbHora.setItems(FXCollections.observableArrayList("13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"));
        cbPinguinos.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        cbMesa.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPinguinos.setCellValueFactory(new PropertyValueFactory<>("numPersonas"));
        colMesa.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colID.setCellValueFactory(new PropertyValueFactory<>("folioUnico"));
        // Bloquea visualmente los días pasados en el DatePicker
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Si el día evaluado es anterior a hoy, lo deshabilita y lo pinta opaco
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #dddddd;");
                }
            }
        });
        
        configurarBuscadorRealTime();
        cargarDatosTabla();

        tablaReservaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                    if (modificandoTabla) return;
                    if (newSel != null) {
                        reservacionSeleccionada = newSel;
                        txtCliente.setText(newSel.getNombreCliente());
                        dpFecha.setValue(LocalDate.parse(newSel.getFecha()));
                        if (newSel.getHora() != null && newSel.getHora().length() >= 5) {
                            cbHora.setValue(newSel.getHora().substring(0, 5));
                        }
                        cbPinguinos.setValue(newSel.getNumPersonas());
                        cbMesa.setValue(newSel.getIdMesa());
                    }
                });
            }

private void cargarDatosTabla() {
        modificandoTabla = true; // Bloqueamos el listener para evitar NullPointerException
        try {
            // 1. Traemos la lista de reservaciones actualizada desde el DAO
            List<Reservacion> deBD = reservacionesDao.obtenerTodasLasReservaciones();
            
            // 2. CORRECCIÓN CRÍTICA: Modificamos la lista existente en lugar de reemplazarla
            listaReservaciones.clear();       // Vaciamos los datos viejos de la memoria
            listaReservaciones.addAll(deBD);   // Inyectamos los datos frescos de MySQL
            
            // 3. Le decimos a la TableView física que se refresque visualmente
            tablaReservaciones.refresh();
            
            System.out.println("Datos cargados con éxito en la interfaz. Total filas: " + listaReservaciones.size());
            
        } catch (SQLException e) { 
            mostrarAlerta("Error de Carga", "No se pudieron obtener las reservaciones: " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            modificandoTabla = false; // Liberamos el listener
        }
    }

    private void configurarBuscadorRealTime() {
        // Creamos el filtro envoltura (FilteredList) apuntando a nuestra lista persistente
        FilteredList<Reservacion> filteredData = new FilteredList<>(listaReservaciones, p -> true);
        
        // Listener que reacciona cada vez que escribes en la barra blanca de Club Penguin
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(reserva -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (reserva.getNombreCliente() != null && reserva.getNombreCliente().toLowerCase().contains(lowerCaseFilter)) return true;
                if (reserva.getFolioUnico() != null && reserva.getFolioUnico().toLowerCase().contains(lowerCaseFilter)) return true;
                if (String.valueOf(reserva.getIdMesa()).contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        
        // Amarramos la FilteredList a la tabla de la interfaz
        tablaReservaciones.setItems(filteredData);
    }

    @FXML
    private void registrarReserva(ActionEvent event) {
        // 1. VALIDACIÓN: Campos vacíos
        if (txtCliente.getText() == null || txtCliente.getText().trim().isEmpty() ||
            dpFecha.getValue() == null || 
            cbHora.getValue() == null || 
            cbPinguinos.getValue() == null || 
            cbMesa.getValue() == null) {
            
            mostrarAlerta("Campos incompletos", "Por favor, llena todos los campos para registrar la reservación.");
            return;
        }

        LocalDate fechaSeleccionada = dpFecha.getValue();
        LocalDate fechaActual = LocalDate.now(); // Captura de forma dinámica la fecha del sistema (20 de mayo de 2026)

        // 2. VALIDACIÓN: Evitar fechas anteriores al día de hoy
        if (fechaSeleccionada.isBefore(fechaActual)) {
            mostrarAlerta("Fecha inválida", "No puedes programar una reservación para una fecha que ya pasó.");
            return;
        }

        // 3. VALIDACIÓN: Si es hoy, evitar que elijan una hora del pasado
        if (fechaSeleccionada.isEqual(fechaActual)) {
            LocalTime horaSeleccionada = LocalTime.parse(cbHora.getValue());
            LocalTime horaActual = LocalTime.now();

            if (horaSeleccionada.isBefore(horaActual)) {
                mostrarAlerta("Hora inválida", "La hora seleccionada ya pasó para el día de hoy. Elige un horario posterior.");
                return;
            }
        }

        // 4. VALIDACIÓN: Regla de negocio de capacidad de mesas (Alineada a tu script SQL)
        int numPinguinos = cbPinguinos.getValue();
        int mesaSeleccionada = cbMesa.getValue();
        int capacidadMaxima = 4; // Capacidad base por defecto

        // Mapeo estricto de capacidades según las restricciones de tu Workbench
        switch (mesaSeleccionada) {
            case 3: case 7: case 9:
                capacidadMaxima = 2; // Mesas pequeñas de 2 personas
                break;
            case 1: case 2: case 5: case 6: case 10: case 12:
                capacidadMaxima = 4; // Mesas estándar de 4 personas
                break;
            case 4: case 11:
                capacidadMaxima = 6; // Mesas grandes de 6 personas
                break;
            case 8:
                capacidadMaxima = 8; // La mesa VIP de 8 personas
                break;
        }

        if (numPinguinos > capacidadMaxima) {
            mostrarAlerta("Límite de capacidad", "La Mesa " + mesaSeleccionada + " solo tiene capacidad para " + capacidadMaxima + " pingüinos. Elige una mesa más grande o reduce los comensales.");
            return;
        }

        // --- SI PASA TODAS LAS EXCEPCIONES, PROCEDE A GUARDAR EN MYSQL ---
        String folio = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String nombreIngresado = txtCliente.getText().trim();
        
        Reservacion nueva = new Reservacion(
            0, 
            folio, 
            "TEMP_ID", 
            nombreIngresado, 
            mesaSeleccionada,
            fechaSeleccionada.toString(), 
            cbHora.getValue() + ":00", 
            numPinguinos, 
            "Confirmada"
        );

        try {
            if (reservacionesDao.insertarReservacion(nueva)) {
                mostrarAlerta("Éxito", "Reservación guardada con Folio: " + folio);
                cargarDatosTabla(); 
                limpiarFormulario();
            }
        } catch (SQLException e) { 
            mostrarAlerta("Error de Base de Datos", "No se pudo registrar: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    @FXML
    private void modificarSeleccion(ActionEvent event) {
        if (reservacionSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Elige una reserva de la cuadrícula.");
            return;
        }

        String horaLimpia = cbHora.getValue();
        if (horaLimpia.length() == 5) {
            horaLimpia += ":00"; // Agrega los segundos solo si le hacen falta
        }

        Reservacion modificada = new Reservacion(
            reservacionSeleccionada.getIdReservacion(),
            reservacionSeleccionada.getFolioUnico(),
            reservacionSeleccionada.getIdCliente(), // Mantiene el ID relacional de la BD
            txtCliente.getText(),
            cbMesa.getValue(),
            dpFecha.getValue().toString(),
            horaLimpia,
            cbPinguinos.getValue(),
            reservacionSeleccionada.getEstado()
        );

        try {
            if (reservacionesDao.actualizarReservacion(modificada)) {
                mostrarAlerta("Éxito", "La reservación se modificó correctamente.");
                cargarDatosTabla();
                limpiarFormulario();
            } else {
                System.out.println("No se alteraron filas en la BD.");
            }
        } catch (SQLException e) { 
            mostrarAlerta("Error de actualización", "Error SQL: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    @FXML
    private void cancelarReserva(ActionEvent event) {
        if (reservacionSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Elige la reservación que deseas cancelar.");
            return;
        }

        try {
            if (reservacionesDao.cancelarReservacion(reservacionSeleccionada.getIdReservacion())) {
                mostrarAlerta("Cancelada", "El registro se ha marcado como Cancelado.");
                cargarDatosTabla();
                limpiarFormulario();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleRegresar(ActionEvent event) {
        try {
            FXMLLoader loader = App.getFXMLLoader("Dashboard");
            Parent root = loader.load();
            DashboardController dc = loader.getController();
            dc.configurarUsuario(App.usuarioLogueado);

            Stage stage = (Stage) tablaReservaciones.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Recepción");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void limpiarFormulario() {
        reservacionSeleccionada = null;
        txtCliente.clear();
        dpFecha.setValue(LocalDate.now());
        cbHora.setValue("13:00");
        cbPinguinos.setValue(2);
        cbMesa.setValue(1);
    }

    private void mostrarAlerta(String t, String m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(t); alert.setHeaderText(null); alert.setContentText(m);
        alert.showAndWait();
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