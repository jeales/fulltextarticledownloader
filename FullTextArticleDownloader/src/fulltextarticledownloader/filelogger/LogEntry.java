/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.filelogger;

/**
 *
 * @author James Eales
 */
public class LogEntry {

    public enum EntryType {

        SUCCESS, FAILURE, ERROR
    }

    public enum IDType {

        PMID, PMCID, DOI
    }
    private EntryType entryType;
    private IDType idType;
    private String uniqueID;

    public LogEntry() {
        
    }

    public LogEntry(EntryType entryType, IDType idType, String uniqueID) {
        setEntryType(entryType);
        setIdType(idType);
        setUniqueID(uniqueID);
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public IDType getIdType() {
        return idType;
    }

    public void setIdType(IDType idType) {
        this.idType = idType;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Entry Type: " + getEntryType() + "\n");
        sb.append("IDType: "+ getIdType() +"\n");
        sb.append("UniqueID: " + getUniqueID());
        
        return sb.toString();
    }
    
    
}
