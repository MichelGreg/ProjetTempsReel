package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.Direction;
import fr.uha.ensisa.gm.projet.GridWrapper;
import fr.uha.ensisa.gm.projet.ProjectMain;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class CarThread extends Thread {
    private static Semaphore crossRoads = new Semaphore(1);
    private final int id;
    private final Direction direction;
    private int x;
    private int dx;
    private int y;
    private int dy;

    public CarThread(int id) {
        this.direction = Direction.get(new Random().nextInt(0, 4));
        this.id = id;
    }

    @Override
    public void run() {
        boolean loop = true;
        switch (direction) {
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
        try {
            GridWrapper gw = ProjectMain.gridWrapper;
            sleep(new Random().nextLong(0, 4000));
            while (gw.cantMove(x, y)) {
                sleep(100L);
            }
            gw.moveCar(id, x, y, direction);
            while (loop) {
                sleep(1000L);
                x += dx;
                y += dy;
                checkCrossroads();
                while (gw.cantMove(x, y)) {
                    sleep(100L);
                }
                gw.moveCar(id, x, y, direction);
                loop = (x >= 0 && x <= 9 && y >= 0 && y <= 9);
            }
            sleep(3000L);
            run();
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private void checkCrossroads() throws InterruptedException {
        boolean horizontal = direction == Direction.LEFT || direction == Direction.RIGHT;
        GridWrapper gw = ProjectMain.gridWrapper;
        if (inCrossroads(x, y)) {
            crossRoads.acquire();
            if (horizontal) {
                if (!ProjectMain.hSem.tryAcquire()){
                    System.out.printf("Car %d is waiting for the green light%n", id);
                    crossRoads.release();
                    ProjectMain.hSem.acquire();
                    crossRoads.acquire();
                }
                ProjectMain.hSem.release();
            } else {
                if (!ProjectMain.vSem.tryAcquire()){
                    System.out.printf("Car %d is waiting for the green light%n", id);
                    crossRoads.release();
                    ProjectMain.vSem.acquire();
                    crossRoads.acquire();
                }
                ProjectMain.vSem.release();
            }

            System.out.printf("Car %d is entering the crossroads%n", id);
            gw.moveCar(id, x, y, direction);
            sleep(1000L);
            x += dx;
            y += dy;
            gw.moveCar(id, x, y, direction);
            sleep(1000L);
            x += dx;
            y += dy;
            crossRoads.release();
            System.out.printf("Car %d exited the crossroads%n", id);
        }

    }
    private boolean inCrossroads (int x, int y) {
        return ((x == 4 && y == 4) || (x == 5 && y == 4) || (x == 4 && y == 5) || (x == 5 && y == 5));
    }
}
