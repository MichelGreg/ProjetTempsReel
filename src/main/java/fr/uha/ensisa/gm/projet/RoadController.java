package fr.uha.ensisa.gm.projet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.TreeMap;

public class RoadController {

    @FXML
    private ImageView verticalLight1;

    @FXML
    private ImageView verticalLight2;

    @FXML
    private ImageView horizontalLight1;

    @FXML
    private ImageView horizontalLight2;

    @FXML
    private GridPane mainGrid;

    private final Image greenLight = new Image(getClass().getResourceAsStream("ui/green.jpg"));
    private final Image redLight = new Image(getClass().getResourceAsStream("ui/red.jpg"));
    private final Image carImg = new Image(getClass().getResourceAsStream("ui/car.png"));
    private final TreeMap<Integer, ImageView> cars = new TreeMap<>();

    public void switchLights(boolean config) {
        System.out.println("Switch lights");
        verticalLight1.setImage(config ? greenLight : redLight);
        verticalLight2.setImage(config ? greenLight : redLight);
        horizontalLight1.setImage(config ? redLight : greenLight);
        horizontalLight2.setImage(config ? redLight : greenLight);
    }

    public void moveCar(int carId, int x, int y, Direction direction) {
        System.out.printf("Move car %d%n", carId);
        ImageView car;
        if (cars.containsKey(carId)){
            car = cars.get(carId);
            Platform.runLater(()-> {
                boolean removed = mainGrid.getChildren().remove(car);
                if (x >= 0 && x <= 9 && y >= 0 && y <= 9) {
                    mainGrid.add(car, x, y);
                }
            });
        } else {
            car = new ImageView(carImg);
            car.setRotate(90*direction.getValue());
            cars.put(carId, car);
            Platform.runLater(()-> mainGrid.add(car, x, y));
        }
    }

    public void deleteCar(int carId) {
        Platform.runLater(()-> {
            boolean removed = mainGrid.getChildren().remove(cars.get(carId));
            System.out.printf("Delete car %d%n", carId);
        });
        cars.remove(carId);
    }
}