/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.filestore;

import java.io.File;

/**
 *
 * @author James Eales
 */
public class FileStoreFileElement {

    private String pmid;
    private FileElementType elementType;
    private File file;

    public FileStoreFileElement(String pmid, FileElementType elementType, File file) {
        this.pmid = pmid;
        this.elementType = elementType;
        this.file = file;
    }

    public FileStoreFileElement() {
        pmid = null;
        elementType = null;
        file = null;
    }

    public FileElementType getElementType() {
        return elementType;
    }

    public void setElementType(FileElementType elementType) {
        this.elementType = elementType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }
}
