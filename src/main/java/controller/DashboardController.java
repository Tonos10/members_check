package controller;

import dao.AlumnoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import modelo.Alumno;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DashboardController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private TableView<Alumno> tablaAlumnos;

    @FXML
    private TableColumn<Alumno, String> colNombre;

    @FXML
    private TableColumn<Alumno, java.time.LocalDate> colFechaPago;

    @FXML
    private TableColumn<Alumno, String> colEstado;

    @FXML
    private TableColumn<Alumno, String> colAcciones;

    @FXML
    private TextField txtBuscar;

    @FXML
    private VBox sidebar;

    @FXML
    private Label lblLogo;

    @FXML
    private Button btnNavAlumnos;

    @FXML
    private Button btnNavPagos;

    @FXML
    private Button btnMenu;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private TableColumn<Alumno, String> colTelefono;

    @FXML
    private TableColumn<Alumno, java.time.LocalDate> colFechaNac;

    @FXML
    private TableColumn<Alumno, String> colMetodo;

    private enum SidebarState { EXPANDED, COMPACT, COLLAPSED }
    private SidebarState sidebarState = SidebarState.EXPANDED;
    private Alumno alumnoSeleccionado = null;
    private PauseTransition menuClickDelay;

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final ObservableList<Alumno> alumnos = FXCollections.observableArrayList();

    private Node nodoAlumnos;
    private Node nodoPagos;

    @FXML
    public void initialize() {
        new AlumnoDAO().migrarBaseDeDatos();
        
        colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colFechaPago.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFecha()));
        colTelefono.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelefono()));
        colFechaNac.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getFechaNacimiento()));
        
        // Uso de PropertyValueFactory no funcionaría directamente con snake_case, usamos un lambda callback.
        colMetodo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().get_metodo_pago_preferido()));
        
        // Configuración de la columna Estado (Semáforo Dinámico)
        colEstado.setCellFactory(col -> new TableCell<Alumno, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null || getTableRow().getItem().getFecha() == null) {
                    setGraphic(null);
                } else {
                    Alumno alumno = getTableRow().getItem();
                    long dias = ChronoUnit.DAYS.between(alumno.getFecha(), LocalDate.now());
                    Label lbl = new Label();
                    if (dias <= 20) {
                        lbl.setText("Activo");
                        lbl.getStyleClass().add("pill-verde");
                    } else if (dias <= 30) {
                        lbl.setText("Por Vencer");
                        lbl.getStyleClass().add("pill-naranja");
                    } else {
                        lbl.setText("Vencido");
                        lbl.getStyleClass().add("pill-rojo");
                    }
                    setGraphic(lbl);
                }
            }
        });

        // Configuración de la columna Acciones (Botones de Texto)
        colAcciones.setCellFactory(col -> new TableCell<Alumno, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Button btnEditar = new Button("Editar");
                    btnEditar.getStyleClass().add("btn-accion-editar");
                    Button btnBorrar = new Button("Borrar");
                    btnBorrar.getStyleClass().add("btn-accion-borrar");
                    
                    btnEditar.setOnAction(e -> {
                        alumnoSeleccionado = getTableRow().getItem();
                        mostrarFormulario(); 
                    });
                    
                    btnBorrar.setOnAction(e -> {
                        alumnoSeleccionado = getTableRow().getItem();
                        eliminarAlumno();
                    });

                    HBox pane = new HBox(10, btnEditar, btnBorrar);
                    pane.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(pane);
                }
            }
        });

        // Lógica de Filtrado y Ordenamiento
        FilteredList<Alumno> filteredData = new FilteredList<>(alumnos, b -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(alumno -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return alumno.getNombre().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        SortedList<Alumno> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaAlumnos.comparatorProperty());
        tablaAlumnos.setItems(sortedData);

        cargarAlumnos();

        // Selección rápida para edición
        tablaAlumnos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                alumnoSeleccionado = newVal;
                mostrarFormulario();
            }
        });

        // Configuración del Enrutador (Router)
        nodoAlumnos = mainPane.getCenter();

        tablaAlumnos.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Alumno> row = new javafx.scene.control.TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && oldItem == null) {
                    FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(500), row);
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

        configurarSidebar();
        update_active_nav(btnNavAlumnos, btnNavPagos);
    }

    @FXML
    private void mostrarAlumnos() {
        System.out.println("Regresando a vista de Alumnos");
        aplicar_transicion_panel(nodoAlumnos);
        update_active_nav(btnNavAlumnos, btnNavPagos);
    }

    @FXML
    private void mostrarPagos() {
        System.out.println("Cambiando a vista de Pagos");
        try {
            if (nodoPagos == null) {
                nodoPagos = FXMLLoader.load(getClass().getResource("/view/Pagos.fxml"));
            }
            aplicar_transicion_panel(nodoPagos);
            update_active_nav(btnNavPagos, btnNavAlumnos);
        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO: No se pudo cargar Pagos.fxml");
            e.printStackTrace();
        }
    }

    private void aplicar_transicion_panel(Node nodo) {
        if (mainPane.getCenter() == nodo) return;
        
        nodo.setOpacity(0.0);
        mainPane.setCenter(nodo);
        
        FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(400), nodo);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void update_active_nav(Button active_btn, Button inactive_btn) {
        if (active_btn != null && !active_btn.getStyleClass().contains("sidebar-btn-active")) {
            active_btn.getStyleClass().add("sidebar-btn-active");
        }
        if (inactive_btn != null) {
            inactive_btn.getStyleClass().remove("sidebar-btn-active");
        }
    }

    @FXML
    private void toggleSidebar() {
        // Determine next state: EXPANDED -> COMPACT, COMPACT -> EXPANDED, COLLAPSED -> EXPANDED
        if (sidebarState == SidebarState.EXPANDED) {
            setSidebarState(SidebarState.COMPACT);
        } else if (sidebarState == SidebarState.COMPACT) {
            setSidebarState(SidebarState.EXPANDED);
        } else { // COLLAPSED
            setSidebarState(SidebarState.EXPANDED);
        }
    }

    private void configurarSidebar() {
        if (btnNavAlumnos != null) {
            btnNavAlumnos.setTooltip(new Tooltip("Alumnos"));
            btnNavAlumnos.setGraphic(crearIconoUsuarios());
            btnNavAlumnos.setContentDisplay(ContentDisplay.LEFT);
            btnNavAlumnos.setGraphicTextGap(12);
        }
        if (btnNavPagos != null) {
            btnNavPagos.setTooltip(new Tooltip("Pagos"));
            btnNavPagos.setGraphic(crearIconoPagos());
            btnNavPagos.setContentDisplay(ContentDisplay.LEFT);
            btnNavPagos.setGraphicTextGap(12);
        }
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setTooltip(new Tooltip("Cerrar sesión"));
            btnCerrarSesion.setGraphic(crearIconoSalir());
            btnCerrarSesion.setContentDisplay(ContentDisplay.LEFT);
            btnCerrarSesion.setGraphicTextGap(12);
        }
        if (btnMenu != null) {
            btnMenu.setGraphic(crearIconoMenu());
            btnMenu.setText("");
            btnMenu.setTooltip(new Tooltip("Expandir o contraer menú"));
            btnMenu.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnMenu.setGraphicTextGap(0);
            menuClickDelay = new PauseTransition(javafx.util.Duration.millis(180));
            menuClickDelay.setOnFinished(e -> toggleSidebar());
            btnMenu.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2) {
                    menuClickDelay.stop();
                    setSidebarState(SidebarState.COLLAPSED);
                } else if (evt.getClickCount() == 1) {
                    menuClickDelay.stop();
                    menuClickDelay.playFromStart();
                }
            });
        }

        aplicarEstadoSidebar(false);
    }

    private void setSidebarState(SidebarState state) {
        sidebarState = state;
        aplicarEstadoSidebar(true);
    }

    private void aplicarEstadoSidebar(boolean animar) {
        double target;
        String claseCompacta = "compact";
        String claseColapsada = "collapsed";

        switch (sidebarState) {
            case EXPANDED -> target = 240;
            case COMPACT -> target = 128;
            default -> target = 64;
        }

        Runnable aplicarContenido = () -> {
            boolean expanded = sidebarState == SidebarState.EXPANDED;
            boolean compact = sidebarState == SidebarState.COMPACT;
            boolean collapsed = sidebarState == SidebarState.COLLAPSED;

            if (sidebar != null) {
                sidebar.setMinWidth(target);
                sidebar.setMaxWidth(target);
                sidebar.getStyleClass().remove(claseCompacta);
                sidebar.getStyleClass().remove(claseColapsada);
                if (compact && !sidebar.getStyleClass().contains(claseCompacta)) sidebar.getStyleClass().add(claseCompacta);
                if (collapsed && !sidebar.getStyleClass().contains(claseColapsada)) sidebar.getStyleClass().add(claseColapsada);
            }

            if (lblLogo != null) {
                lblLogo.setManaged(expanded);
                lblLogo.setVisible(expanded);
                lblLogo.setOpacity(expanded ? 1.0 : 0.0);
            }

            configurarBotonSidebar(btnNavAlumnos, "Alumnos", "Alu", expanded, compact, collapsed, crearIconoUsuarios());
            configurarBotonSidebar(btnNavPagos, "Pagos", "Pag", expanded, compact, collapsed, crearIconoPagos());
            configurarBotonSidebar(btnCerrarSesion, "Cerrar Sesión", "Salir", expanded, compact, collapsed, crearIconoSalir());

            if (btnMenu != null) {
                btnMenu.setGraphic(sidebarState == SidebarState.COLLAPSED ? crearIconoExpandir() : crearIconoMenu());
                btnMenu.setTooltip(new Tooltip(sidebarState == SidebarState.COLLAPSED ? "Expandir menú" : "Contraer menú"));
            }
        };

        if (!animar || sidebar == null) {
            aplicarContenido.run();
            return;
        }

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), sidebar.getPrefWidth())),
                new KeyFrame(javafx.util.Duration.millis(220),
                        new KeyValue(sidebar.prefWidthProperty(), target, javafx.animation.Interpolator.EASE_BOTH))
        );

        timeline.setOnFinished(e -> {
            aplicarContenido.run();
            if (lblLogo != null) {
                FadeTransition fade = new FadeTransition(javafx.util.Duration.millis(160), lblLogo);
                fade.setToValue(sidebarState == SidebarState.EXPANDED ? 1.0 : 0.0);
                fade.play();
            }
        });
        timeline.play();
    }

    private void configurarBotonSidebar(Button button, String textoCompleto, String textoCompacto,
                                        boolean expanded, boolean compact, boolean collapsed, SVGPath graphic) {
        if (button == null) return;
        button.setGraphic(graphic);
        button.setTooltip(new Tooltip(textoCompleto));
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(12);
        button.setAlignment(expanded ? Pos.CENTER_LEFT : Pos.CENTER_LEFT);

        if (expanded) {
            button.setText(textoCompleto);
            button.setOpacity(1.0);
            button.setManaged(true);
            return;
        }

        if (compact) {
            button.setText(textoCompacto);
            button.setOpacity(1.0);
            button.setManaged(true);
            button.setContentDisplay(ContentDisplay.LEFT);
            button.setGraphicTextGap(10);
            return;
        }

        button.setText("");
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setGraphicTextGap(0);
        button.setAlignment(Pos.CENTER);
        button.setOpacity(1.0);
        button.setManaged(true);
    }

    private SVGPath crearIconoMenu() {
        return crearIcono("M3 6H21V8H3z M3 11H21V13H3z M3 16H21V18H3z");
    }

    private SVGPath crearIconoExpandir() {
        return crearIcono("M9 6H6v3H4V4h5z M15 6h3V4h-5v2z M6 15v3h3v2H4v-5z M18 15h2v5h-5v-2h3z");
    }

    private SVGPath crearIconoUsuarios() {
        return crearIcono("M12 12a4 4 0 1 0-4-4 4 4 0 0 0 4 4z M4 20a8 8 0 0 1 16 0z");
    }

    private SVGPath crearIconoPagos() {
        return crearIcono("M4 6h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2zm0 4h16V8H4zm0 4h6v2H4z");
    }

    private SVGPath crearIconoSalir() {
        return crearIcono("M10 17l1.4-1.4L8.8 13H20v-2H8.8l2.6-2.6L10 7l-6 6z M4 4h8V2H4a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h8v-2H4z");
    }

    private SVGPath crearIcono(String pathData) {
        SVGPath icon = new SVGPath();
        icon.setContent(pathData);
        icon.setFill(Color.web("#607D8B"));
        icon.getStyleClass().add("sidebar-icon");
        icon.setScaleX(0.9);
        icon.setScaleY(0.9);
        return icon;
    }

    private void cargarAlumnos() {
        try {
            List<Alumno> lista = alumnoDAO.obtenerTodos();
            alumnos.setAll(lista);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los alumnos: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarFormulario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlumnoForm.fxml"));
            Parent root = loader.load();
            
            AlumnoFormController controller = loader.getController();
            controller.setAlumno(alumnoSeleccionado);

            Stage stage = new Stage();
            stage.setTitle(alumnoSeleccionado == null ? "Registrar Alumno" : "Editar Alumno");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.sizeToScene();
            stage.showAndWait();

            alumnoSeleccionado = null; // Resetear selección ANTES de recargar
            tablaAlumnos.getSelectionModel().clearSelection();
            cargarAlumnos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarAlumno() {
        if (alumnoSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar a " + alumnoSeleccionado.getNombre() + "?");

        var resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean ok = alumnoDAO.eliminarAlumno(alumnoSeleccionado.getId());
                if (ok) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Alumno eliminado correctamente.");
                    alumnoSeleccionado = null; // Resetear selección ANTES de recargar
                    tablaAlumnos.getSelectionModel().clearSelection();
                    cargarAlumnos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el alumno.");
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + e.getMessage());
            }
        } else {
            alumnoSeleccionado = null;
            tablaAlumnos.getSelectionModel().clearSelection();
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
                Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Acceso al Sistema - Dojo");
                loginStage.setScene(new Scene(root, 400, 500));
                loginStage.setResizable(false);
                loginStage.show();

                Stage current = (Stage) tablaAlumnos.getScene().getWindow();
                current.close();

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver al login: " + e.getMessage());
            }
        }
    }
}
