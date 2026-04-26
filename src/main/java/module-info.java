module com.mycompany.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Fundamental para que OrdenDAO conecte a MySQL
    requires java.base;

    opens com.mycompany.restaurante.controller to javafx.fxml;
    opens com.mycompany.restaurante.modelo.pojo to javafx.base; // Permite que la Tabla de Cocina lea tus POJOs
    
    exports com.mycompany.restaurante;
}