package sistemarestaurante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlatilloDAO {
    
    // Cambia estos datos por los de tu servidor local
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Goku2004";

    public boolean registrarPlatillo(Platillo platillo) {
        String sql = "INSERT INTO platillos (nombre, descripcion, precio, categoria, imagen) VALUES (?,?,?,?,?)";
        
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, platillo.getNombre());
            ps.setString(2, platillo.getDescripcion());
            ps.setDouble(3, platillo.getPrecio());
            ps.setString(4, platillo.getCategoria());
            ps.setString(5, platillo.getImagen());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se guardó con éxito
            
        } catch (SQLException e) {
            // Ex-01: Error al guardar en la base de datos
            System.err.println(">> EXCEPCIÓN TÉCNICA: Error de base de datos - " + e.getMessage());
            return false;
        }
    }
}