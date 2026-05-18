package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class DashboardController {

    // --- COMPONENTES VISUALES (Todos unificados) ---
    @FXML private Label lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero;
    @FXML private Button btnAltaMenu, btnEmpleados, btnReservaciones, btnCobro, btnPedido, btnAsignar, btnGenerar, btnEstado, btnLista, btnFacturacion, btnAsistencia;
    @FXML private MenuButton mbControl, mbGestionOrdenes;
    @FXML private MenuItem miReporteVentas, miReporteAlmacen;

    @FXML
    public void initialize() {
        if (App.usuarioLogueado != null) {
            configurarPermisos(App.usuarioLogueado.getRol());
        }
    }

    public void configurarUsuario(Usuario usuario) {
        configurarPermisos(usuario.getRol());
    }

    // --- GESTIÓN DE PERMISOS POR ROL ---
    private void configurarPermisos(String rol) {
        Node[] todos = {
            lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero,
            btnAltaMenu, btnEmpleados, mbControl, btnReservaciones, btnAsignar, 
            btnLista, btnCobro, btnGenerar, btnFacturacion, btnPedido, btnEstado, mbGestionOrdenes,
            btnAsistencia
        };

        // Ocultamos todo al inicio
        for (Node n : todos) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(false);
            }
        }

        // Activamos según el rol correspondiente (Incluyendo tu botón de Asistencia)
        if ("Gerente".equals(rol)) {
            activar(lblSeccionGerente); activar(btnAltaMenu); activar(btnEmpleados); activar(mbControl); activar(btnAsistencia);
        } else if ("Recepcionista".equals(rol)) {
            activar(lblSeccionRecepcionista); activar(btnReservaciones); activar(btnAsignar); activar(btnLista); activar(btnAsistencia);
        } else if ("Cajero".equals(rol)) {
            activar(lblSeccionCajero); activar(btnCobro); activar(btnGenerar); activar(btnFacturacion); activar(btnAsistencia);
        } else if ("Mesero".equals(rol)) {
            activar(lblSeccionMesero); activar(btnPedido); activar(btnEstado); activar(mbGestionOrdenes); activar(btnAsistencia);
        }
    }

    private void activar(Node n) {
        if (n != null) {
            n.setVisible(true);
            n.setManaged(true);
        }
    }

    // --- MÉTODO MAESTRO PARA CAMBIAR PANTALLAS (Ultra Seguro) ---
    private void cambiarPantalla(ActionEvent event, String fxml, String titulo) throws IOException {
        // Usamos el cargador centralizado del equipo para mantener el estándar
        FXMLLoader loader = App.getFXMLLoader(fxml);
        Parent root = loader.load();

        Stage stage;
        // Validación para evitar ClassCastException si el click viene de un MenuItem
        if (event != null && event.getSource() instanceof Node) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            // Si viene de un menú, usamos btnEmpleados como ancla segura para conseguir la ventana
            stage = (Stage) btnEmpleados.getScene().getWindow(); 
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(titulo);
        stage.centerOnScreen();
        stage.show();
    }

    // --- ACCIONES DE NAVEGACIÓN (Tus métodos + los de tu equipo) ---

    @FXML 
    private void abrirAsistencia(ActionEvent event) {
        try { cambiarPantalla(event, "RegistrarAsistenciaEmpleados", "Registro de Asistencia"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirPantallaPedido(ActionEvent event) {
        try { cambiarPantalla(event, "PantallaPedido", "Tomar Pedido"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirRegistroPlatillo(ActionEvent event) {
        try { cambiarPantalla(event, "RegistroPlatillo", "Gestión de Menú"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirRegistroEmpleado(ActionEvent event) {
        try { cambiarPantalla(event, "RegistroEmpleado", "Gestión de Empleados"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirReporteVentas(ActionEvent event) {
        try { cambiarPantalla(event, "ReporteVentas", "Reporte de Ventas"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirAlmacen(ActionEvent event) {
        try { cambiarPantalla(event, "Almacen", "Control de Almacén"); } catch (IOException ex) { ex.printStackTrace(); }
    }
    
    @FXML 
    private void abrirEstadoMesas(ActionEvent event) {
        try { cambiarPantalla(event, "EstadoMesa", "Estado de Mesas"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML
    private void clicGenerarCuenta(ActionEvent event) {
        try { cambiarPantalla(event, "GenerarCuenta", "Generar Cuenta - Restaurante CP"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void clicCobro(ActionEvent event) {
        try { cambiarPantalla(event, "RegistrarPago", "Registro de Pago - Cajero"); } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void btnIrFacturacion(ActionEvent event) {
        try { cambiarPantalla(event, "Factura", "Facturación - Pizzatron CP"); } catch (IOException ex) { ex.printStackTrace(); }
    }
    
    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            App.usuarioLogueado = null; 
            cambiarPantalla(event, "Login", "Acceso al Sistema");
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
