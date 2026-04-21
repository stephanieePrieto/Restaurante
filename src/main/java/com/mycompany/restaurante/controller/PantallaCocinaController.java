
package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.PedidoDAO; // Cambiado de OrdenDAO
import com.mycompany.restaurante.modelo.pojo.Pedido; // Cambiado de Orden
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PantallaCocinaController {
    
    @FXML private TableView<Pedido> tvOrdenesPendientes;
    @FXML private TableColumn<Pedido, Integer> tcIdOrden;
    @FXML private TableColumn<Pedido, String> tcHoraLlegada;
    @FXML private TableColumn<Pedido, String> tcEstado;
    @FXML private TableColumn<Pedido, String> tcDetallePedido;
    
    @FXML private Label lblSinOrdenes;
    @FXML private Label lblMensajeError;
    
    @FXML
    public void initialize() {
        // Vinculación con los nuevos nombres del POJO Pedido
        tcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        tcHoraLlegada.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tcDetallePedido.setCellValueFactory(new PropertyValueFactory<>("detalleTexto"));
        
        // Evento de doble clic para marcar como listo
        tvOrdenesPendientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tvOrdenesPendientes.getSelectionModel().getSelectedItem() != null) {
                Pedido pedidoSeleccionado = tvOrdenesPendientes.getSelectionModel().getSelectedItem();
                procesarOrdenTerminada(pedidoSeleccionado);
            }
        });
        
        consultarOrdenes();
    }
    
    @FXML
    private void consultarOrdenes() {
        lblMensajeError.setVisible(false);
        lblSinOrdenes.setVisible(false);
        tvOrdenesPendientes.getItems().clear();
        
        try {
            MySQLConnect db = new MySQLConnect();
            
            if (db.connection() == null) {
                throw new SQLException("La conexión a la base de datos falló (conn is null)");
            }
            
            PedidoDAO pedidoDao = new PedidoDAO(db.connection());
            
            // Buscamos pedidos con estado 'Pendiente'
            List<Pedido> lista = pedidoDao.buscarPedidosPorEstado("Pendiente");
            
            if (lista.isEmpty()) {
                lblSinOrdenes.setVisible(true);
            } else {
                ObservableList<Pedido> data = FXCollections.observableArrayList(lista);
                tvOrdenesPendientes.setItems(data);
            }
            db.close();
            
        } catch (SQLException e) {
            lblMensajeError.setText("Error de conexión: " + e.getMessage());
            lblMensajeError.setVisible(true);
            System.err.println(e.getMessage());
        }
    }
    
    @FXML
    private void volverAlMenu() {
        try {
            com.mycompany.restaurante.App.setRoot("/fxml/mainForm");
        } catch (java.io.IOException e) {
            lblMensajeError.setText("Error al cargar el menú principal");
            lblMensajeError.setVisible(true);
            System.err.println("Error de navegación: " + e.getMessage());
        }
    }
    
    private void procesarOrdenTerminada(Pedido pedido) {
        try {
            MySQLConnect db = new MySQLConnect();
            PedidoDAO pedidoDao = new PedidoDAO(db.connection());

            // Actualizamos el estado a 'Listo' usando el idPedido real
            boolean exito = pedidoDao.actualizarEstadoPedido(pedido.getIdPedido(), "Listo");

            if (exito) {
                consultarOrdenes(); // Refrescar la tabla
                System.out.println("Pedido #" + pedido.getIdPedido() + " marcado como Listo.");
            }
            db.close();
        } catch (SQLException e) {
            lblMensajeError.setText("Error al actualizar el pedido.");
            lblMensajeError.setVisible(true);
        }
    }
}