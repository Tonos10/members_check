package controller;

import dao.UsuarioDAO;
import modelo.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PerfilModalController {

    @FXML
    private TextField txt_new_username;

    @FXML
    private PasswordField pwd_new_password;

    @FXML
    private PasswordField pwd_confirm_password;

    @FXML
    private Button btn_save_profile;

    private final UsuarioDAO user_database_dao = new UsuarioDAO();
    private Usuario active_admin_user;

    @FXML
    public void initialize() {
        load_current_profile_data();
    }

    private void load_current_profile_data() {
        active_admin_user = user_database_dao.get_current_admin_user();
        if (active_admin_user != null) {
            txt_new_username.setText(active_admin_user.getUsername());
        }
    }

    @FXML
    private void handle_save_profile() {
        String val_username = txt_new_username.getText().trim();
        String val_password = pwd_new_password.getText().trim();
        String val_confirm = pwd_confirm_password.getText().trim();

        if (val_username.isEmpty() || val_password.isEmpty() || val_confirm.isEmpty()) {
            display_alert(Alert.AlertType.WARNING, "Campos Incompletos", "Debes llenar todos los campos.");
            return;
        }

        if (!val_password.equals(val_confirm)) {
            display_alert(Alert.AlertType.ERROR, "Validación Fallida", "Las contraseñas no coinciden. Revisa de nuevo.");
            return;
        }

        if (active_admin_user != null) {
            boolean success_update = user_database_dao.update_admin_user(active_admin_user.getId(), val_username, val_password);
            
            if (success_update) {
                display_alert(Alert.AlertType.INFORMATION, "Perfil Actualizado", "Tus nuevos datos se han guardado exitosamente.");
                close_profile_modal();
            } else {
                display_alert(Alert.AlertType.ERROR, "Database Error", "Fallo al intentar actualizar en SQLite.");
            }
        }
    }

    @FXML
    private void handle_cancel_profile() {
        close_profile_modal();
    }

    private void close_profile_modal() {
        Stage modal_window = (Stage) btn_save_profile.getScene().getWindow();
        modal_window.close();
    }

    private void display_alert(Alert.AlertType type, String header_title, String message) {
        Alert alert_box = new Alert(type);
        alert_box.setTitle(header_title);
        alert_box.setHeaderText(null);
        alert_box.setContentText(message);
        alert_box.showAndWait();
    }
}