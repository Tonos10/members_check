package controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.AlumnoDAO;
import javafx.scene.control.Alert;
import modelo.Alumno;
import java.time.LocalDate;

public class AlumnoFormController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaIngreso;
    @FXML private TextField txtTelefono;
    @FXML private DatePicker dpFechaNac;
    @FXML private ComboBox<String> combo_box_pago;
    @FXML private TextField txt_cantidad_pagada;

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private Alumno alumnoActual = null;

    public void setAlumno(Alumno alumno) {
        if (alumno != null) {
            this.alumnoActual = alumno;
            cargarDatos();
        }
    }

    @FXML
    public void initialize() {
        alumnoDAO.migrarBaseDeDatos(); // Aseguramos que la columna existe antes de cualquier guardado
        combo_box_pago.getItems().addAll("Efectivo", "Transferencia");
        combo_box_pago.setValue("Efectivo");
        
        if (txt_cantidad_pagada != null) {
             txt_cantidad_pagada.setText("500.0");
        }
    }

    public void cargarDatos() {
        if (alumnoActual != null) {
            txtNombre.setText(alumnoActual.getNombre());
            dpFechaIngreso.setValue(alumnoActual.getFecha());
            txtTelefono.setText(alumnoActual.getTelefono());
            dpFechaNac.setValue(alumnoActual.getFechaNacimiento());
            
            String select_metodo_pago = alumnoActual.get_metodo_pago_preferido();
            if (select_metodo_pago != null && (select_metodo_pago.equals("Transferencia") || select_metodo_pago.equals("Efectivo"))) {
                combo_box_pago.setValue(select_metodo_pago);
            } else {
                combo_box_pago.setValue("Efectivo");
            }
            
            if (txt_cantidad_pagada != null) {
                 txt_cantidad_pagada.setText(String.valueOf(alumnoActual.get_cantidad_pagada()));
            }
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void update_alumno_data() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        LocalDate fechaIngreso = dpFechaIngreso.getValue();
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";
        LocalDate fechaNac = dpFechaNac.getValue();
        String metodo_pago_seleccionado = combo_box_pago.getValue();
        
        double cantidad_pagada = 500.0;
        if (txt_cantidad_pagada != null && !txt_cantidad_pagada.getText().trim().isEmpty()) {
            try {
                cantidad_pagada = Double.parseDouble(txt_cantidad_pagada.getText().trim());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La cantidad pagada debe ser un número válido.");
                return;
            }
        }

        if (nombre.isEmpty() || fechaIngreso == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Completa los campos obligatorios.");
            return;
        }

        try {
            boolean isNew = (alumnoActual == null);
            Alumno alumno = isNew ? new Alumno() : alumnoActual;

            alumno.setNombre(nombre);
            alumno.setFecha(fechaIngreso);
            alumno.setTelefono(telefono);
            alumno.setFechaNacimiento(fechaNac);
            alumno.setNivel("N/A"); // Default fallback
            alumno.set_metodo_pago_preferido(metodo_pago_seleccionado);
            alumno.set_cantidad_pagada(cantidad_pagada);

            boolean ok;
            if (isNew) {
                ok = alumnoDAO.registrarAlumno(alumno);
            } else {
                ok = alumnoDAO.actualizarAlumno(alumno);
            }

            if (ok) {
                String mensaje = isNew ? "Alumno guardado correctamente." : "Alumno actualizado correctamente.";
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", mensaje);
                cerrarVentana();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo procesar la solicitud.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}