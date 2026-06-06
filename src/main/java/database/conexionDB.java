package database;
import  java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionDB {
    //ruta donde se aguarda la bsase de datos
    private static final String URL = "jdbc:sqlite:database.db";
    public static Connection conectar() {
        Connection conexion = null;
        // comprovacion de conexion con BD
        try {
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexion exitosa con la base de datos");

        } catch (SQLException e) {
            System.err.println("Error al conectar la base de datos");
        }
        return conexion;
    }

    public static void inicializarBaseDeDatos() {
        String sql_alumnos = "CREATE TABLE IF NOT EXISTS alumnos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "cinta TEXT NOT NULL,"
                + "fecha_ingreso TEXT NOT NULL,"
                + "metodo_pago_preferido TEXT DEFAULT 'Efectivo'"
                + ");";

        String sql_pagos = "CREATE TABLE IF NOT EXISTS pagos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_alumno INTEGER NOT NULL,"
                + "fecha_pago TEXT NOT NULL,"
                + "monto REAL NOT NULL,"
                + "metodo_pago TEXT NOT NULL,"
                + "FOREIGN KEY (id_alumno) REFERENCES alumnos (id)"
                + ");";

        try (Connection conn = conectar();
             java.sql.Statement stmt = conn.createStatement()) {

            stmt.execute(sql_alumnos);
            stmt.execute(sql_pagos);
            System.out.println("Estructura de la base de datos verificada y lista.");

        } catch (SQLException e) {
            System.err.println("Error al inicializar las tablas: " + e.getMessage());
        }
    }
}
