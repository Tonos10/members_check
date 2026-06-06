package controller;

import dao.pago_dao;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.pago_modelo;
import util.pdf_generator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;

public class PagosController {

    @FXML private Label lbl_ingresos;
    @FXML private Label lbl_pendientes;
    @FXML private Label lbl_alumnos;
    
    @FXML private TableView<pago_modelo> tbl_movimientos;
    @FXML private TableColumn<pago_modelo, String> col_alumno;
    @FXML private TableColumn<pago_modelo, String> col_fecha;
    @FXML private TableColumn<pago_modelo, Double> col_monto;
    @FXML private TableColumn<pago_modelo, String> col_metodo;
    @FXML private TableColumn<pago_modelo, Void> col_accion;

    private pago_dao dao_pagos = new pago_dao();

    public void initialize() {
        setup_table();
        load_cards_data();
        load_table_data();
    }

    private void setup_table() {
        col_alumno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get_nombre_alumno()));
        col_fecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get_fecha_pago()));
        col_monto.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get_monto()));
        col_metodo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get_metodo_pago()));

        col_accion.setCellFactory(param -> new TableCell<>() {
            private final Button btn_recibo = new Button("Recibo");
            {
                btn_recibo.getStyleClass().add("btn_recibo");
                btn_recibo.setOnAction(event -> {
                    pago_modelo pago = getTableView().getItems().get(getIndex());
                    generate_and_download_pdf(pago);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn_recibo);
                }
            }
        });

        tbl_movimientos.setRowFactory(tv -> {
            javafx.scene.control.TableRow<pago_modelo> row = new javafx.scene.control.TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && oldItem == null) {
                    javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), row);
                    fade.setFromValue(0.0);
                    fade.setToValue(1.0);
                    javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(500), row);
                    slide.setFromY(20);
                    slide.setToY(0);
                    fade.play();
                    slide.play();
                }
            });
            return row;
        });
    }

    private void load_cards_data() {
        String mes_actual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        double ingresos = dao_pagos.get_ingresos_del_mes(mes_actual);
        int pendientes = dao_pagos.get_alumnos_pendientes(mes_actual);
        int total_alumnos = dao_pagos.get_total_alumnos();

        lbl_ingresos.setText(String.format("$%,.2f MXN", ingresos));
        lbl_pendientes.setText(String.valueOf(pendientes));
        lbl_alumnos.setText(String.valueOf(total_alumnos));
    }

    private void load_table_data() {
        List<pago_modelo> lista = dao_pagos.get_all_payments();
        ObservableList<pago_modelo> observable_list = FXCollections.observableArrayList(lista);
        tbl_movimientos.setItems(observable_list);
    }

    private void generate_and_download_pdf(pago_modelo pago) {
        // En un caso real se usa FileChooser, aquí guardamos localmente por simplicidad
        String file_name = "Recibo_" + pago.get_nombre_alumno().replace(" ", "_") + "_" + pago.get_id() + ".pdf";
        File file = new File(System.getProperty("user.home"), file_name);
        
        boolean exito = pdf_generator.generate_receipt(pago, file.getAbsolutePath());
        
        if (exito) {
            mostrar_alerta(Alert.AlertType.INFORMATION, "Éxito", "Recibo generado en: " + file.getAbsolutePath());
        } else {
            mostrar_alerta(Alert.AlertType.ERROR, "Error", "No se pudo generar el recibo.");
        }
    }

    private void mostrar_alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void resetearEstadisticas() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Reinicio");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas borrar el registro de pagos de este mes? Esta acción no se puede deshacer.");

        var resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String mes_actual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            boolean exito = dao_pagos.delete_payments_by_month(mes_actual);
            
            if (exito) {
                mostrar_alerta(Alert.AlertType.INFORMATION, "Éxito", "Estadísticas del mes reiniciadas correctamente.");
                load_cards_data();
                load_table_data();
            } else {
                mostrar_alerta(Alert.AlertType.ERROR, "Error", "No se pudieron borrar los pagos del mes.");
            }
        }
    }
}