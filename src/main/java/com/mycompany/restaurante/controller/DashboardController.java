package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.modelo.pojo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.Node;

public class DashboardController {

    @FXML private Label lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero;
    @FXML private Button btnAltaMenu, btnEmpleados, btnReservaciones, btnCobro, btnCapturar, btnAsignar, btnGenerar, btnEstado, btnLista, btnFacturacion;
    @FXML private MenuButton mbControl, mbGestionOrdenes;

    public void configurarUsuario(Usuario usuario) {
        // Ejecutamos la restricción según el rol
        configurarPermisos(usuario.getRol());
    }

    private void configurarPermisos(String rol) {
        // 1. Ocultamos ABSOLUTAMENTE TODO primero
        // Se corrigió el nombre de la variable (sin espacio)
        Node[] todosLosComponentes = {
            lblSeccionGerente, lblSeccionRecepcionista, lblSeccionCajero, lblSeccionMesero,
            btnAltaMenu, btnEmpleados, mbControl, 
            btnReservaciones, btnAsignar, btnLista,
            btnCobro, btnGenerar, btnFacturacion,
            btnCapturar, btnEstado, mbGestionOrdenes
        };

        for (Node n : todosLosComponentes) {
            if (n != null) {
                n.setVisible(false);
                n.setManaged(true);
            }
        }

        // 2. Activamos solo lo que pertenece según el rol de tu DB
        // IMPORTANTE: Asegúrate que en tu base de datos el nombre sea exacto
        if ("Gerente".equals(rol)) {
            activarComponente(lblSeccionGerente);
            activarComponente(btnAltaMenu);
            activarComponente(btnEmpleados);
            activarComponente(mbControl);
        } else if ("Recepcionista".equals(rol)) {
            activarComponente(lblSeccionRecepcionista);
            activarComponente(btnReservaciones);
            activarComponente(btnAsignar);
            activarComponente(btnLista);
        } else if ("Cajero".equals(rol)) {
            activarComponente(lblSeccionCajero);
            activarComponente(btnCobro);
            activarComponente(btnGenerar);
            activarComponente(btnFacturacion);
        } else if ("Mesero".equals(rol)) {
            activarComponente(lblSeccionMesero);
            activarComponente(btnCapturar);
            activarComponente(btnEstado);
            activarComponente(mbGestionOrdenes);
        }
    }

    private void activarComponente(Node n) {
        if (n != null) {
            n.setVisible(true);
            n.setManaged(true);
        }
    }
}