package dao;
import database.conexionDB;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class AlumnoDAO {

    // --- 1. CREAR (Insertar un nuevo alumno) ---
    public boolean registrarAlumno(Alumno alumno) {
        // BUENA PRÁCTICA: Usamos '?' para evitar ataques de Inyección SQL
        String sql = "INSERT INTO alumnos (nombre, cinta, fecha_ingreso) VALUES (?, ?, ?)";

        // BUENA PRÁCTICA: try-with-resources. Esto cierra la conexión automáticamente al terminar,
        // evitando fugas de memoria (memory leaks) que harían lenta la PC.
        conexionDB ConexionDB = null;
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getNivel());
            pstmt.setString(3, alumno.getFecha().toString()); // SQLite guarda fechas como texto

            pstmt.executeUpdate();
            return true; // Éxito

        } catch (SQLException e) {
            System.err.println("Error al registrar alumno: " + e.getMessage());
            return false;
        }
    }

    // --- 2. ACTUALIZAR (Editar un alumno existente) ---
    public boolean actualizarAlumno(Alumno alumno) {
        // El UPDATE busca al alumno por su ID para no sobreescribir a otro por error
        String sql = "UPDATE alumnos SET nombre = ?, cinta = ?, fecha_ingreso = ? WHERE id = ?";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, alumno.getNombre());
            pstmt.setString(2, alumno.getNivel());
            pstmt.setString(3, alumno.getFecha().toString());
            pstmt.setInt(4, alumno.getId()); // El 4to '?' es el ID de la condición WHERE

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar alumno: " + e.getMessage());
            return false;
        }
    }

    // --- 3. LEER (Obtener todos los alumnos para mostrarlos en la tabla visual) ---
    public List<Alumno> obtenerTodos() {
        List<Alumno> listaAlumnos = new ArrayList<>();
        String sql = "SELECT * FROM alumnos";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { // ResultSet guarda los datos que devuelve la BD

            while (rs.next()) { // Recorremos fila por fila
                Alumno alumno = new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("cinta"),
                        LocalDate.parse(rs.getString("fecha_ingreso")) // Convertimos el texto de SQLite de vuelta a LocalDate
                );
                listaAlumnos.add(alumno);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener alumnos: " + e.getMessage());
        }
        return listaAlumnos;
    }
}