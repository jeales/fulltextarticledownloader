/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui.workers;

import fulltextarticledownloader.beans.PubMedMetaDataForTable;
import fulltextarticledownloader.gui.panels.ResultsPanel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import jbot.ncbi.beans.PubMedMetaData;
import jbot.ncbi.pubmed.PubMedMetaDataRetriever;
import jbot.util.SegmentedCollection;

/**
 *
 * @author jameseales
 */
public class PubMedMetaDataImporter extends SwingWorker<Void, PubMedMetaDataForTable> {

    private DefaultTableModel model;
    private ResultsPanel resultsPanel;
    private LinkedList<String> allids;

    public PubMedMetaDataImporter(LinkedList<String> allids, DefaultTableModel model, ResultsPanel resultsPanel) {
        this.allids = allids;
        this.model = model;
        this.resultsPanel = resultsPanel;
        resultsPanel.showMetadataProgressBar();
    }

    @Override
    protected Void doInBackground() {
        SegmentedCollection<String> s = new SegmentedCollection<String>(allids, 25);

        LinkedList<String> list = new LinkedList<String>();

        while ((list = s.getNextSegment()) != null) {
            PubMedMetaDataRetriever ret = new PubMedMetaDataRetriever(list);
            HashMap<String, PubMedMetaData> data = ret.getAllData();
            for (String string : data.keySet()) {
                PubMedMetaData md = data.get(string);
                if (md != null) {
                    PubMedMetaDataForTable ft = new PubMedMetaDataForTable(md);
                    publish(ft);
                }

            }
        }
        return null;
    }

    @Override
    protected void process(List<PubMedMetaDataForTable> chunks) {
        resultsPanel.showMetadataProgressBar();
        for (PubMedMetaDataForTable pubMedMetaDataForTable : chunks) {
            model.addRow(pubMedMetaDataForTable.toTableRowVector());
        }
    }

    @Override
    protected void done() {
        resultsPanel.hideMetadataProgressBar();
    }
}
