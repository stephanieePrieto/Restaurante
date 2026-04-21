package sistemarestaurante;

import java.sql.*;

public class UsuarioDAO {
    
    private static final String URL = "jdbc:mysql://localhost:3306/restaurante_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Goku2004"; // Pon tu contraseña real

    public Usuario validarLogin(String user, String pass) {
        // ¡OJO AQUÍ! Los signos de interrogación (?) son obligatorios
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(rs.getString("usuario"), null, rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.out.println("Error en Login: " + e.getMessage());
        }
        return null; 
    }
}