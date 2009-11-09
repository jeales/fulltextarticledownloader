/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui.workers;

import jbot.ncbi.pubmed.QueryTerm;
import fulltextarticledownloader.gui.dialogs.SearchingDialog;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.SwingWorker;
import jbot.ncbi.pubmed.PubMedSearcher;

/**
 *
 * @author jameseales
 */
public class PubMedSearchingWorker extends SwingWorker<LinkedList<String>, Void> {

    private SearchingDialog dialog;
    private Vector<QueryTerm> terms;
    private LinkedList<String> ids;

    public PubMedSearchingWorker(SearchingDialog dialog, Vector<QueryTerm> terms) {
        this.dialog = dialog;
        this.terms = terms;
        ids = new LinkedList<String>();
    }

    @Override
    protected LinkedList<String> doInBackground() {
        PubMedSearcher searcher = new PubMedSearcher();
        for (QueryTerm queryTerm : terms) {
            searcher.addQueryTerm(queryTerm);
        }
        ids = searcher.doSearch();
        return ids;
    }



    @Override
    protected void done() {
        dialog.setVisible(false);
    }
}
