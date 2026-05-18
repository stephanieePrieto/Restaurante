package com.mycompany.restaurante.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class VentanaPizzatronController implements Initializable {

    @FXML private Pane panelMesas;
    @FXML private TextField txtIdCliente;
    @FXML private DatePicker dateFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtPersonas;

    // Conexión a tu base de datos local
    private final String URL = "jdbc:mysql://localhost:3306/restaurante_db";
    private final String USER = "root";
    private final String PASS = "Goku2004";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Pone la fecha de hoy por defecto al abrir la ventana
        dateFecha.setValue(LocalDate.now());
    }

    @FXML
    private void hacerReservaAutomatica() {
        String idCliente = txtIdCliente.getText();
        LocalDate fecha = dateFecha.getValue();
        String hora = txtHora.getText();
        String personasStr = txtPersonas.getText();

        if(idCliente.isEmpty() || fecha == null || hora.isEmpty() || personasStr.isEmpty()) {
            mostrarAlerta("Error", "Llena todos los campos para buscar tu mesa, wey.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            // 1. EL CEREBRO: Busca una mesa que quepa la gente y que NO esté reservada a esa misma hora y fecha
            String sqlBuscarMesa = "SELECT id_mesa, mapa_x, mapa_y FROM mesas " +
                                   "WHERE capacidad >= ? AND id_mesa NOT IN (" +
                                   "SELECT id_mesa FROM reservaciones WHERE fecha = ? AND hora = ? AND estado = 'Confirmada')";
            
            PreparedStatement psMesa = conn.prepareStatement(sqlBuscarMesa);
            psMesa.setInt(1, Integer.parseInt(personasStr));
            psMesa.setString(2, fecha.toString());
            psMesa.setString(3, hora);
            
            ResultSet rsMesa = psMesa.executeQuery();
            
            if (rsMesa.next()) {
                // Si encontró mesa, saca los datos y las coordenadas
                int idMesa = rsMesa.getInt("id_mesa");
                int x = rsMesa.getInt("mapa_x");
                int y = rsMesa.getInt("mapa_y");

                // 2. Guarda la reserva en la BD
                String sqlReserva = "INSERT INTO reservaciones (id_reservacion, id_cliente, id_mesa, fecha, hora, num_personas, estado) VALUES (?, ?, ?, ?, ?, ?, 'Confirmada')";
                PreparedStatement psReserva = conn.prepareStatement(sqlReserva);
                String idUnico = UUID.randomUUID().toString(); // Genera un ID automático perrón
                psReserva.setString(1, idUnico);
                psReserva.setString(2, idCliente);
                psReserva.setInt(3, idMesa);
                psReserva.setString(4, fecha.toString());
                psReserva.setString(5, hora);
                psReserva.setInt(6, Integer.parseInt(personasStr));
                psReserva.executeUpdate();

                // 3. ¡LA MAGIA! Pone al pingüino en la mesa usando las coordenadas de la BD
                dibujarPinguino(x, y);
                
                mostrarAlerta("¡Reserva Exitosa!", "Se te asignó la Mesa " + idMesa + ".\nTu ID único de confirmación es:\n" + idUnico);

            } else {
                mostrarAlerta("Sin Mesas", "El Pizzatron está lleno a esa hora o no hay mesas tan grandes.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Hubo un pedo con la base de datos.");
        }
    }

    @FXML
    private void cancelarReserva() {
        // Lógica básica para cancelar, luego se puede expandir
        mostrarAlerta("Cancelación", "Pronto agregaremos la opción para cancelar reservaciones.");
    }

    // Este método es el que pega la imagen del pingüino encima de la mesa
    private void dibujarPinguino(double x, double y) {
        try {
            Image img = new Image(getClass().getResourceAsStream("pinguino.png")); // NECESITAS ESTA IMAGEN
            ImageView pinguino = new ImageView(img);
            pinguino.setFitWidth(50);
            pinguino.setFitHeight(50);
            pinguino.setLayoutX(x - 25); // Ajuste fino para centrarlo en la mesa
            pinguino.setLayoutY(y - 40);
            
            panelMesas.getChildren().add(pinguino);
        } catch (Exception e) {
            System.out.println("No encontré la imagen pinguino.png, metela a la carpeta wey.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}