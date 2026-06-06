package controller;

import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Button btnRegistrar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void registrarUsuario() {
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";
        String confirmPassword = txtConfirmPassword.getText() != null ? txtConfirmPassword.getText().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Completa todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Las contraseñas no coinciden.");
            return;
        }

        if (usuarioDAO.registrarUsuario(username, password)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente. Ahora puedes iniciar sesión.");
            abrirLogin();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario.");
        }
    }

    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Stage stageActual = (Stage) txtUsername.getScene().getWindow();
            stageActual.setScene(new Scene(root, 400, 500));
            stageActual.setTitle("Acceso al Sistema - Dojo");
            stageActual.centerOnScreen();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el login: " + e.getMessage());
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
