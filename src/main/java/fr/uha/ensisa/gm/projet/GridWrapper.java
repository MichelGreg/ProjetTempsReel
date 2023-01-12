package fr.uha.ensisa.gm.projet;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.ImageView;

import java.util.Arrays;

import static java.lang.Thread.sleep;

public class GridWrapper {
    private final SimpleIntegerProperty[][] matrixProperties;
    private final int[][] matrix;

    public GridWrapper(int[][] matrix) {
        this.matrix = matrix;
        this.matrixProperties = new SimpleIntegerProperty[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrixProperties[i][j] = new SimpleIntegerProperty(matrix[i][j]);
            }
        }
    }

    public SimpleIntegerProperty getMatrixProperty(int i, int j) {
        return matrixProperties[i][j];
    }

    public void setMatrixValue(int i, int j, int value) {
        matrix[i][j] = value;
        matrixProperties[i][j].set(value);
    }

    synchronized public boolean moveCar(int carId, int x, int y, Direction direction) throws InterruptedException {
        if ((x == 0 && y == 5) || (x == 9 && y == 4) || (x == 4 && y == 0) || (x == 5 && y == 9)) {
            System.out.printf("Create car %d%n", carId);
            setMatrixValue(x, y, direction.getValue());
        } else if ((x == 10 && y == 5) || (x == -1 && y == 4) || (x == 4 && y == 10) || (x == 5 && y == -1)) {
            System.out.printf("Delete car %d%n", carId);
            switch (direction) {
                case TOP -> setMatrixValue(5, 0, -1);
                case RIGHT -> setMatrixValue(9, 5, -1);
                case BOTTOM -> setMatrixValue(4, 9, -1);
                case LEFT -> setMatrixValue(0, 5, -1);
            }
        } else if (getMatrixProperty(x, y).get() == -1){
            System.out.printf("Move car %d%n", carId);
            switch (direction) {
                case TOP -> setMatrixValue(x, y + 1, -1);
                case RIGHT -> setMatrixValue(x - 1, y, -1);
                case BOTTOM -> setMatrixValue(x, y - 1, -1);
                case LEFT -> setMatrixValue(x + 1, y, -1);
            }
            setMatrixValue(x, y, direction.getValue());
        } else {
            return false;
        }
        return true;
    }
}

