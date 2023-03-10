package fr.uha.ensisa.gm.projet;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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

    public void switchLights(boolean config) {
        System.out.println("Switch lights");
        verticalLight1.setImage(config ? greenLight : redLight);
        verticalLight2.setImage(config ? greenLight : redLight);
        horizontalLight1.setImage(config ? redLight : greenLight);
        horizontalLight2.setImage(config ? redLight : greenLight);
    }

    public GridPane getMainGrid() {
        return mainGrid;
    }
}