package vallegrande.edu.pe.gestion_eventos.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/participantes.fxml")
        );

        Scene scene = new Scene(loader.load(), 1150, 700);

        stage.setTitle("Sistema de Gestión de Participantes");

        stage.setScene(scene);

        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
