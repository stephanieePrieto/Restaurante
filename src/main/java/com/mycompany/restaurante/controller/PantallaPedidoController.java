package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.dao.PedidoDAO;
import com.mycompany.restaurante.dao.PlatilloDAO;
import com.mycompany.restaurante.modelo.pojo.Platillo;
import com.mycompany.restaurante.modelo.sql.MySQLConnect;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PantallaPedidoController implements Initializable {

    @FXML private Button btnBebidas, btnEspeciales, btnExtras, btnPasteles, btnPizzas, btnVolver;
    @FXML private GridPane gridBebidas, gridEspeciales, gridExtras, gridPasteles, gridPizza;

    // --- ELEMENTOS DEL TICKET ---
    @FXML private ComboBox<String> cmbMesas;
    @FXML private Label lblTotalText;
    @FXML private Label lblPedido; // <--- AQUÍ ESTÁ TU NUEVO LABEL PARA EL FOLIO
    
    @FXML private TableView<Platillo> tablaPedido;
    @FXML private TableColumn<Platillo, String> colArticulo;
    @FXML private TableColumn<Platillo, Integer> colCant;
    @FXML private TableColumn<Platillo, Double> colTotal;
    
    @FXML private Button btnBorrarPedido, btnEliminar, btnEnviarChef;

    // --- VARIABLES ---
    private ObservableList<Platillo> listaPedido = FXCollections.observableArrayList();
    private double totalMonto = 0.0;
    private int pedidoActivoActual = -1; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarMesasOcupadas();
        mostrarGrid("Pizzas"); 
        
        // Al entrar, el pedido está en blanco
        if (lblPedido != null) lblPedido.setText("0000");
        
        cmbMesas.setOnAction(event -> cargarPedidoMesaSeleccionada());
    }

    private void configurarTabla() {
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("precio")); 
        tablaPedido.setItems(listaPedido);
    }

    // =======================================================================
    // CONEXIONES A BASE DE DATOS
    // =======================================================================

    private void cargarMesasOcupadas() {
        MySQLConnect mysql = new MySQLConnect();
        try (Connection con = mysql.connection()) {
            if (con == null) return; // Si no hay BD conectada, no hace nada
            
            try (PreparedStatement ps = con.prepareStatement("SELECT idMesa FROM mesa WHERE estado = 'Ocupada'");
                 ResultSet rs = ps.executeQuery()) {
                
                cmbMesas.getItems().clear();
                while (rs.next()) {
                    // Ahora SOLO insertamos el número, como pediste
                    cmbMesas.getItems().add(String.valueOf(rs.getInt("idMesa")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error BD al cargar mesas: " + e.getMessage());
        }
    }

    private void cargarPedidoMesaSeleccionada() {
        if (cmbMesas.getValue() == null) return;
        
        // Como ahora solo es un número, lo parseamos directo
        int idMesa = Integer.parseInt(cmbMesas.getValue());
        MySQLConnect mysql = new MySQLConnect();
        
        try (Connection con = mysql.connection()) {
            if (con == null) return;
            
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            PlatilloDAO platilloDAO = new PlatilloDAO(con);
            
            pedidoActivoActual = pedidoDAO.obtenerPedidoActivoPorMesa(idMesa);
            listaPedido.clear();
            
            if (pedidoActivoActual != -1) {
                // Si ya existe el pedido, actualizamos tu Label con el Folio real (con ceros a la izquierda)
                if (lblPedido != null) lblPedido.setText(String.format("%04d", pedidoActivoActual));
                
                List<Platillo> platillosBD = platilloDAO.obtenerPlatillosPorOrden(pedidoActivoActual);
                for (Platillo p : platillosBD) {
                    p.setPrecio(p.getPrecio() * p.getCantidad());
                }
                listaPedido.setAll(platillosBD);
            } else {
                // Si la mesa no tiene pedido aún, mostramos "Nuevo"
                if (lblPedido != null) lblPedido.setText("Nuevo");
            }
            calcularTotal();
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo cargar el pedido de la mesa.");
        }
    }

    @FXML
    void clicEnviarChef(ActionEvent event) {
        if (listaPedido.isEmpty()) {
            mostrarAlerta("Error", "El ticket está vacío. Agrega platillos a la comanda primero.");
            return;
        }
        if (cmbMesas.getValue() == null) {
            mostrarAlerta("Error", "Selecciona el número de mesa del cliente.");
            return;
        }

        int idMesa = Integer.parseInt(cmbMesas.getValue());
        MySQLConnect mysql = new MySQLConnect();

        try (Connection con = mysql.connection()) {
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            
            if (pedidoActivoActual == -1) {
                pedidoActivoActual = pedidoDAO.crearNuevoPedido(idMesa);
            }
            
            pedidoDAO.guardarDetallesPedido(pedidoActivoActual, listaPedido);
            
            // --- AQUÍ ESTÁ TU NUEVO MENSAJE PROFESIONAL ---
            mostrarAlertaExito("Comanda Registrada", "La orden #" + String.format("%04d", pedidoActivoActual) + " de la mesa " + idMesa + " ya está en preparación en cocina.");
            
            // Limpiamos todo para el siguiente cliente
            listaPedido.clear();
            calcularTotal();
            cmbMesas.getSelectionModel().clearSelection();
            pedidoActivoActual = -1; 
            if (lblPedido != null) lblPedido.setText("0000");

        } catch (SQLException e) {
                    // Esto hará que la alerta te diga EXACTAMENTE qué platillo o qué columna falla
                    mostrarAlerta("Error de Base de Datos", "Detalle: " + e.getMessage());
                    e.printStackTrace(); 
                }
    }

    // =======================================================================
    // LÓGICA DE INTERFAZ Y TICKET
    // =======================================================================

    private void agregarAlTicket(String nombreItem, double precioBase) {
        if (cmbMesas.getValue() == null) {
            mostrarAlerta("Mesa no seleccionada", "Por favor selecciona el número de mesa antes de armar la comanda.");
            return; 
        }

        boolean encontrado = false;
        for (Platillo p : listaPedido) {
            if (p.getNombre().equals(nombreItem)) {
                p.setCantidad(p.getCantidad() + 1);
                p.setPrecio(precioBase * p.getCantidad()); 
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            Platillo nuevo = new Platillo();
            nuevo.setNombre(nombreItem);
            nuevo.setCantidad(1);
            nuevo.setPrecio(precioBase); 
            listaPedido.add(nuevo);
        }
        tablaPedido.refresh();
        calcularTotal();
    }

    private void calcularTotal() {
        totalMonto = 0.0;
        for (Platillo p : listaPedido) {
            totalMonto += p.getPrecio(); 
        }
        lblTotalText.setText("$ " + String.format("%.2f", totalMonto));
    }

    @FXML
    void clicEliminarRenglon(ActionEvent event) {
        Platillo seleccionado = tablaPedido.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getCantidad() > 1) {
                double precioBase = seleccionado.getPrecio() / seleccionado.getCantidad();
                seleccionado.setCantidad(seleccionado.getCantidad() - 1);
                seleccionado.setPrecio(precioBase * seleccionado.getCantidad());
                tablaPedido.refresh();
            } else {
                listaPedido.remove(seleccionado);
            }
            calcularTotal();
        } else {
            mostrarAlerta("Aviso", "Selecciona un renglón del ticket para eliminar.");
        }
    }

    @FXML
    void clicBorrarPedido(ActionEvent event) {
        listaPedido.clear();
        calcularTotal();
    }

    private void mostrarGrid(String categoria) {
        gridPizza.setVisible(false); gridBebidas.setVisible(false);
        gridPasteles.setVisible(false); gridExtras.setVisible(false); gridEspeciales.setVisible(false);

        switch (categoria) {
            case "Pizzas": gridPizza.setVisible(true); break;
            case "Bebidas": gridBebidas.setVisible(true); break;
            case "Pasteles": gridPasteles.setVisible(true); break;
            case "Extras": gridExtras.setVisible(true); break;
            case "Especiales": gridEspeciales.setVisible(true); break;
        }
    }

    @FXML void clicVerPizzas(ActionEvent event) { mostrarGrid("Pizzas"); }
    @FXML void clicVerBebidas(ActionEvent event) { mostrarGrid("Bebidas"); }
    @FXML void clicVerPasteles(ActionEvent event) { mostrarGrid("Pasteles"); }
    @FXML void clicVerExtras(ActionEvent event) { mostrarGrid("Extras"); }
    @FXML void clicVerEspeciales(ActionEvent event) { mostrarGrid("Especiales"); }

    // --- TODOS LOS BOTONES DE PLATILLOS (+) ---
    @FXML void clicAddPizzaQueso(ActionEvent event) { agregarAlTicket("Pizza Queso", 120.0); }
    @FXML void clicAddPizzaPepperoni(ActionEvent event) { agregarAlTicket("Pizza Pepperoni", 140.0); }
    @FXML void clicAddPizzaVegetariana(ActionEvent event) { agregarAlTicket("Pizza Vegetariana", 150.0); }
    @FXML void clicAddPizzaHawaiana(ActionEvent event) { agregarAlTicket("Pizza Hawaiana", 145.0); }
    @FXML void clicAddPizzaCarne(ActionEvent event) { agregarAlTicket("Pizza de Carne", 160.0); }
    @FXML void clicAddPizzaBBQ(ActionEvent event) { agregarAlTicket("Pizza BBQ Pollo", 155.0); }
    @FXML void clicAddPizzaAlfredo(ActionEvent event) { agregarAlTicket("Pizza Alfredo", 150.0); }
    @FXML void clicAddPizzaDeluxe(ActionEvent event) { agregarAlTicket("Pizza Deluxe", 170.0); }
    @FXML void clicAddPizzaQuesos(ActionEvent event) { agregarAlTicket("Pizza 4 Quesos", 165.0); }
    @FXML void clicAddCafe(ActionEvent event) { agregarAlTicket("Café", 35.0); }
    @FXML void clicAddCafeHelado(ActionEvent event) { agregarAlTicket("Café Helado", 45.0); }
    @FXML void clicAddCafeLeche(ActionEvent event) { agregarAlTicket("Café con Leche", 40.0); }
    @FXML void clicAddCapucchino(ActionEvent event) { agregarAlTicket("Capuchino", 50.0); }
    @FXML void clicAddGranizadoAzul(ActionEvent event) { agregarAlTicket("Granizado Azul", 55.0); }
    @FXML void clicAddJugoNaranja(ActionEvent event) { agregarAlTicket("Jugo de Naranja", 40.0); }
    @FXML void clicAddMalteadaFresa(ActionEvent event) { agregarAlTicket("Malteada de Fresa", 60.0); }
    @FXML void clicAddBatidoChocolate(ActionEvent event) { agregarAlTicket("Batido de Chocolate", 60.0); }
    @FXML void clicAddSodaLima(ActionEvent event) { agregarAlTicket("Soda de Lima", 30.0); }
    @FXML void clicAddBrownie(ActionEvent event) { agregarAlTicket("Brownie", 45.0); }
    @FXML void clicAddCheeseCakeFresa(ActionEvent event) { agregarAlTicket("Cheesecake de Fresa", 65.0); }
    @FXML void clicAddCupcakeChocolate(ActionEvent event) { agregarAlTicket("Cupcake Chocolate", 30.0); }
    @FXML void clicAddCupcakeFresa(ActionEvent event) { agregarAlTicket("Cupcake Fresa", 30.0); }
    @FXML void clicAddPastelChocolate(ActionEvent event) { agregarAlTicket("Pastel de Chocolate", 60.0); }
    @FXML void clicAddPayManzana(ActionEvent event) { agregarAlTicket("Pay de Manzana", 50.0); }
    @FXML void clicAddPayZanahoria(ActionEvent event) { agregarAlTicket("Pay de Zanahoria", 50.0); }
    @FXML void clicAddRolCanela(ActionEvent event) { agregarAlTicket("Rol de Canela", 40.0); }
    @FXML void clicAddSundae(ActionEvent event) { agregarAlTicket("Sundae", 55.0); }
    @FXML void clicAddAlitasBBQ(ActionEvent event) { agregarAlTicket("Alitas BBQ", 90.0); }
    @FXML void clicAddArosCebolla(ActionEvent event) { agregarAlTicket("Aros de Cebolla", 60.0); }
    @FXML void clicAddCalzone(ActionEvent event) { agregarAlTicket("Calzone Clásico", 110.0); }
    @FXML void clicAddEnsalada(ActionEvent event) { agregarAlTicket("Ensalada Fresca", 70.0); }
    @FXML void clicAddEnsaladaRepollo(ActionEvent event) { agregarAlTicket("Ensalada de Repollo", 40.0); }
    @FXML void clicAddNuggetsPollo(ActionEvent event) { agregarAlTicket("Nuggets de Pollo", 75.0); }
    @FXML void clicAddPanAjo(ActionEvent event) { agregarAlTicket("Pan de Ajo", 45.0); }
    @FXML void clicAddPanAjoQueso(ActionEvent event) { agregarAlTicket("Pan de Ajo con Queso", 55.0); }
    @FXML void clicAddPapasFritas(ActionEvent event) { agregarAlTicket("Papas Fritas", 50.0); }
    @FXML void clicAddSopaDia(ActionEvent event) { agregarAlTicket("Sopa del Día", 60.0); }
    @FXML void clicAddPizzaCamarones(ActionEvent event) { agregarAlTicket("Pizza de Camarones", 180.0); }
    @FXML void clicAddPizzaCorazon(ActionEvent event) { agregarAlTicket("Pizza Corazón", 160.0); }
    @FXML void clicAddPizzaEstrella(ActionEvent event) { agregarAlTicket("Pizza Estrella", 160.0); }
    @FXML void clicAddPizzaMacCheese(ActionEvent event) { agregarAlTicket("Pizza Mac & Cheese", 150.0); }
    @FXML void clicAddPizzaPinguino(ActionEvent event) { agregarAlTicket("Pizza Pingüino Especial", 170.0); }
    @FXML void clicAddPizzaPostre(ActionEvent event) { agregarAlTicket("Pizza de Postre", 140.0); }
    @FXML void clicAddPizzaTaco(ActionEvent event) { agregarAlTicket("Pizza Taco", 160.0); }
    @FXML void clicAddPizzaVolcan(ActionEvent event) { agregarAlTicket("Pizza Volcán Picante", 190.0); }

    @FXML
    void volverDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = App.getFXMLLoader("Dashboard"); 
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje);
        alert.showAndWait();
    }
}