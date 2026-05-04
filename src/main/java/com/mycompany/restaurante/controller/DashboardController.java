package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero;
    @FXML private Button btnAltaMenu, btnEmpleados, btnReservaciones, btnCobro, btnPedido, btnAsignar, btnGenerar, btnEstado, btnLista, btnFacturacion;
    @FXML private MenuButton mbControl, mbGestionOrdenes;

    @FXML
    public void initialize() {
        // Cada vez que se carga el Dashboard, revisamos quién está logueado
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
            App.usuarioLogueado = null; // Borrar sesión
            cambiarPantalla(event, "Login", "Acceso al Sistema");
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirPantallaPedido(ActionEvent event) {
        try { cambiarPantalla(event, "PantallaPedido", "Tomar Pedido"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    @FXML private void abrirRegistroPlatillo(ActionEvent event) {
        try { cambiarPantalla(event, "RegistroPlatillo", "Gestión de Menú"); } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void cambiarPantalla(ActionEvent event, String fxml, String titulo) throws IOException {
        FXMLLoader loader = App.getFXMLLoader(fxml);
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}