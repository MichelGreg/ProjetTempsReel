package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.Direction;
import fr.uha.ensisa.gm.projet.GridWrapper;
import fr.uha.ensisa.gm.projet.ProjectMain;

import java.util.Random;

public class AmbulanceThread extends CarThread {

    private boolean passing;

    public AmbulanceThread(int id) {
        super(id);
    }

    @Override
    public void run() {
        boolean loop = true;
        this.passing = false;
        init();
        try {
            GridWrapper gw = ProjectMain.gridWrapper;
            sleep(new Random().nextLong(0, 4000L));
            while (gw.cantMove(x, y)) {
                sleep(100L);
            }
            gw.moveCar(id, x, y, directionMove, directionCar, true);
            crossRoads.acquire();
            System.out.println("L'ambulance à la priorité dans le carrefour");
            while (loop) {
                sleep(700L);
                x += dx;
                y += dy;
                while (gw.cantMove(x, y)) {
                    if (gw.canPass(x, y, directionMove)) {
                        changeLane(true);
                    }
                }
                if (passing && inCrossroads(x, y, directionMove)) {
                    changeLane(false);
                }
                gw.moveCar(id, x, y, directionMove, directionCar, true);
                if (passing) {
                    directionMove = directionCar;
                }
                checkCrossroadsPassed(directionMove);
                loop = (x >= 0 && x <= 9 && y >= 0 && y <= 9);
            }
            sleep(3000L);
            run();
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private boolean inCrossroads (int x, int y, Direction dir) {
        return ((x == 5 && y == 4) && dir == Direction.RIGHT
                || (x == 5 && y == 5) && dir == Direction.BOTTOM
                || (x == 4 && y == 4) && dir == Direction.TOP
                || (x == 4 && y == 5) && dir == Direction.LEFT);
    }

    private void changeLane(boolean change) {
        if (change) {
            this.passing = true;
            switch (directionMove) {
                case TOP -> {
                    x--;
                    directionMove = Direction.TOP_LEFT;
                }
                case RIGHT -> {
                    y--;
                    directionMove = Direction.TOP_RIGHT;
                }
                case BOTTOM -> {
                    x++;
                    directionMove = Direction.BOTTOM_RIGHT;
                }
                case LEFT -> {
                    y++;
                    directionMove = Direction.BOTTOM_LEFT;
                }
            }
        } else {
            switch (directionMove) {
                case TOP -> {
                    x++;
                    directionMove = Direction.TOP_RIGHT;
                }
                case RIGHT -> {
                    y++;
                    directionMove = Direction.BOTTOM_RIGHT;
                }
                case BOTTOM -> {
                    x--;
                    directionMove = Direction.BOTTOM_LEFT;
                }
                case LEFT -> {
                    y--;
                    directionMove = Direction.TOP_LEFT;
                }
            }
        }
    }

    private void checkCrossroadsPassed(Direction dir) {
        if ((dir == Direction.TOP && y == 3)
            || (dir == Direction.BOTTOM && y == 6)
            || (dir == Direction.LEFT && x == 3)
            || (dir == Direction.RIGHT && x == 6)) {
            crossRoads.release();
        }
    }
}
