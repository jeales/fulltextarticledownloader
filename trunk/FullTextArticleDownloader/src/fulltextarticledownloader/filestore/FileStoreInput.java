/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fulltextarticledownloader.filestore;

import java.io.InputStream;

/**
 *
 * @author James Eales
 */
public class FileStoreInput {

    private String pmid;
    private FileElementType inputType;
    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public FileElementType getInputType() {
        return inputType;
    }

    public void setInputType(FileElementType inputType) {
        this.inputType = inputType;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public FileStoreInput(String pmid, FileElementType inputType, InputStream inputStream) {
        this.pmid = pmid;
        this.inputType = inputType;
        this.inputStream = inputStream;
    }

    public FileStoreInput() {
    }
}
