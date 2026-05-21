package com.mycompany.restaurante.controller;

import com.mycompany.restaurante.App;
import com.mycompany.restaurante.modelo.pojo.DetalleFactura;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;
import com.mycompany.restaurante.dao.DetalleFacturaDAO;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph; //para el pdf

public class FacturaController implements Initializable {
    
    //cliente
    @FXML
    private TextField txtRFC;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCP;

    @FXML
    private TextField txtCorreo;

    @FXML
    private ComboBox<String> cbUsoCFDI;

    @FXML
    private ComboBox<String> cbRegimenReceptor;
    
    //botones para la factura 

    @FXML
    private TextField txtFolio;

    @FXML
    private TextField txtFecha;

    @FXML
    private ComboBox<String> cbFormaPago;

    //detalles de la tabla para la factura 

    @FXML
    private TableView<DetalleFactura> tvFactura;

    @FXML
    private TableColumn<DetalleFactura, String> colClave;

    @FXML
    private TableColumn<DetalleFactura, Integer> colCantidad;

    @FXML
    private TableColumn<DetalleFactura, String> colUnidad;

    @FXML
    private TableColumn<DetalleFactura, String> colPlatillo;

    @FXML
    private TableColumn<DetalleFactura, Double> colPrecio;

    @FXML
    private TableColumn<DetalleFactura, Double> colTotal;


    @FXML
    private ComboBox<String> cbAgregarConcepto;

    @FXML
    private TextField txtConceptoConsumo;

    @FXML
    private ComboBox<String> cbRegimenFiscalAbajo;

    @FXML
    private TextField txtSubtotal;

    @FXML
    private TextField txtIVA;

    @FXML
    private TextField txtTotalGeneral;
//btones
    @FXML
    private Button btnVolver;

    @FXML
    private Button btnGenerarFactura;
    
    private int idMesa;
    
    private DetalleFacturaDAO dao =
    new DetalleFacturaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) { //combobox cfd
        cbUsoCFDI.getItems().addAll(
            "G01 - Adquisición de mercancías",
            "G03 - Gastos en general",
            "D01 - Honorarios médicos, dentales y gastos hospitalarios",
            "D10 - Pagos por servicios educativos (colegiaturas)",
            "S01 - Sin efectos fiscales"
        );

        cbUsoCFDI.setValue(
            "G03 - Gastos en general"
        ); //combobox regimen fixcal 
        cbRegimenReceptor.getItems().addAll(
            "601 - General de Ley Personas Morales",
            "603 - Personas Morales con Fines no Lucrativos",
            "605 - Sueldos y Salarios e Ingresos Asimilados a Salarios",
            "606 - Arrendamiento",
            "612 - Personas Físicas con Actividades Empresariales y Profesionales",
            "616 - Sin obligaciones fiscales",
            "621 - Incorporación Fiscal",
            "625 - Régimen de Actividades Empresariales con ingresos a través de Plataformas",
            "626 - Régimen Simplificado de Confianza (RESICO)"
        );

        cbRegimenReceptor.setValue(
            "616 - Sin obligaciones fiscales"
        );

        cbFormaPago.getItems().addAll(
            "01 - Efectivo",
            "28 - Tarjeta de Débito",
            "03 - Transferencia"
        );

        cbFormaPago.setValue(
            "01 - Efectivo"
        );  //fecha segun autimica 
        DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        txtFecha.setText(
            LocalDateTime.now().format(formato)
        );

        colClave.setCellValueFactory(
            new PropertyValueFactory<>("claveProdServ")
        );

        colCantidad.setCellValueFactory(
            new PropertyValueFactory<>("cantidad")
        );

        colUnidad.setCellValueFactory(
            new PropertyValueFactory<>("unidad")
        );

        colPlatillo.setCellValueFactory(
            new PropertyValueFactory<>("platillo")
        );

        colPrecio.setCellValueFactory(
            new PropertyValueFactory<>("precioUnitario")
        );

        colTotal.setCellValueFactory(
            new PropertyValueFactory<>("total")
        );
    }
    
    

