import dao.AlumnoDAO;
import modelo.Alumno;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        database.conexionDB.inicializarBaseDeDatos();
        AlumnoDAO dao = new AlumnoDAO();
        // 1. Instanciamos nuestro DAO (El gerente de la base de datos)
        dao = new AlumnoDAO();

        // 2. Creamos un alumno de prueba usando el constructor sin ID
        // (Usa LocalDate.now() para obtener la fecha de hoy automáticamente)
        Alumno nuevoAlumno = new Alumno("Antonio", "Cinta Blanca", LocalDate.now());

        // 3. Le pedimos al DAO que lo guarde
        boolean exito = dao.registrarAlumno(nuevoAlumno);

        if (exito) {
            System.out.println("¡Alumno guardado correctamente en SQLite!");
        } else {
            System.out.println("Hubo un error al guardar.");
        }

        // 4. PRUEBA DE LECTURA: Le pedimos al DAO que nos traiga todos los alumnos
        System.out.println("\n--- Lista de Alumnos en la BD ---");
        List<Alumno> lista = dao.obtenerTodos();

        for (Alumno a : lista) {
            System.out.println("ID: " + a.getId() + " | Nombre: " + a.getNombre() + " | Cinta: " + a.getNivel() + " | Ingreso: " + a.getFecha());
        }
    }
}