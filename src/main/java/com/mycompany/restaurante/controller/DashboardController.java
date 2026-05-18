package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.CuentaDAO;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero;
    @FXML private Button btnAltaMenu, btnEmpleados, btnReservaciones, btnCobro, btnPedido, btnAsignar, btnGenerar, btnEstado, btnLista, btnFacturacion;
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

    private void configurarPermisos(String rol) {
        Node[] todos = {
            lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero,
            btnAltaMenu, btnEmpleados, mbControl, btnReservaciones, btnAsignar, 
            btnLista, btnCobro, btnGenerar, btnFacturacion, btnPedido, btnEstado, mbGestionOrdenes
        };

        for (Node n : todos) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(false);
            }
        }

        if ("Gerente".equals(rol)) {
            activar(lblSeccionGerente); activar(btnAltaMenu); activar(btnEmpleados); activar(mbControl);
        } else if ("Recepcionista".equals(rol)) {
            activar(lblSeccionRecepcionista); activar(btnReservaciones); activar(btnAsignar); activar(btnLista);
        } else if ("Cajero".equals(rol)) {
            activar(lblSeccionCajero); activar(btnCobro); activar(btnGenerar); activar(btnFacturacion);
        } else if ("Mesero".equals(rol)) {
            activar(lblSeccionMesero); activar(btnPedido); activar(btnEstado); activar(mbGestionOrdenes);
        }
    }

    private void activar(Node n) {
        if (n != null) {
            n.setVisible(true);
            n.setManaged(true);
        }
    }
    
    

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            App.usuarioLogueado = null; 
            cambiarPantalla(event, "Login", "Acceso al Sistema");
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- MÉTODOS DEL EQUIPO ---
    @FXML private void abrirPantallaPedido(ActionEvent event) {
        try { cambiarPantalla(event, "PantallaPedido", "Tomar Pedido"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirRegistroPlatillo(ActionEvent event) {
        try { cambiarPantalla(event, "RegistroPlatillo", "Gestión de Menú"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- MÉTODOS RECUPERADOS (NUESTROS) ---
    @FXML private void abrirRegistroEmpleado(ActionEvent event) {
        try { cambiarPantalla(event, "RegistroEmpleado", "Gestión de Empleados"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirReporteVentas(ActionEvent event) {
        try { cambiarPantalla(event, "ReporteVentas", "Reporte de Ventas"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirAlmacen(ActionEvent event) {
        try { cambiarPantalla(event, "Almacen", "Control de Almacén"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    // --- MÉTODO CAMBIAR PANTALLA (Corregido para soportar MenuItem) ---
    private void cambiarPantalla(ActionEvent event, String fxml, String titulo) throws IOException {
        FXMLLoader loader = App.getFXMLLoader(fxml);
        Parent root = loader.load();
        
        // Usamos btnEmpleados como referencia segura en lugar de event.getSource() para evitar el ClassCastException
        Stage stage = (Stage) btnEmpleados.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
    
    
    
    

    
    @FXML
    private void clicGenerarCuenta(ActionEvent event) {
    try {
        // 1. Cargamos el FXML de la pantalla que acabas de arreglar
        // Revisa que el nombre del archivo sea exacto (mayúsculas/minúsculas)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenerarCuenta.fxml"));
        Parent root = loader.load();

        // 2. Obtenemos la ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        // 3. Cambiamos la escena a la de "Estado de Cuenta"
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Generar Cuenta - Restaurante CP");
        stage.show();
        
    } catch (IOException e) {
        // Esto te avisará en la consola si el archivo no existe o tiene errores
        System.err.println("Error al abrir la pantalla de cuenta: " + e.getMessage());
        e.printStackTrace(); 
    }
}

    
    @FXML
    private void clicCobro(ActionEvent event) {
    try {
        // 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistrarPago.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Registro de Pago - Cajero");
        stage.show();
        
    } catch (IOException e) {
        System.err.println("Error al abrir RegistrarPago.fxml: " + e.getMessage());
        e.printStackTrace();
    }
    
}
    @FXML
    private void btnIrFacturacion(javafx.event.ActionEvent event) {
        try { 
            cambiarPantalla(event, "Factura", "Facturación - Pizzatron CP"); 
        } catch (IOException ex) { 
            ex.printStackTrace(); 
        }
    }
    
    
@FXML 
    private void abrirEstadoMesas(ActionEvent event) {
        try { 
            cambiarPantalla(event, "EstadoMesa", "Estado de Mesas"); 
        } catch (IOException ex) { 
            ex.printStackTrace(); 
        }
    }
    
  



}