package view;

import dao.UsuarioDAO;
import database.conexionDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        conexionDB.inicializarBaseDeDatos();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.crearTablaUsuarios();

        String viewPath = "/view/Login.fxml";
        String title = "Members Check";

        if (usuarioDAO.estaVacia()) {
            viewPath = "/view/Registro.fxml";
            title = "Configuración Inicial - Dojo";
        }

        Parent root = FXMLLoader.load(getClass().getResource(viewPath));
        Scene scene = new Scene(root, 400, 500);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);

        // --- El ícono DEBE ir adentro de este método, antes del show() ---
        try {
            // Recuerda: Aquí debe ser el archivo .png, NO el .ico
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/logo.png")));
        } catch (Exception e) {
            System.out.println("No se encontró la imagen del logo.");
        }

        // Mostramos y centramos la ventana una sola vez al final
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}