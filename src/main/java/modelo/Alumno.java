package modelo;
import  java.time.LocalDate;
public class Alumno {
    private int id;
    private String nombre;
    private String nivel;
    private LocalDate fecha;

    public Alumno() {
    }
    // generador de id
    public Alumno(String nombre, String nivel, LocalDate fecha) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.fecha = fecha;
    }
    public  Alumno(int id, String nombre, String nivel, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.fecha = fecha;
    }

    public int getId() {return  id;}
    public void setId(int id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getNivel() {return nivel;}
    public void setNivel(String nivel) {this.nivel = nivel;}

    public LocalDate getFecha() {return fecha;}
    public void setFecha(LocalDate fecha) {this.fecha = fecha;}

}
