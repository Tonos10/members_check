package modelo;

public class pago_modelo {
    private int id;
    private int id_alumno;
    private String nombre_alumno;
    private String fecha_pago;
    private double monto;
    private String metodo_pago;

    public pago_modelo(int id, int id_alumno, String nombre_alumno, String fecha_pago, double monto, String metodo_pago) {
        this.id = id;
        this.id_alumno = id_alumno;
        this.nombre_alumno = nombre_alumno;
        this.fecha_pago = fecha_pago;
        this.monto = monto;
        this.metodo_pago = metodo_pago;
    }

    public pago_modelo(int id_alumno, String fecha_pago, double monto, String metodo_pago) {
        this.id_alumno = id_alumno;
        this.fecha_pago = fecha_pago;
        this.monto = monto;
        this.metodo_pago = metodo_pago;
    }

    public int get_id() { return id; }
    public void set_id(int id) { this.id = id; }

    public int get_id_alumno() { return id_alumno; }
    public void set_id_alumno(int id_alumno) { this.id_alumno = id_alumno; }

    public String get_nombre_alumno() { return nombre_alumno; }
    public void set_nombre_alumno(String nombre_alumno) { this.nombre_alumno = nombre_alumno; }

    public String get_fecha_pago() { return fecha_pago; }
    public void set_fecha_pago(String fecha_pago) { this.fecha_pago = fecha_pago; }

    public double get_monto() { return monto; }
    public void set_monto(double monto) { this.monto = monto; }

    public String get_metodo_pago() { return metodo_pago; }
    public void set_metodo_pago(String metodo_pago) { this.metodo_pago = metodo_pago; }
}