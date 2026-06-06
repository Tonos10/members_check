package dao;

import database.conexionDB;
import modelo.pago_modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class pago_dao {

    public boolean save_payment_data(pago_modelo pago) {
        String sql = "INSERT INTO pagos (id_alumno, fecha_pago, monto, metodo_pago) VALUES (?, ?, ?, ?)";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, pago.get_id_alumno());
            pstmt.setString(2, pago.get_fecha_pago());
            pstmt.setDouble(3, pago.get_monto());
            pstmt.setString(4, pago.get_metodo_pago());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar el pago: " + e.getMessage());
            return false;
        }
    }

    public List<pago_modelo> get_all_payments() {
        List<pago_modelo> lista_pagos = new ArrayList<>();
        String sql = "SELECT p.id, p.id_alumno, a.nombre as nombre_alumno, p.fecha_pago, p.monto, p.metodo_pago " +
                     "FROM pagos p INNER JOIN alumnos a ON p.id_alumno = a.id ORDER BY p.fecha_pago DESC";
                     
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                pago_modelo p = new pago_modelo(
                    rs.getInt("id"),
                    rs.getInt("id_alumno"),
                    rs.getString("nombre_alumno"),
                    rs.getString("fecha_pago"),
                    rs.getDouble("monto"),
                    rs.getString("metodo_pago")
                );
                lista_pagos.add(p);
            }
            System.out.println("DEBUG: Se cargaron " + lista_pagos.size() + " pagos de la base de datos.");
        } catch (SQLException e) {
            System.err.println("ERROR SQL EN get_all_payments:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Estado SQL: " + e.getSQLState());
            e.printStackTrace();
        }
        return lista_pagos;
    }

    // Consulta para la tarjeta 1: Ingresos del mes
    public double get_ingresos_del_mes(String mes_anio) {
        String sql = "SELECT SUM(monto) FROM pagos WHERE fecha_pago LIKE ?";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mes_anio + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0.0;
    }

    // Consulta para la tarjeta 2: Pagos pendientes
    public int get_alumnos_pendientes(String mes_anio) {
        String sql = "SELECT COUNT(*) FROM alumnos WHERE id NOT IN (SELECT id_alumno FROM pagos WHERE fecha_pago LIKE ?)";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mes_anio + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // Consulta para la tarjeta 3: Total de alumnos
    public int get_total_alumnos() {
        String sql = "SELECT COUNT(*) FROM alumnos";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // Borrar pagos de un mes específico (reinicio manual)
    public boolean delete_payments_by_month(String mes_anio) {
        String sql = "DELETE FROM pagos WHERE fecha_pago LIKE ?";
        try (Connection conn = conexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mes_anio + "%");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al borrar pagos del mes: " + e.getMessage());
            return false;
        }
    }
}