package fr.uha.ensisa.gm.projet.threads;

import fr.uha.ensisa.gm.projet.ProjectMain;
import fr.uha.ensisa.gm.projet.RoadController;

public class SwitchLightsThread extends Thread {
    private boolean doStop = false;
    private final int duration;
    private final RoadController rc;

    public SwitchLightsThread(int duration, RoadController rc) {
        this.rc = rc;
        this.duration = duration;
    }

    @Override
    public void run() {
        while (!doStop) {
            try {
                rc.switchLights(false);
                ProjectMain.hSem.release();
                ProjectMain.vSem.acquire();
                sleep(duration* 1000L);
                rc.switchLights(true);
                ProjectMain.vSem.release();
                ProjectMain.hSem.acquire();
                sleep(duration*1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void interrupt() {
        doStop = true;
    }
}
