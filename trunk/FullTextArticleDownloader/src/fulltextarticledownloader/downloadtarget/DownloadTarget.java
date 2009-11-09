/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fulltextarticledownloader.downloadtarget;

/**
 *
 * @author James Eales
 */
public interface DownloadTarget {

    public boolean isDownloadTarget(byte[] blob, String url);

}
