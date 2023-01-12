package fr.uha.ensisa.gm.projet;

import fr.uha.ensisa.gm.projet.threads.CarThread;
import fr.uha.ensisa.gm.projet.threads.SwitchLightsThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ProjectMain {
    private static final Image carImg = new Image(ProjectMain.class.getResourceAsStream("ui/car.png"));

    public static GridWrapper gridWrapper;
    public static Semaphore vSem = new Semaphore(1);
    public static Semaphore hSem = new Semaphore(0);
    public static void run(FXMLLoader fxmlLoader, ArrayList<Thread> threads) {
        RoadController rc = fxmlLoader.getController();
        int[][] matrix = new int[10][10];
        Arrays.stream(matrix).forEach(a -> Arrays.fill(a,-1));
        gridWrapper = new GridWrapper(matrix);
        HashMap<Integer, ImageView> cars = new HashMap<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int x = i, y = j;
                SimpleIntegerProperty matrixProperty = gridWrapper.getMatrixProperty(x, y);
                matrixProperty.addListener((obs, oldValue, newValue) ->
                        Platform.runLater(() -> {
                            ImageView car = new ImageView(carImg);
                            if (newValue.intValue() != -1) {
                                car.setRotate(90*newValue.intValue());
                                rc.getMainGrid().add(car, x, y);
                                cars.put(10*x+y, car);
                            } else {
                                rc.getMainGrid().getChildren().remove(cars.get(10*x+y));
                                cars.remove(10*x+y);
                            }
                        }));
            }
        }

        Thread switchLightsThread = new SwitchLightsThread(6, rc);
        for (int i = 0; i < 7; i ++) {
            threads.add(new CarThread(i));
        }
        threads.add(switchLightsThread);
        for (Thread th : threads) {
            th.start();
        }
    }
}
