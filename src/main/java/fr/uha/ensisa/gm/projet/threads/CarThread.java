package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.Direction;
import fr.uha.ensisa.gm.projet.GridWrapper;
import fr.uha.ensisa.gm.projet.ProjectMain;
import fr.uha.ensisa.gm.projet.RoadController;

import java.util.Random;

public class CarThread extends Thread {
    private final int id;
    private final RoadController rc;
    private final Direction direction;
    private int x;
    private int dx;
    private int y;
    private int dy;

    public CarThread(RoadController rc, int id) {
        this.rc = rc;
        this.direction = Direction.get(new Random().nextInt(0, 3));
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
            //rc.moveCar(id, x, y, direction);
            gw.moveCar(id, x, y, direction);
            while (loop) {
                sleep(1000L);
                x += dx;
                y += dy;
                checkCrossroads();
                //rc.moveCar(id, x, y, direction);
                boolean move = gw.moveCar(id, x, y, direction);
                while (!move){
                    sleep(100L);
                    move = gw.moveCar(id, x, y, direction);
                }
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
            if (horizontal) {
                if (!ProjectMain.hSem.tryAcquire()){
                    System.out.printf("Car %d is waiting for the green light%n", id);
                    ProjectMain.hSem.acquire();
                }
            } else {
                if (!ProjectMain.vSem.tryAcquire()){
                    System.out.printf("Car %d is waiting for the green light%n", id);
                    ProjectMain.vSem.acquire();
                }
            }
            System.out.printf("Car %d is entering the crossroads%n", id);
            //rc.moveCar(id, x, y, direction);
            gw.moveCar(id, x, y, direction);
            sleep(1000L);
            x += dx;
            y += dy;
            //rc.moveCar(id, x, y, direction);
            gw.moveCar(id, x, y, direction);
            sleep(1000L);
            x += dx;
            y += dy;
            //rc.moveCar(id, x, y, direction);
            //gw.moveCar(id, x, y, direction);
            if (horizontal) {
                ProjectMain.hSem.release();
            } else {
                ProjectMain.vSem.release();
            }
            System.out.printf("Car %d exited the crossroads%n", id);
        }

    }
    private boolean inCrossroads (int x, int y) {
        return ((x == 4 && y == 4) || (x == 5 && y == 4) || (x == 4 && y == 5) || (x == 5 && y == 5));
    }
}
