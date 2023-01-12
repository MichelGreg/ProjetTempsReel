package fr.uha.ensisa.gm.projet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class TrafficApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>();

        FXMLLoader fxmlLoader = new FXMLLoader(TrafficApplication.class.getResource("road-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("Road Trafic");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("ui/red.jpg")));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            // Termination de tous les processus en arrière-plan
            for (Thread thread : threads) {
                thread.interrupt();
            }

            // Termination de l'application
            Platform.exit();
        });

        ProjectMain.run(fxmlLoader, threads);
    }

    public static void main(String[] args) {
        launch();
    }
}