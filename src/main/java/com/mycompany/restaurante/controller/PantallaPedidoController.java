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

    @FXML private GridPane gridBebidas, gridEspeciales, gridExtras, gridPasteles, gridPizza;
    @FXML private ComboBox<String> cmbMesas;
    @FXML private Label lblTotalText, lblPedido;
    @FXML private TableView<Platillo> tablaPedido;
    @FXML private TableColumn<Platillo, String> colArticulo;
    @FXML private TableColumn<Platillo, Integer> colCant;
    @FXML private TableColumn<Platillo, Double> colTotal;

    private ObservableList<Platillo> listaPedido = FXCollections.observableArrayList();
    private double totalMonto = 0.0;
    private int pedidoActivoActual = -1; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarMesasOcupadas();
        mostrarGrid("Pizzas");
        if (lblPedido != null) lblPedido.setText("0000");
        cmbMesas.setOnAction(event -> cargarPedidoMesaSeleccionada());
    }

    private void configurarTabla() {
        colArticulo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("precio")); 
        tablaPedido.setItems(listaPedido);
    }

    private void cargarMesasOcupadas() {
        MySQLConnect mysql = new MySQLConnect();
        try (Connection con = mysql.connection()) {
            if (con == null) return;
            try (PreparedStatement ps = con.prepareStatement("SELECT idMesa FROM mesa WHERE estado = 'Ocupada'")) {
                ResultSet rs = ps.executeQuery();
                cmbMesas.getItems().clear();
                while (rs.next()) {
                    cmbMesas.getItems().add(String.valueOf(rs.getInt("idMesa")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarPedidoMesaSeleccionada() {
        if (cmbMesas.getValue() == null) return;
        try {
            int idMesa = Integer.parseInt(cmbMesas.getValue());
            MySQLConnect mysql = new MySQLConnect();
            Connection con = mysql.connection();
            if (con != null) {
                PedidoDAO pedidoDAO = new PedidoDAO(con);
                PlatilloDAO platilloDAO = new PlatilloDAO(con);
                pedidoActivoActual = pedidoDAO.obtenerPedidoActivoPorMesa(idMesa);
                listaPedido.clear();
                if (pedidoActivoActual != -1) {
                    if (lblPedido != null) lblPedido.setText(String.format("%04d", pedidoActivoActual));
                    List<Platillo> platillosBD = platilloDAO.obtenerPlatillosPorOrden(pedidoActivoActual);
                    for (Platillo p : platillosBD) { p.setPrecio(p.getPrecio() * p.getCantidad()); }
                    listaPedido.setAll(platillosBD);
                } else {
                    if (lblPedido != null) lblPedido.setText("Nuevo");
                }
                calcularTotal();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    void clicEnviarChef(ActionEvent event) {
        if (listaPedido.isEmpty() || cmbMesas.getValue() == null) {
            mostrarAlerta("Error", "Verifica la mesa y los platillos.");
            return;
        }
        int idMesa = Integer.parseInt(cmbMesas.getValue());
        int idEmpleado = App.usuarioLogueado.getId(); 

        MySQLConnect mysql = new MySQLConnect();
        try (Connection con = mysql.connection()) {
            PedidoDAO pedidoDAO = new PedidoDAO(con);
            if (pedidoActivoActual == -1) {
                pedidoActivoActual = pedidoDAO.crearNuevoPedido(idMesa, idEmpleado);
            }
            pedidoDAO.guardarDetallesPedido(pedidoActivoActual, listaPedido);
            mostrarAlertaExito("¡Comanda Enviada!", "La orden #" + pedidoActivoActual + " está en cocina.");
            limpiarPantalla();
        } catch (SQLException e) { mostrarAlerta("Error", e.getMessage()); }
    }

    @FXML
    void clicBorrarPedido(ActionEvent event) {
        listaPedido.clear();
        calcularTotal();
    }

    @FXML
    void clicEliminarRenglon(ActionEvent event) {
        Platillo seleccionado = tablaPedido.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getCantidad() > 1) {
                double precioUnitario = seleccionado.getPrecio() / seleccionado.getCantidad();
                seleccionado.setCantidad(seleccionado.getCantidad() - 1);
                seleccionado.setPrecio(precioUnitario * seleccionado.getCantidad());
                tablaPedido.refresh();
            } else {
                listaPedido.remove(seleccionado);
            }
            calcularTotal();
        } else {
            mostrarAlerta("Aviso", "Selecciona un platillo de la tabla.");
        }
    }

    private void agregarAlTicket(String nombreItem, double precioBase) {
        if (cmbMesas.getValue() == null) {
            mostrarAlerta("Aviso", "Selecciona una mesa primero.");
            return;
        }
        boolean encontrado = false;
        for (Platillo p : listaPedido) {
            if (p.getNombre().equals(nombreItem)) {
                p.setCantidad(p.getCantidad() + 1);
                p.setPrecio(precioBase * p.getCantidad());
                encontrado = true; break;
            }
        }
        if (!encontrado) {
            Platillo n = new Platillo(); n.setNombre(nombreItem); n.setCantidad(1); n.setPrecio(precioBase);
            listaPedido.add(n);
        }
        tablaPedido.refresh(); calcularTotal();
    }

    private void calcularTotal() {
        totalMonto = 0;
        for (Platillo p : listaPedido) { totalMonto += p.getPrecio(); }
        lblTotalText.setText("$ " + String.format("%.2f", totalMonto));
    }

    private void limpiarPantalla() {
        listaPedido.clear(); calcularTotal();
        cmbMesas.getSelectionModel().clearSelection();
        pedidoActivoActual = -1;
        if (lblPedido != null) lblPedido.setText("0000");
    }

    private void mostrarGrid(String cat) {
        gridPizza.setVisible(cat.equals("Pizzas"));
        gridBebidas.setVisible(cat.equals("Bebidas"));
        gridPasteles.setVisible(cat.equals("Pasteles"));
        gridExtras.setVisible(cat.equals("Extras"));
        gridEspeciales.setVisible(cat.equals("Especiales"));
    }

    @FXML void clicVerPizzas(ActionEvent event) { mostrarGrid("Pizzas"); }
    @FXML void clicVerBebidas(ActionEvent event) { mostrarGrid("Bebidas"); }
    @FXML void clicVerPasteles(ActionEvent event) { mostrarGrid("Pasteles"); }
    @FXML void clicVerExtras(ActionEvent event) { mostrarGrid("Extras"); }
    @FXML void clicVerEspeciales(ActionEvent event) { mostrarGrid("Especiales"); }

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
    @FXML void clicAddEnsalada(ActionEvent event) { agregarAlTicket("Ensalada Fresca", 70.0); }
    @FXML void clicAddEnsaladaRepollo(ActionEvent event) { agregarAlTicket("Ensalada de Repollo", 40.0); }
    @FXML void clicAddNuggetsPollo(ActionEvent event) { agregarAlTicket("Nuggets de Pollo", 75.0); }
    @FXML void clicAddPanAjo(ActionEvent event) { agregarAlTicket("Pan de Ajo", 45.0); }
    @FXML void clicAddPanAjoQueso(ActionEvent event) { agregarAlTicket("Pan de Ajo con Queso", 55.0); }
    @FXML void clicAddPapasFritas(ActionEvent event) { agregarAlTicket("Papas Fritas", 50.0); }
    @FXML void clicAddSopaDia(ActionEvent event) { agregarAlTicket("Sopa del Día", 60.0); }
    @FXML void clicAddCalzone(ActionEvent event) { agregarAlTicket("Calzone Clásico", 110.0); }
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
            Parent root = App.getFXMLLoader("Dashboard").load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    private void mostrarAlertaExito(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setContentText(m); a.showAndWait();
    }
}