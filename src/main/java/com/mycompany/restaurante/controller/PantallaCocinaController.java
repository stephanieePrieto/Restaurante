
package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.OrdenDAO;
import com.mycompany.restaurante.modelo.pojo.Orden;
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

/**
 *
 * @author Stephanie
 */
public class PantallaCocinaController {
    @FXML private TableView<Orden> tvOrdenesPendientes;
    @FXML private TableColumn<Orden, Integer> tcIdOrden;
    @FXML private TableColumn<Orden, String> tcHoraLlegada;
    @FXML private TableColumn<Orden, String> tcEstado;
    @FXML private TableColumn<Orden, String> tcDetallePedido;
    
    @FXML private Label lblSinOrdenes;
    @FXML private Label lblMensajeError;
    
    @FXML
    public void initialize(){
        tcIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        tcHoraLlegada.setCellValueFactory(new PropertyValueFactory<>("fechaHoraLlegada"));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tcDetallePedido.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        
        consultarOrdenes();
    }
    
    @FXML
    private void consultarOrdenes(){
        lblMensajeError.setVisible(false);
        lblSinOrdenes.setVisible(false);
        tvOrdenesPendientes.getItems().clear();
        
        try{
            // Creamos la conexión usando la clase SQL
            MySQLConnect db = new MySQLConnect();
            
            if (db.connection() == null) {
                throw new SQLException("La conexión a la base de datos falló (conn is null)");
            }
            OrdenDAO ordenDao = new OrdenDAO(db.connection());
            
            // 2. Buscamos por estado pendiente como dice nuestro caso
            List<Orden> lista = ordenDao.buscarOrdenesPorEstado("Pendiente");
            
            
            if (lista.isEmpty()){
                lblSinOrdenes.setVisible(true); // Flujo alternativo
            } else {
                ObservableList<Orden> data = FXCollections.observableArrayList(lista);
                tvOrdenesPendientes.setItems(data); //Flujo normal
            }
            db.close(); //Cerramos la conexión por seguridad
        } catch (SQLException e){
            lblMensajeError.setVisible(true);
            System.err.println(e.getMessage());
        }
    }
    
    @FXML
    private void volverAlMenu(){
        try {
            com.mycompany.restaurante.App.setRoot("/fxml/mainForm");
        } catch (java.io.IOException e) {
            lblMensajeError.setText("Error al cargar el menú principal");
            lblMensajeError.setVisible(true);
            System.err.println("Error de navegación: " + e.getMessage());
        }
    }
}
