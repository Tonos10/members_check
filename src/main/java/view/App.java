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
        String title = "Acceso al Sistema - Dojo";

        if (usuarioDAO.estaVacia()) {
            viewPath = "/view/Registro.fxml";
            title = "Configuración Inicial - Dojo";
        }

        Parent root = FXMLLoader.load(getClass().getResource(viewPath));
        Scene scene = new Scene(root, 400, 500);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

