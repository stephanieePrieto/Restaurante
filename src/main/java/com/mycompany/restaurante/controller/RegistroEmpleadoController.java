package com.mycompany.restaurante.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import com.mycompany.restaurante.dao.UsuarioDAO;

public class RegistroEmpleadoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;
    
    @FXML private Button btnRegistrar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;
    
    @FXML private TableView<Usuario> tblEmpleados;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colPassword;
    @FXML private TableColumn<Usuario, String> colRol;

    private UsuarioDAO dao = new UsuarioDAO();
    private ObservableList<Usuario> listaEmpleados;
    private Usuario empleadoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mejorado para incluir todos los roles de la base de datos de tu equipo
        cbRol.getItems().addAll("Gerente", "Mesero", "Chef", "Cajero", "Recepcionista");
        
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));
        
        cargarEmpleados();
        
        tblEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                empleadoSeleccionado = newSelection;
                txtNombre.setText(empleadoSeleccionado.getNombre());
                txtUsuario.setText(empleadoSeleccionado.getUsername());
                txtPassword.setText(empleadoSeleccionado.getPassword());
                cbRol.setValue(empleadoSeleccionado.getNombreRol());
            }
        });
    }

    private void cargarEmpleados() {
        List<Usuario> empleadosDB = dao.obtenerEmpleados();
        listaEmpleados = FXCollections.observableArrayList(empleadosDB);
        tblEmpleados.setItems(listaEmpleados);
    }

    private int obtenerIdRol(String nombreRol) {
        if (nombreRol == null) return 0;
        switch (nombreRol) {
            case "Gerente": return 1;
            case "Mesero": return 2;
            case "Chef": return 3;
            case "Cajero": return 4;
            case "Recepcionista": return 5;
            default: return 0;
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        String rol = cbRol.getValue();

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || rol == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        Usuario nuevo = new Usuario(0, nombre, usuario, password, obtenerIdRol(rol), rol);
        
        if (dao.registrarEmpleado(nuevo)) {
            mostrarAlerta("Éxito", "Empleado registrado correctamente.");
            cargarEmpleados();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo registrar el empleado.");
        }
    }

    @FXML
    private void clicActualizar(ActionEvent event) {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un empleado de la tabla para actualizar.");
            return;
        }

        String nombre = txtNombre.getText();
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        String rol = cbRol.getValue();

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || rol == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        empleadoSeleccionado.setNombre(nombre);
        empleadoSeleccionado.setUsername(usuario);
        empleadoSeleccionado.setPassword(password);
        empleadoSeleccionado.setIdRol(obtenerIdRol(rol));
        empleadoSeleccionado.setNombreRol(rol);

        if (dao.actualizarEmpleado(empleadoSeleccionado)) {
            mostrarAlerta("Éxito", "Empleado actualizado correctamente.");
            cargarEmpleados();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el empleado.");
        }
    }

    @FXML
    private void clicLimpiar(ActionEvent event) {
        limpiarCampos();
    }
    
    // El método que le da vida al botón de volver
    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            java.net.URL url = com.mycompany.restaurante.App.class.getResource("/fxml/Dashboard.fxml");
            if (url == null) {
                url = com.mycompany.restaurante.App.class.getResource("Dashboard.fxml");
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
            javafx.scene.Parent root = loader.load();

            javafx.scene.Node nodoOrigen = (javafx.scene.Node) event.getSource();
            javafx.scene.Scene escenaActual = nodoOrigen.getScene();
            escenaActual.setRoot(root);
            
        } catch (Exception ex) {
            System.out.println("Error al volver al Dashboard.");
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtUsuario.clear();
        txtPassword.clear();
        cbRol.setValue(null);
        empleadoSeleccionado = null;
        tblEmpleados.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}