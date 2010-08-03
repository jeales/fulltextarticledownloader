/*
 * ResultsPanel.java
 *
 * Created on 04 November 2008, 12:09
 */
package fulltextarticledownloader.gui.panels;

import fulltextarticledownloader.beans.DownloadTask;
import fulltextarticledownloader.gui.FinalGUI;
import fulltextarticledownloader.gui.SearchingPropertyChangeHandler;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import jbot.ncbi.pubmed.QueryTerm;
import fulltextarticledownloader.gui.workers.PubMedMetaDataImporter;
import fulltextarticledownloader.gui.workers.PubMedSearchingWorker;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  James Eales
 */
public class ResultsPanel extends javax.swing.JPanel {

    private PubMedMetaDataImporter metaDataImporter;
    private LinkedList<String> lastSetOfIds;
    private DefaultTableModel resultsTableModel;
    private SearchingPropertyChangeHandler handler;
    private PubMedSearchingWorker searcher;
    private static final int GET_METADATA_FOR = 50;
    private int metadataCounter = 0;

    /** Creates new form ResultsPanel */
    public ResultsPanel() {
        resultsTableModel = new DefaultTableModel(new String[]{"PubMed ID", "Article Title", "Authors", "Journal", "Year"}, 0);
        handler = new SearchingPropertyChangeHandler(this);
        initComponents();
        setupTableStuff();
    }

