package sistemarestaurante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroPlatilloController {

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<String> cmbCategoria;

    @FXML
    public void initialize() {
        // EXACTAMENTE las categorías de tu caso de uso
        cmbCategoria.getItems().addAll("Plato Fuerte", "Entrada", "Postre", "Bebida");
    }

    @FXML
    void clicGuardar(ActionEvent event) {
        String nombre = txtNombre.getText();
        String desc = txtDescripcion.getText();
        String precioTexto = txtPrecio.getText();
        String categoria = cmbCategoria.getValue();

        // VALIDACIÓN FA-01
        if (nombre.trim().isEmpty() || precioTexto.trim().isEmpty() || categoria == null) {
            mostrarAlerta("Datos Incompletos", "⚠️ ALERTA: Nombre, Precio y Categoría son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto); 
            
            Platillo nuevo = new Platillo(nombre, desc, precio, categoria, "default.png");
            PlatilloDAO dao = new PlatilloDAO();
            
            if (dao.registrarPlatillo(nuevo)) {
                mostrarAlerta("Éxito", "✅ ¡Platillo guardado exitosamente en el Restaurante CP!", Alert.AlertType.INFORMATION);
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "❌ Fallo de servidor al conectar con la base de datos.", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "⚠️ El precio debe ser un número válido.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void clicCancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close(); 
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        cmbCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}