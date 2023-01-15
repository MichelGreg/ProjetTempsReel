package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.Direction;
import fr.uha.ensisa.gm.projet.GridWrapper;
import fr.uha.ensisa.gm.projet.PrioritySemaphore;
import fr.uha.ensisa.gm.projet.ProjectMain;

import java.util.Random;

public class CarThread extends Thread {
    protected static PrioritySemaphore crossRoads = new PrioritySemaphore(1);
    protected final int id;
    protected Direction directionMove;
    protected final Direction directionCar;
    protected int x;
    protected int dx;
    protected int y;
    protected int dy;

    public CarThread(int id) {
        this.directionMove = Direction.get(new Random().nextInt(0, 4));
        this.directionCar = directionMove;
        this.id = id;
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
            x += dx;
            y += dy;
            gw.moveCar(id, x, y, directionMove, directionCar, false);
            sleep(1000L);
            x += dx;
            y += dy;
            crossRoads.release();
            System.out.printf("La voiture %d sors de l'intersection%n", id);
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
