package dao;
import database.conexionDB;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class AlumnoDAO {

    public boolean registrarAlumno(Alumno alumno) {
        String sql_alumno = "INSERT INTO alumnos (nombre, cinta, fecha_ingreso, telefono, fecha_nacimiento, metodo_pago_preferido) VALUES (?, ?, ?, ?, ?, ?)";
        String sql_pago = "INSERT INTO pagos (id_alumno, fecha_pago, monto, metodo_pago) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = conexionDB.conectar()) {
            conn.setAutoCommit(false); // Iniciar transacción
            
            int generated_id = -1;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql_alumno, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, alumno.getNombre());
                pstmt.setString(2, alumno.getNivel());
                pstmt.setString(3, alumno.getFecha() != null ? alumno.getFecha().toString() : "");
                pstmt.setString(4, alumno.getTelefono());
                pstmt.setString(5, alumno.getFechaNacimiento() != null ? alumno.getFechaNacimiento().toString() : "");
                pstmt.setString(6, alumno.get_metodo_pago_preferido());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generated_id = rs.getInt(1);
                    }
                }
            }
            
            if (generated_id != -1) {
                try (PreparedStatement pstmt_pago = conn.prepareStatement(sql_pago)) {
                    pstmt_pago.setInt(1, generated_id);
                    pstmt_pago.setString(2, alumno.getFecha() != null ? alumno.getFecha().toString() : LocalDate.now().toString());
                    pstmt_pago.setDouble(3, alumno.get_cantidad_pagada()); // Monto capturado
                    pstmt_pago.setString(4, alumno.get_metodo_pago_preferido() != null ? alumno.get_metodo_pago_preferido() : "Efectivo");
                    pstmt_pago.executeUpdate();
                }
            }
            
            conn.commit(); // Confirmar transacción
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar alumno y pago inicial: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarAlumno(Alumno alumno) {
        String sql = "UPDATE alumnos SET nombre = ?, cinta = ?, fecha_ingreso = ?, telefono = ?, fecha_nacimiento = ?, metodo_pago_preferido = ? WHERE id = ?";
        String sql_update_pago = "UPDATE pagos SET monto = ?, metodo_pago = ? WHERE id_alumno = ? AND id = (SELECT MAX(id) FROM pagos WHERE id_alumno = ?)";
        try (Connection conn = conexionDB.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, alumno.getNombre());
                pstmt.setString(2, alumno.getNivel());
                pstmt.setString(3, alumno.getFecha() != null ? alumno.getFecha().toString() : "");
                pstmt.setString(4, alumno.getTelefono());
                pstmt.setString(5, alumno.getFechaNacimiento() != null ? alumno.getFechaNacimiento().toString() : "");
                pstmt.setString(6, alumno.get_metodo_pago_preferido());
                pstmt.setInt(7, alumno.getId());
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt_pago = conn.prepareStatement(sql_update_pago)) {
                pstmt_pago.setDouble(1, alumno.get_cantidad_pagada());
                pstmt_pago.setString(2, alumno.get_metodo_pago_preferido());
                pstmt_pago.setInt(3, alumno.getId());
                pstmt_pago.setInt(4, alumno.getId());
                pstmt_pago.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar alumno: " + e.getMessage());
            return false;
        }
    }

    public List<Alumno> obtenerTodos() {
        List<Alumno> lista_alumnos = new ArrayList<>();
        // Hacemos una subconsulta para traer el último método de pago de la tabla pagos, o usar el preferido si no hay pagos.
        String sql = "SELECT a.*, " +
                     "COALESCE((SELECT p.metodo_pago FROM pagos p WHERE p.id_alumno = a.id ORDER BY p.fecha_pago DESC LIMIT 1), a.metodo_pago_preferido, 'Efectivo') AS metodo_calculado, " +
                     "COALESCE((SELECT p.monto FROM pagos p WHERE p.id_alumno = a.id ORDER BY p.fecha_pago DESC LIMIT 1), 500.0) AS ultimo_monto " +
                     "FROM alumnos a";
                     
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String fechaStr = rs.getString("fecha_ingreso");
                LocalDate fechaIngreso = (fechaStr != null && !fechaStr.isEmpty()) ? LocalDate.parse(fechaStr) : LocalDate.now();

                Alumno alumno = new Alumno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("cinta"),
                        fechaIngreso
                );
                alumno.setTelefono(rs.getString("telefono"));
                String fNac = rs.getString("fecha_nacimiento");
                if (fNac != null && !fNac.isEmpty()) {
                    alumno.setFechaNacimiento(LocalDate.parse(fNac));
                }
                
                String metodo_pago = rs.getString("metodo_calculado");
                System.out.println("DEBUG: Método recuperado de SQLite para " + alumno.getNombre() + ": " + metodo_pago);
                
                if (metodo_pago != null) {
                    alumno.set_metodo_pago_preferido(metodo_pago);
                }

                double ultimo_monto = rs.getDouble("ultimo_monto");
                alumno.set_cantidad_pagada(ultimo_monto);
                
                lista_alumnos.add(alumno);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de alumnos: " + e.getMessage());
        }
        return lista_alumnos;
    }

    public void migrarBaseDeDatos() {
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement("ALTER TABLE alumnos ADD COLUMN telefono TEXT;")) {
            pstmt.executeUpdate();
        } catch (SQLException e) {}
        
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement("ALTER TABLE alumnos ADD COLUMN fecha_nacimiento TEXT;")) {
            pstmt.executeUpdate();
        } catch (SQLException e) {}
        
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement("ALTER TABLE alumnos ADD COLUMN metodo_pago_preferido TEXT DEFAULT 'Efectivo';")) {
            pstmt.executeUpdate();
        } catch (SQLException e) {}
    }

    public boolean eliminarAlumno(int id) {
        String sql = "DELETE FROM alumnos WHERE id = ?";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar alumno: " + e.getMessage());
            return false;
        }
    }
}