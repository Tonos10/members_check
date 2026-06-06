package dao;

import database.conexionDB; // Con tu 'c' minúscula original

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDAO {

    // Método 1: Crea la tabla si no existe
    public void crearTablaUsuarios() {
        String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL"
                + ")";

        try (Connection conn = conexionDB.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlCrearTabla);
            System.out.println("Tabla de usuarios verificada con éxito.");

        } catch (SQLException e) {
            System.err.println("Error crítico al crear la tabla usuarios: " + e.getMessage());
        }
    }

    // Verifica si la tabla de usuarios está vacía
    public boolean estaVacia() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Connection conn = conexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si la tabla está vacía: " + e.getMessage());
        }
        return true; // Si hay error, asumimos vacía para seguridad del primer inicio
    }

    // Registra un nuevo usuario
    public boolean registrarUsuario(String username, String password) {
        String sql = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método 2: Verifica si el usuario y la contraseña coinciden
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