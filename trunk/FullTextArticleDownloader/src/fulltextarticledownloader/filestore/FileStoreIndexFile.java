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
public class FileStoreIndexFile extends File {

    private File rootDir;

    public FileStoreIndexFile(File directory, String xmlFileName) {
        super(directory, xmlFileName);
        rootDir = directory;
    }

    public File getRootDir() {
        return rootDir;
    }

    public boolean createFileStoreDirectory() {
        return rootDir.mkdirs();
    }

    
}
