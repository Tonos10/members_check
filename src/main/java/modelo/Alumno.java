package modelo;
import java.time.LocalDate;

public class Alumno {
    private int id;
    private String nombre;
    private String nivel;
    private LocalDate fecha;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String metodo_pago_preferido = "Efectivo";
    private double cantidad_pagada = 500.0;

    public Alumno() {
    }
    
    public Alumno(String nombre, String nivel, LocalDate fecha) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.fecha = fecha;
    }
    
    public Alumno(int id, String nombre, String nivel, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.fecha = fecha;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getNivel() {return nivel;}
    public void setNivel(String nivel) {this.nivel = nivel;}

    public LocalDate getFecha() {return fecha;}
    public void setFecha(LocalDate fecha) {this.fecha = fecha;}

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String get_metodo_pago_preferido() { return metodo_pago_preferido; }
    public void set_metodo_pago_preferido(String metodo_pago_preferido) { this.metodo_pago_preferido = metodo_pago_preferido; }

    public double get_cantidad_pagada() { return cantidad_pagada; }
    public void set_cantidad_pagada(double cantidad_pagada) { this.cantidad_pagada = cantidad_pagada; }
}