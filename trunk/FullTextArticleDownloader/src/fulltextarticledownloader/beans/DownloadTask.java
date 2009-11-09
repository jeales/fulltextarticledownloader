/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.beans;

import fulltextarticledownloader.filestore.FileStore;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jameseales
 */
public class DownloadTask {

    private List<String> ids;
    private FileStore storeHere;
    private boolean savePDF, saveXML, savePlainText;

    public DownloadTask(LinkedList<String> ids, FileStore storeHere, boolean savePDF, boolean saveXML, boolean savePlainText) {
        this.ids = ids;
        this.storeHere = storeHere;
        this.savePDF = savePDF;
        this.saveXML = saveXML;
        this.savePlainText = savePlainText;
    }

    public DownloadTask() {
    }



    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean isSavePDF() {
        return savePDF;
    }

    public void setSavePDF(boolean savePDF) {
        this.savePDF = savePDF;
    }

    public boolean isSavePlainText() {
        return savePlainText;
    }

    public void setSavePlainText(boolean savePlainText) {
        this.savePlainText = savePlainText;
    }

    public boolean isSaveXML() {
        return saveXML;
    }

    public void setSaveXML(boolean saveXML) {
        this.saveXML = saveXML;
    }

    public FileStore getStoreHere() {
        return storeHere;
    }

    public void setStoreHere(FileStore storeHere) {
        this.storeHere = storeHere;
    }
}
