package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.dao.AlmacenDAO;
import com.mycompany.restaurante.modelo.pojo.ProductoAlmacen;
import com.mycompany.restaurante.modelo.pojo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class AlmacenController {

    @FXML private TextField txtNombre, txtCantidad, txtStockMinimo;
    @FXML private ComboBox<String> cbUnidad;
    @FXML private TableView<ProductoAlmacen> tblAlmacen;
    @FXML private TableColumn<ProductoAlmacen, String> colNombre, colUnidad;
    @FXML private TableColumn<ProductoAlmacen, Double> colCantidad, colMinimo;

    private AlmacenDAO dao = new AlmacenDAO();
    private ObservableList<ProductoAlmacen> listaProductos;
    private ProductoAlmacen productoSeleccionado;

    @FXML
    public void initialize() {
        cbUnidad.getItems().addAll("Kilogramos (kg)", "Litros (L)", "Gramos (g)", "Mililitros (ml)", "Piezas (pz)");
        
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        
        cargarDatos();
        
        tblAlmacen.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                txtNombre.setText(productoSeleccionado.getNombre());
                txtCantidad.setText(String.valueOf(productoSeleccionado.getCantidad()));
                cbUnidad.setValue(productoSeleccionado.getUnidad());
                txtStockMinimo.setText(String.valueOf(productoSeleccionado.getStockMinimo()));
            }
        });
    }

    private void cargarDatos() {
        List<ProductoAlmacen> productosDB = dao.obtenerProductos();
        listaProductos = FXCollections.observableArrayList(productosDB);
        tblAlmacen.setItems(listaProductos);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String unidad = cbUnidad.getValue();
        
        if (nombre.isEmpty() || txtCantidad.getText().isEmpty() || unidad == null || txtStockMinimo.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            double cantidad = Double.parseDouble(txtCantidad.getText());
            double stockMinimo = Double.parseDouble(txtStockMinimo.getText());

            if (productoSeleccionado == null) {
                // Nuevo registro
                ProductoAlmacen nuevo = new ProductoAlmacen(0, nombre, cantidad, unidad, stockMinimo);
                if (dao.registrarProducto(nuevo)) {
                    mostrarAlerta("Éxito", "Producto agregado al almacén.");
                } else {
                    mostrarAlerta("Error", "No se pudo registrar.");
                }
            } else {
                // Actualizar existente
                productoSeleccionado.setNombre(nombre);
                productoSeleccionado.setCantidad(cantidad);
                productoSeleccionado.setUnidad(unidad);
                productoSeleccionado.setStockMinimo(stockMinimo);
                
                if (dao.actualizarProducto(productoSeleccionado)) {
                    mostrarAlerta("Éxito", "Producto actualizado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar.");
                }
            }
            cargarDatos();
            clicLimpiar(null);
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "La cantidad y el stock mínimo deben ser números.");
        }
    }

    @FXML
    private void clicLimpiar(ActionEvent event) {
        txtNombre.clear();
        txtCantidad.clear();
        txtStockMinimo.clear();
        cbUnidad.setValue(null);
        productoSeleccionado = null;
        tblAlmacen.getSelectionModel().clearSelection();
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            java.net.URL url = com.mycompany.restaurante.App.class.getResource("/fxml/Dashboard.fxml");
            if (url == null) {
                url = com.mycompany.restaurante.App.class.getResource("Dashboard.fxml");
            }
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
            javafx.scene.Parent root = loader.load();

            // Reconfigurar el Dashboard para el Gerente
            DashboardController controller = loader.getController();
            Usuario admin = new Usuario();
            admin.setRol("Gerente");
            controller.configurarUsuario(admin);

            javafx.scene.Node nodoOrigen = (javafx.scene.Node) event.getSource();
            javafx.scene.Scene escenaActual = nodoOrigen.getScene();
            escenaActual.setRoot(root);

        } catch (Exception ex) {
            System.out.println("Error al volver al Dashboard.");
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}