/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

/**
 *
 * @author jameseales
 */
public class WaitingDetails {

    private String host = "";
    private long lastRequestToHost = 0l;
    private long waitTime = 0l;

    public WaitingDetails() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getLastRequestToHost() {
        return lastRequestToHost;
    }

    public void setLastRequestToHost(long lastRequestToHost) {
        this.lastRequestToHost = lastRequestToHost;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }


    

}
