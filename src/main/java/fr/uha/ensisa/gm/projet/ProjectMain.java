package fr.uha.ensisa.gm.projet;

import fr.uha.ensisa.gm.projet.threads.CarThread;
import fr.uha.ensisa.gm.projet.threads.SwitchLightsThread;
import javafx.fxml.FXMLLoader;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ProjectMain {
    public static Semaphore vSem = new Semaphore(1);
    public static Semaphore hSem = new Semaphore(1);
    public static void run(FXMLLoader fxmlLoader, ArrayList<Thread> threads) {
        RoadController rc = fxmlLoader.getController();
        Thread switchLightsThread = new SwitchLightsThread(10, rc);
        for (int i = 0; i < 3; i ++) {
            threads.add(new CarThread(rc, i));
        }
        threads.add(switchLightsThread);
        for (Thread th : threads) {
            th.start();
        }
    }
}
