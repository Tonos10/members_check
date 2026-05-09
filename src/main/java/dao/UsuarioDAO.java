package dao;

import database.conexionDB; // Con tu 'c' minúscula original

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDAO {

    // Método 1: Crea la tabla y el usuario por defecto
    public void crearTablaYAdminPorDefecto() {
        String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL"
                + ")";

        String sqlInsertarAdmin = "INSERT OR IGNORE INTO usuarios (id, username, password) VALUES (1, 'admin', 'admin123')";

        // Usamos conexionDB.conectar() respetando tu import
        try (Connection conn = conexionDB.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlCrearTabla);
            stmt.execute(sqlInsertarAdmin);

            System.out.println("Tabla de usuarios y cuenta admin verificadas con éxito.");

        } catch (SQLException e) {
            System.err.println("Error crítico al crear la tabla usuarios: " + e.getMessage());
        }
    }

    // Método 2: ¡EL QUE FALTABA! Verifica si el usuario y la contraseña coinciden en la base de datos
    public boolean validarLogin(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Inyectamos los valores de forma segura para evitar Hackeos (Inyección SQL)
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            // Si rs.next() es true, significa que encontró un usuario con esa clave exacta
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Error al validar el login: " + e.getMessage());
            return false;
        }
    }
}