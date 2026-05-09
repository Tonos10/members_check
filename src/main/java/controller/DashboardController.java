package controller;

import dao.AlumnoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Alumno;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCinta; // en la vista mostramos "Cinta" pero en el modelo se llama 'nivel'

    @FXML
    private DatePicker dpFechaIngreso;

    @FXML
    private TableView<Alumno> tablaAlumnos;

    @FXML
    private TableColumn<Alumno, Integer> colId;

    @FXML
    private TableColumn<Alumno, String> colNombre;

    @FXML
    private TableColumn<Alumno, String> colCinta;

    @FXML
    private TableColumn<Alumno, java.time.LocalDate> colFechaIngreso;

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final ObservableList<Alumno> alumnos = FXCollections.observableArrayList();

    // Almacena el alumno actualmente seleccionado en la tabla
    private Alumno alumnoSeleccionado = null;

    @FXML
    public void initialize() {
        // Asegúrate de que los nombres aquí coincidan con los getters de modelo.Alumno
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        // en el POJO se llama 'nivel'
        colCinta.setCellValueFactory(new PropertyValueFactory<>("nivel"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tablaAlumnos.setItems(alumnos);
        cargarAlumnos();

        // Configurar listener para que al seleccionar una fila, se rellenen los campos
        tablaAlumnos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                alumnoSeleccionado = newVal;
                rellenarFormulario(newVal);
            }
        });
    }

    @FXML
    private void guardarAlumno() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String cinta = txtCinta.getText() != null ? txtCinta.getText().trim() : "";
        LocalDate fechaIngreso = dpFechaIngreso.getValue();

        if (nombre.isEmpty() || cinta.isEmpty() || fechaIngreso == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Completa todos los campos.");
            return;
        }

        try {
            Alumno alumno = new Alumno();
            alumno.setNombre(nombre);
            // en el modelo el campo se llama 'nivel'
            alumno.setNivel(cinta);
            alumno.setFecha(fechaIngreso);

            boolean ok = alumnoDAO.registrarAlumno(alumno);

            if (ok) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Alumno guardado correctamente.");
                limpiarFormulario();
                cargarAlumnos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el alumno.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el alumno: " + e.getMessage());
        }
    }

    private void cargarAlumnos() {
        try {
            List<Alumno> lista = alumnoDAO.obtenerTodos();
            alumnos.setAll(lista);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los alumnos: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtCinta.clear();
        dpFechaIngreso.setValue(null);
    }

    // Método para rellenar los campos desde un alumno seleccionado
    private void rellenarFormulario(Alumno alumno) {
        if (alumno != null) {
            txtNombre.setText(alumno.getNombre());
            txtCinta.setText(alumno.getNivel());
            dpFechaIngreso.setValue(alumno.getFecha());
        }
    }

    // Método llamado al hacer clic en "Limpiar"
    @FXML
    private void limpiarFormularioUI() {
        limpiarFormulario();
        alumnoSeleccionado = null;
        tablaAlumnos.getSelectionModel().clearSelection();
    }

    // Método llamado al hacer clic en "Actualizar"
    @FXML
    private void actualizarAlumno() {
        if (alumnoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Selecciona un alumno de la tabla para actualizar.");
            return;
        }

        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String cinta = txtCinta.getText() != null ? txtCinta.getText().trim() : "";
        LocalDate fechaIngreso = dpFechaIngreso.getValue();

        if (nombre.isEmpty() || cinta.isEmpty() || fechaIngreso == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Completa todos los campos.");
            return;
        }

        try {
            // Actualizar los datos del objeto seleccionado
            alumnoSeleccionado.setNombre(nombre);
            alumnoSeleccionado.setNivel(cinta);
            alumnoSeleccionado.setFecha(fechaIngreso);

            // Llamar al DAO para actualizar en la base de datos
            boolean ok = alumnoDAO.actualizarAlumno(alumnoSeleccionado);

            if (ok) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Alumno actualizado correctamente.");
                limpiarFormularioUI();
                cargarAlumnos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el alumno.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar: " + e.getMessage());
        }
    }

    // Método llamado al hacer clic en "Eliminar"
    @FXML
    private void eliminarAlumno() {
        if (alumnoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Selecciona un alumno de la tabla para eliminar.");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + alumnoSeleccionado.getNombre() + "?");

        var resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
            try {
                // Eliminar de la base de datos usando el ID
                boolean ok = alumnoDAO.eliminarAlumno(alumnoSeleccionado.getId());

                if (ok) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Alumno eliminado correctamente.");
                    limpiarFormularioUI();
                    cargarAlumnos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el alumno.");
                }

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cierre de sesión");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro que deseas salir?");

        var resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Abrir Login.fxml
                Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Acceso al Sistema - Dojo");
                loginStage.setScene(new Scene(root, 400, 500));
                loginStage.setResizable(false);
                loginStage.show();

                // Cerrar la ventana actual
                Stage current = (Stage) tablaAlumnos.getScene().getWindow();
                current.close();

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver al login: " + e.getMessage());
            }
        }
    }
}
