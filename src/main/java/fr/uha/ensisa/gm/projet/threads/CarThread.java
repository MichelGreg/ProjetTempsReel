package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.Direction;
import fr.uha.ensisa.gm.projet.GridWrapper;
import fr.uha.ensisa.gm.projet.ProjectMain;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class CarThread extends Thread {
    protected static Semaphore crossRoads = new Semaphore(1);
    protected static Semaphore ambulance = new Semaphore(1);
    protected final int id;
    protected Direction directionMove;
    protected Direction directionCar;
    protected final Direction turn;
    protected int x;
    protected int dx;
    protected int y;
    protected int dy;

    public CarThread(int id) {
        this.id = id;
        Random rd = new Random();
        int direction = rd.nextInt(0, 4);
        this.directionMove = Direction.get(direction);
        this.directionCar = directionMove;
        int turn;
        do {
            turn = rd.nextInt(4);
        } while (turn == (direction + 1) % 4);
        this.turn = Direction.get(turn);
    }

    @Override
    public void run() {
        boolean loop = true;
        init();
        try {
            GridWrapper gw = ProjectMain.gridWrapper;
            sleep(new Random().nextLong(0, 4000));
            while (gw.cantMove(x, y)) {
                sleep(100L);
            }
            gw.moveCar(id, x, y, directionMove, directionCar, false);
            while (loop) {
                sleep(1000L);
                x += dx;
                y += dy;
                checkCrossroads();
                while (gw.cantMove(x, y)) {
                    sleep(100L);
                }
                gw.moveCar(id, x, y, directionMove, directionCar, false);
                loop = (x >= 0 && x <= 9 && y >= 0 && y <= 9);
            }
            sleep(3000L);
            run();
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    protected void checkCrossroads() throws InterruptedException {
        boolean horizontal = directionMove == Direction.LEFT || directionMove == Direction.RIGHT;
        GridWrapper gw = ProjectMain.gridWrapper;
        if (inCrossroads(x, y)) {
            crossRoads.acquire();
            if (!ambulance.tryAcquire()) {
                System.out.printf("La voiture %d attends le passage de l'ambulance%n", id);
                crossRoads.release();
                ambulance.acquire();
                crossRoads.acquire();
            }
            ambulance.release();
            if (horizontal) {
                if (!ProjectMain.hSem.tryAcquire()){
                    System.out.printf("La voiture %d attends le feu vert%n", id);
                    crossRoads.release();
                    ProjectMain.hSem.acquire();
                    crossRoads.acquire();
                }
                ProjectMain.hSem.release();
            } else {
                if (!ProjectMain.vSem.tryAcquire()){
                    System.out.printf("La voiture %d attends le feu vert%n", id);
                    crossRoads.release();
                    ProjectMain.vSem.acquire();
                    crossRoads.acquire();
                }
                ProjectMain.vSem.release();
            }

            System.out.printf("La voiture %d entre dans l'intersection%n", id);
            gw.moveCar(id, x, y, directionMove, directionCar, false);
            sleep(1000L);
//            x += dx;
//            y += dy;
            turn(gw);
            sleep(1000L);
            x += dx;
            y += dy;
            crossRoads.release();
            System.out.printf("La voiture %d sors de l'intersection%n", id);
        }
    }

    private void turn(GridWrapper gw) throws InterruptedException {
        switch (directionMove) {
            case TOP -> {
                switch (turn) {
                    case TOP -> {
                        x += dx;
                        y += dy;
                        gw.moveCar(id, x, y, directionMove, directionCar, false);
                        System.out.printf("La voiture %d va tout droit%n", id);
                    }
                    case RIGHT -> {
                        dy = 0;
                        dx = 1;
                        directionCar = Direction.RIGHT;
                        gw.moveCar(id, x, y, Direction.NONE, directionCar, false);
                        System.out.printf("La voiture %d tourne à droite%n", id);
                        directionMove = Direction.RIGHT;
                    }
                    case LEFT -> {
                        dx = -1;
                        dy = 0;
                        x--;
                        y--;
                        directionCar = Direction.LEFT;
                        gw.moveCar(id, x, y, Direction.TOP_LEFT, directionCar, false);
                        System.out.printf("La voiture %d tourne à gauche%n", id);
                        directionMove = Direction.LEFT;
                    }
                }
            }
            case RIGHT -> {
                switch (turn) {
                    case TOP -> {
                        x++;
                        y--;
                        dx = 0;
                        dy = -1;
                        directionCar = Direction.TOP;
                        gw.moveCar(id, x, y, Direction.TOP_RIGHT, directionCar, false);
                        System.out.printf("La voiture %d tourne vers le haut%n", id);
                        directionMove = Direction.TOP;
                    }
                    case RIGHT -> {
                        x += dx;
                        y += dy;
                        gw.moveCar(id, x, y, directionMove, directionCar, false);
                        System.out.printf("La voiture %d va tout droit%n", id);
                    }
                    case BOTTOM -> {
                        dy = 1;
                        dx = 0;
                        directionCar = Direction.BOTTOM;
                        gw.moveCar(id, x, y, Direction.NONE, directionCar, false);
                        System.out.printf("La voiture %d tourne vers le bas%n", id);
                        directionMove = Direction.BOTTOM;
                    }
                }
            }
            case BOTTOM -> {
                switch (turn) {
                    case RIGHT -> {
                        dx = 1;
                        dy = 0;
                        x++;
                        y++;
                        directionCar = Direction.RIGHT;
                        gw.moveCar(id, x, y, Direction.BOTTOM_RIGHT, directionCar, false);
                        System.out.printf("La voiture %d tourne à droite%n", id);
                        directionMove = Direction.RIGHT;
                    }
                    case BOTTOM -> {
                        x += dx;
                        y += dy;
                        gw.moveCar(id, x, y, directionMove, directionCar, false);
                        System.out.printf("La voiture %d va tout droit%n", id);
                    }
                    case LEFT -> {
                        dy = 0;
                        dx = -1;
                        directionCar = Direction.LEFT;
                        gw.moveCar(id, x, y, Direction.NONE, directionCar, false);
                        System.out.printf("La voiture %d tourne à gauche%n", id);
                        directionMove = Direction.LEFT;
                    }
                }
            }
            case LEFT -> {
                switch (turn) {
                    case TOP -> {
                        dy = -1;
                        dx = 0;
                        directionCar = Direction.TOP;
                        gw.moveCar(id, x, y, Direction.NONE, directionCar, false);
                        System.out.printf("La voiture %d tourne vers le haut%n", id);
                        directionMove = Direction.TOP;
                    }
                    case BOTTOM -> {
                        dx = -1;
                        dy = 0;
                        x--;
                        y++;
                        directionCar = Direction.BOTTOM;
                        gw.moveCar(id, x, y, Direction.BOTTOM_LEFT, directionCar, false);
                        System.out.printf("La voiture %d tourne vers le bas%n", id);
                        directionMove = Direction.BOTTOM;
                    }
                    case LEFT -> {
                        x += dx;
                        y += dy;
                        gw.moveCar(id, x, y, directionMove, directionCar, false);
                        System.out.printf("La voiture %d va tout droit%n", id);
                    }
                }
            }
        }
    }
    protected boolean inCrossroads (int x, int y) {
        return ((x == 4 && y == 4) || (x == 5 && y == 4) || (x == 4 && y == 5) || (x == 5 && y == 5));
    }

    protected void init() {
        switch (directionMove) {
            case TOP -> {
                x = 5;
                y = 9;
                dx = 0;
                dy = -1;
            }
            case RIGHT -> {
                x = 0;
                y = 5;
                dx = 1;
                dy = 0;
            }
            case BOTTOM -> {
                x = 4;
                y = 0;
                dx = 0;
                dy = 1;
            }
            case LEFT -> {
                x = 9;
                y = 4;
                dx = -1;
                dy = 0;
            }
        }
    }
}
