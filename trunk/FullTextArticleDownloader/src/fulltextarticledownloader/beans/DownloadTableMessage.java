/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

/**
 *
 * @author jameseales
 */
public class DownloadTableMessage {

    private int rowID;
    private int colID;
    private String value;

    public DownloadTableMessage(int rowID, int colID, String value) {
        this.rowID = rowID;
        this.colID = colID;
        this.value = value;
    }

    public DownloadTableMessage() {
    }

    public int getColID() {
        return colID;
    }

    public void setColID(int colID) {
        this.colID = colID;
    }

    

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
