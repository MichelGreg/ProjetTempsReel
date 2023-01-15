package fr.uha.ensisa.gm.projet;

import javafx.beans.property.SimpleIntegerProperty;


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

    synchronized public void moveCar(int carId, int x, int y, Direction directionMove, Direction directionCar , boolean amb) throws InterruptedException {
        int ambInt = amb ? 4 : 0;
        String name = amb ? "ambulance" : "car";
        if ((x == 0 && y == 5) || (x == 9 && y == 4) || (x == 4 && y == 0) || (x == 5 && y == 9)) {
            System.out.printf("Create %s %d%n", name, carId);
            setMatrixValue(x, y, directionMove.getValue() + ambInt);
        } else if ((x == 10 && y == 5) || (x == -1 && y == 4) || (x == 4 && y == 10) || (x == 5 && y == -1)) {
            System.out.printf("Delete %s %d%n", name, carId);
            switch (directionMove) {
                case TOP -> setMatrixValue(5, 0, -1);
                case RIGHT -> setMatrixValue(9, 5, -1);
                case BOTTOM -> setMatrixValue(4, 9, -1);
                case LEFT -> setMatrixValue(0, 4, -1);
            }
        } else {
            //System.out.printf("Move %s %d%n", name, carId);
            switch (directionMove) {
                case TOP -> setMatrixValue(x, y + 1, -1);
                case RIGHT -> setMatrixValue(x - 1, y, -1);
                case BOTTOM -> setMatrixValue(x, y - 1, -1);
                case LEFT -> setMatrixValue(x + 1, y, -1);
                case TOP_LEFT -> setMatrixValue(x + 1, y + 1, -1);
                case TOP_RIGHT -> setMatrixValue(x - 1, y + 1, -1);
                case BOTTOM_LEFT -> setMatrixValue(x + 1, y - 1, -1);
                case BOTTOM_RIGHT -> setMatrixValue(x - 1, y - 1, -1);
            }
            setMatrixValue(x, y, directionCar.getValue() + ambInt);
        }
    }

    synchronized public boolean cantMove(int x, int y) {
        boolean gridEnd = (x == 10 && y == 5) || (x == -1 && y == 4) || (x == 4 && y == 10) || (x == 5 && y == -1);
        return !gridEnd && getMatrixProperty(x, y).get() != -1;
    }

    synchronized public boolean canPass(int x, int y, Direction direction) {
        boolean busy = false;
        switch (direction) {
            case TOP -> {
                for (int yt = y; yt >= 4; yt--) {
                    if (getMatrixProperty(4, yt).get() != -1) {
                        busy = true;
                    }
                }
            }
            case RIGHT -> {
                for (int xt = x; xt <= 5; xt++) {
                    if (getMatrixProperty(xt, 4).get() != -1) {
                        busy = true;
                    }
                }
            }
            case BOTTOM -> {
                for (int yt = y; yt <= 5; yt++) {
                    if (getMatrixProperty(5, yt).get() != -1) {
                        busy = true;
                    }
                }
            }
            case LEFT -> {
                for (int xt = x; xt >= 4; xt--) {
                    if (getMatrixProperty(xt, 5).get() != -1) {
                        busy = true;
                    }
                }
            }
        }
        return !busy;
    }


}

