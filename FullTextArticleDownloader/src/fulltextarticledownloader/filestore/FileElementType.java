/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.filestore;

/**
 *
 * @author James Eales
 */
public enum FileElementType {

    pdf(".pdf"), text(".txt"), xml(".xml");
    private String fileSuffix;

    private FileElementType(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public static FileElementType getTypeFromString(String type) {
        if (type.equals("pdf")) {
            return pdf;
        } else if (type.equals("text")) {
            return text;
        } else if (type.equals("xml")) {
            return xml;
        } else {
            return null;
        }
    }
}