//boton oara generar la factura y el pdf 
    @FXML
    
    private void btnGenerarFacturaAction() {

    // Validar RFC
    if (!txtRFC.getText().matches("[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}")) {

        mostrarAlerta(
            "RFC inválido",
            "Formato incorrecto.\nEjemplo: ABCD010203EF1"
        );

        return;
    }

    try {

        // Nombre del archivo PDF
        String ruta =
            "Factura_" + txtRFC.getText() + ".pdf";

        // Crear escritor PDF
        PdfWriter writer =
            new PdfWriter(ruta);

        // Crear documento PDF
        PdfDocument pdf =
            new PdfDocument(writer);

        // Documento visual
        Document document =
            new Document(pdf);

        
        document.add(
            new Paragraph("FACTURA RESTAURANTE")
                .setBold()
                .setFontSize(20)
        );

//cliente
        document.add(
            new Paragraph(
                "RFC: " + txtRFC.getText()
            )
        );

        document.add(
            new Paragraph(
                "Cliente: " + txtNombre.getText()
            )
        );

        document.add(
            new Paragraph(
                "Correo: " + txtCorreo.getText()
            )
        );

        document.add(
            new Paragraph(
                "Uso CFDI: " +
                cbUsoCFDI.getValue()
            )
        );

        document.add(
            new Paragraph(
                "Régimen Fiscal: " +
                cbRegimenReceptor.getValue()
            )
        );

        document.add(
            new Paragraph(" ")
        );

//productos de la tabla 

        document.add(
            new Paragraph("PRODUCTOS")
                .setBold()
        );

        for (DetalleFactura detalle : tvFactura.getItems()) {

            String linea =
                detalle.getPlatillo()
                + " | Cantidad: "
                + detalle.getCantidad()
                + " | Precio: $"
                + detalle.getPrecioUnitario()
                + " | Total: $"
                + detalle.getTotal();

            document.add(
                new Paragraph(linea)
            );
        }

        document.add(
            new Paragraph(" ")
        );


        document.add(
            new Paragraph(
                "TOTAL GENERAL: $" +
                txtTotalGeneral.getText()
            )
            .setBold()
            .setFontSize(16)
        );
        
         // Cerrar documento PDF
         
         document.close(); 
// Abrir automáticamente el PDF
         
         java.awt.Desktop.getDesktop().open(
                 new java.io.File(ruta)
         );
         
         
         mostrarAlerta(
                 "Éxito",
                 "Factura PDF generada correctamente."
         );



    } catch (Exception e) {

        e.printStackTrace();

        mostrarAlerta(
            "Error",
            "No se pudo generar el PDF."
        );
    }
}


//btoon para regresar

    @FXML
    void volverDashboard(ActionEvent event) {

        try {

            Parent root =
                App.getFXMLLoader("Dashboard").load();

            Stage stage =
                (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }


    private void mostrarAlerta(
        String titulo,
        String mensaje
    ) {

        Alert alerta =
            new Alert(Alert.AlertType.WARNING);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
    
    public void inicializarFactura(int idMesa) {

    // Guardamos la mesa actual
    this.idMesa = idMesa;

    // Cargar productos a la tabla
    tvFactura.setItems(
        dao.obtenerDetallesFactura(idMesa)
    );

    // Calcular subtotal
    double subtotal =
        dao.obtenerSubtotalMesa(idMesa);

    // IVA
    double iva = subtotal * 0.16;

    // Total
    double total = subtotal + iva;

    // Mostrar en TextFields
    txtSubtotal.setText(
        String.format("%.2f", subtotal)
    );

    txtIVA.setText(
        String.format("%.2f", iva)
    );

    txtTotalGeneral.setText(
        String.format("%.2f", total)
    );
}
}