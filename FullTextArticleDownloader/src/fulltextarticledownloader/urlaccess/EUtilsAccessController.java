/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.urlaccess;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jameseales
 */
public class EUtilsAccessController {

    private static EUtilsAccessController singleton;
    private Long data;
    //rate is number of requests allowed per host per second
    private static final double RATE = 2.5;
    private static long delay;

    private EUtilsAccessController() {
        data = 0l;
        delay = (long) (1000.0 / RATE);
    }

    public static EUtilsAccessController getURLAccessControllerRef() {
        if (singleton == null) {
            singleton = new EUtilsAccessController();
        }
        return singleton;
    }

    public void requestAccess() {

        Long lastTime = data;

        if (lastTime == null) {
            //not been accessed before
            //store time now
            data = System.currentTimeMillis();
            //allow access now
            return;
        } else {
            long now = System.currentTimeMillis();
            long diff = now - lastTime;
            if (diff < delay) {
                long waitTime = delay - diff;
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EUtilsAccessController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            //store time now
            data = System.currentTimeMillis();
            //allow access now
            return;
        }
    }
}
