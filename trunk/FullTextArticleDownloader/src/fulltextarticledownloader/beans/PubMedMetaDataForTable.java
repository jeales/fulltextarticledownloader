/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.beans;

import java.util.Vector;
import jbot.ncbi.beans.PubMedMetaData;

/**
 *
 * @author jameseales
 */
public class PubMedMetaDataForTable extends PubMedMetaData {

    public PubMedMetaDataForTable(PubMedMetaData data) {
        this.setArticleTitle(data.getArticleTitle());
        this.setAuthors(data.getAuthors());
        this.setJournal(data.getJournal());
        this.setPmid(data.getPmid());
        this.setYear(data.getYear());
    }

    public Vector toTableRowVector() {
        Vector v = new Vector();

        try {
            Integer iPmid = Integer.parseInt(getPmid());
            v.add(iPmid);
        } catch (NumberFormatException e) {
            v.add("");
        }
        v.add(getArticleTitle());
        v.add(getAuthors());
        v.add(getJournal());
        try {
            Integer iYear = Integer.parseInt(getYear());
            v.add(iYear);
        } catch (NumberFormatException e) {
            v.add("");
        }


        return v;
    }
}