    private void setupTableStuff() {
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(384);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        //jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
        resultsTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                //System.out.println("Type: " + e.getType() + " Column: " + e.getColumn() + " FirstRow: " + e.getFirstRow() + " LastRow: " + e.getLastRow());
                if (e.getType() == TableModelEvent.INSERT) {
                    searchStatusField.setText("Search results found: " + getLastSetOfIds().size() + " Metadata: " + resultsTableModel.getRowCount() + "/" + getLastSetOfIds().size());
                }
            }
        });
    }

    public LinkedList<String> getLastSetOfIds() {
        return lastSetOfIds;
    }

    public void setLastSetOfIds(LinkedList<String> lastSetOfIds) {
        this.lastSetOfIds = lastSetOfIds;
        searchStatusField.setText("Search results found: " + this.lastSetOfIds.size());
    }

    public void addStaticSetOfIDs(LinkedList<String> ids) {
        FinalGUI.getQueryPanel().setReadyToGo();
        metadataCounter = 0;
        clearResultsTable();
        setLastSetOfIds(ids);
        if (ids.size() > (metadataCounter + GET_METADATA_FOR)) {
            LinkedList<String> subList = new LinkedList<String>(ids.subList(metadataCounter, metadataCounter + GET_METADATA_FOR));
            metadataCounter = GET_METADATA_FOR;
            updateMetaDataFor(subList);
        } else {
            LinkedList<String> subList = new LinkedList<String>(ids.subList(metadataCounter, metadataCounter + ids.size()));
            metadataCounter = ids.size();
            updateMetaDataFor(subList);
        }
    }

    private void updateMetaDataFor(LinkedList<String> ids) {
        //
        PubMedMetaDataImporter importer = new PubMedMetaDataImporter(ids, resultsTableModel, this);
        //importer.addPropertyChangeListener(new SearchingPropertyChangeHandler(this));
        importer.execute();
    }

    public void showMetadataProgressBar() {
        jProgressBar1.setVisible(true);
    }

    public void hideMetadataProgressBar() {
        jProgressBar1.setVisible(false);
    }

    private LinkedList<String> getPMIDsFromTable(int[] rows) {
        LinkedList<String> r = new LinkedList<String>();

        for (int i : rows) {
            Integer id = (Integer) resultsTableModel.getValueAt(i, 0);
            r.add(id.toString());
        }

        return r;
    }

    private void downloadSelected() {
        int[] rows = resultsTable.getSelectedRows();
        if (rows.length > 0) {
            DownloadTask task = new DownloadTask();
            LinkedList<String> pmids = getPMIDsFromTable(rows);
            task.setIds(pmids);
            task.setSavePDF(FinalGUI.getOptionsDialog().isSavePDF());
            task.setSavePlainText(FinalGUI.getOptionsDialog().isSavePlainText());
            task.setSaveXML(FinalGUI.getOptionsDialog().isSaveXML());
            task.setCheckPDF(FinalGUI.getOptionsDialog().isCheckPDF());
            task.setStoreHere(FinalGUI.getOptionsDialog().getFileStore());
            FinalGUI.getDownloadPanel().addDownloadTask(task);
        } else {
            JOptionPane.showMessageDialog(this, "You need to select one or more results");
        }
    }

    private void downloadFirstN() {
        int n = FinalGUI.getChooseNDialog().getN();
        if (n > 0) {
            DownloadTask task = new DownloadTask();
            task.setSavePDF(FinalGUI.getOptionsDialog().isSavePDF());
            task.setSavePlainText(FinalGUI.getOptionsDialog().isSavePlainText());
            task.setSaveXML(FinalGUI.getOptionsDialog().isSaveXML());
            task.setCheckPDF(FinalGUI.getOptionsDialog().isCheckPDF());
            task.setStoreHere(FinalGUI.getOptionsDialog().getFileStore());

            if (n >= lastSetOfIds.size()) {
                task.setIds(lastSetOfIds);
            } else {
                List<String> subList = lastSetOfIds.subList(0, n);
                task.setIds(subList);
            }

            FinalGUI.getDownloadPanel().addDownloadTask(task);
        }
    }

    private void downloadAll() {
        DownloadTask task = new DownloadTask();
        task.setSavePDF(FinalGUI.getOptionsDialog().isSavePDF());
        task.setSavePlainText(FinalGUI.getOptionsDialog().isSavePlainText());
        task.setSaveXML(FinalGUI.getOptionsDialog().isSaveXML());
        task.setCheckPDF(FinalGUI.getOptionsDialog().isCheckPDF());
        task.setStoreHere(FinalGUI.getOptionsDialog().getFileStore());
        LinkedList<String> ids = new LinkedList<String>();
        ids.addAll(lastSetOfIds);
        task.setIds(ids);
        FinalGUI.getDownloadPanel().addDownloadTask(task);
    }

    private void getMoreResults() {
        LinkedList<String> ids = getLastSetOfIds();
        if (ids.size() > metadataCounter) {
            if (ids.size() > (metadataCounter + GET_METADATA_FOR)) {
                LinkedList<String> subList = new LinkedList<String>(ids.subList(metadataCounter, metadataCounter + GET_METADATA_FOR));
                metadataCounter += GET_METADATA_FOR;
                updateMetaDataFor(subList);
            } else {
                //System.out.println("metadataCount="+ metadataCounter);
                LinkedList<String> subList = new LinkedList<String>(ids.subList(metadataCounter, metadataCounter + (ids.size() - metadataCounter)));
                metadataCounter += ids.size();
                updateMetaDataFor(subList);
            }
        } else {
            JOptionPane.showMessageDialog(this, "All metadata has or is being downloaded");
        }
    }

    public void doSearch() {
        Vector<QueryTerm> terms = FinalGUI.getQueryPanel().getQueryTerms();
        searcher = new PubMedSearchingWorker(FinalGUI.getSearchingDialog(), terms);
        searcher.addPropertyChangeListener(handler);
        searcher.execute();
        if (!FinalGUI.getSearchingDialog().isVisible()) {
            FinalGUI.getSearchingDialog().setVisible(true);
        }
    }

    public void searchingDone() {
        //System.out.println("searchingDone()");
        LinkedList<String> ids = null;
        try {
            ids = searcher.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(ResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ResultsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        addStaticSetOfIDs(ids);
    }

    public void clearResultsTable() {
        resultsTableModel.setRowCount(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        searchStatusField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jButton4.setText("Get more results");
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        resultsTable.setModel(resultsTableModel);
        resultsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        resultsTable.setDoubleBuffered(true);
        jScrollPane1.setViewportView(resultsTable);

        jButton3.setText("Download selected");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText("Download first n articles");
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        searchStatusField.setEditable(false);

        jButton1.setText("Download all articles");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jProgressBar1.setDoubleBuffered(true);
        jProgressBar1.setIndeterminate(true);

        jLabel1.setText("Search Results Summary");

        jLabel2.setText("Search Results Detail");

        jProgressBar1.setVisible(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(575, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(searchStatusField, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(596, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(searchStatusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton4))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // Download Selected Button
        if (FinalGUI.getQueryPanel().isReadyToGo() && lastSetOfIds != null) {
            if (lastSetOfIds.size() > 0) {
                if (resultsTable.getSelectedRowCount() > 0) {
                    FinalGUI.getOptionsDialog().setVisible(true);
                    if (!FinalGUI.getOptionsDialog().wasCancelled()) {
                        downloadSelected();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "You need to select one or more results");
                }
            } else {
                JOptionPane.showMessageDialog(this, "You need to do a search first");
            }
        } else {
            JOptionPane.showMessageDialog(this, "You need to do a search first");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Download First N articles
        if (FinalGUI.getQueryPanel().isReadyToGo() && lastSetOfIds != null) {
            if (lastSetOfIds.size() > 0) {
                FinalGUI.getChooseNDialog().setVisible(true);
                if (!FinalGUI.getChooseNDialog().wasCancelled()) {
                    FinalGUI.getOptionsDialog().setVisible(true);
                    if (!FinalGUI.getOptionsDialog().wasCancelled()) {
                        downloadFirstN();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "You need to do a search first");
            }
        } else {
            JOptionPane.showMessageDialog(this, "You need to do a search first");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Download All button
        if (FinalGUI.getQueryPanel().isReadyToGo() && lastSetOfIds != null) {
            if (lastSetOfIds.size() > 0) {
                FinalGUI.getOptionsDialog().setVisible(true);
                if (!FinalGUI.getOptionsDialog().wasCancelled()) {
                    downloadAll();
                }
            } else {
                JOptionPane.showMessageDialog(this, "You need to do a search first");
            }
        } else {
            JOptionPane.showMessageDialog(this, "You need to do a search first");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Get More results button
        if (FinalGUI.getQueryPanel().isReadyToGo() && lastSetOfIds != null) {
            if (lastSetOfIds.size() > 0) {
                getMoreResults();
            } else {
                JOptionPane.showMessageDialog(this, "You need to do a search first");
            }
        } else {
            JOptionPane.showMessageDialog(this, "You need to do a search first");
        }
    }//GEN-LAST:event_jButton4ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable resultsTable;
    private javax.swing.JTextField searchStatusField;
    // End of variables declaration//GEN-END:variables
}
