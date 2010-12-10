/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.urlaccess;

import fulltextarticledownloader.beans.WaitingDetails;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author jameseales
 */
public class URLAccessController {

    private static URLAccessController singleton;
    private ConcurrentHashMap<String, Long> data;
    //rate is number of requests allowed per host per second
    private static final double RATE = 0.5;
    private static long delay;

    private URLAccessController() {
        data = new ConcurrentHashMap<String, Long>();
        delay = (long) (1000.0 / RATE);
    }

    public static URLAccessController getURLAccessControllerRef() {
        if (singleton == null) {
            singleton = new URLAccessController();
        }
        return singleton;
    }

    public GetMethod requestURL(String url, WaitingDetails wait) {
        //System.out.println("Requesting URL: " + url);
        if (url.startsWith("?")) {
            url = url.substring(1, url.length());
        }

        try {
            URL u = new URL(url);
            URI uri = new URI(url);
            String host = u.getHost();
            if (wait != null) {
                wait.setHost(host);
            }
            Long lastTime = data.get(host);
            if (wait != null) {
                if (lastTime == null) {
                    wait.setLastRequestToHost(0l);
                } else {
                    wait.setLastRequestToHost(lastTime);
                }
            }
            if (lastTime == null) {
                if (wait != null) {
                    wait.setWaitTime(0);
                }
                //not been accessed before
                //store time now
                data.put(host, System.currentTimeMillis());
                //allow access now
                return new GetMethod(url);
            } else {
                long now = System.currentTimeMillis();
                long diff = now - lastTime;
                if (diff < delay) {
                    long waitTime = delay - diff;
                    if (wait != null) {
                        wait.setWaitTime(waitTime);
                    }
                    //System.out.println("Waiting: " + waitTime);
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(URLAccessController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //store time now
                data.put(host, System.currentTimeMillis());
                //allow access now
                return new GetMethod(url);
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    public GetMethod requestURL(String url) {
        return requestURL(url, null);
    }
}
