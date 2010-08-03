/*
 * QueryPanel.java
 *
 * Created on 04 November 2008, 11:43
 */
package fulltextarticledownloader.gui.panels;

import fulltextarticledownloader.gui.FinalGUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbot.ncbi.pubmed.QueryField;
import jbot.ncbi.pubmed.QueryTerm;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author  James Eales
 */
public class QueryPanel extends javax.swing.JPanel {

    private DefaultListModel queryTermListModel;
    private boolean readyToGo = false;
    private JFileChooser pmidFileChooser;
    private Pattern digitCheckerP;

    /** Creates new form QueryPanel */
    public QueryPanel() {
        pmidFileChooser = new JFileChooser();
        digitCheckerP = Pattern.compile("^\\d+$");
        queryTermListModel = new DefaultListModel();
        initComponents();
    }

    public boolean isReadyToGo() {
        return readyToGo;
    }

    public void setReadyToGo() {
        readyToGo = true;
    }

    private void addTerm(String text, QueryField field) {
        if (!readyToGo) {
            readyToGo = true;
        }
        //System.out.println("Adding Term - Query: '" + text + "' Field: " + field.toString());
        QueryTerm q = new QueryTerm(text, field);
        queryTermListModel.addElement(q);
    }

    private void resetQuery() {
        queryTermListModel.removeAllElements();
        readyToGo = false;
    }

    private void deleteSelectedQueryTerm() {
        int ind = queryTermList.getSelectedIndex();
        if (ind == -1) {
            JOptionPane.showMessageDialog(deleteSearchTermButton, "You need to select a search term");
        } else {
            queryTermListModel.remove(ind);
            if (queryTermListModel.size() == 0) {
                readyToGo = false;
            }
        }
    }

    public Vector<QueryTerm> getQueryTerms() {
        Vector<QueryTerm> terms = new Vector<QueryTerm>();
        for (Enumeration e = queryTermListModel.elements(); e.hasMoreElements();) {
            terms.add((QueryTerm) e.nextElement());
        }
        return terms;
    }

    private ComboBoxModel generateSearchFieldsComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(QueryField.values());
        return model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        queryTextField = new javax.swing.JTextField();
        searchFieldsComboBox = new javax.swing.JComboBox();
        addSearchTermButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        queryTermList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        deleteSearchTermButton = new javax.swing.JButton();
        clearQueryButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        loadPMIDsButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        queryTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        searchFieldsComboBox.setModel(generateSearchFieldsComboBoxModel());

        addSearchTermButton.setText("Add search term");
        addSearchTermButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addSearchTermButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addSearchTermButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSearchTermButtonActionPerformed(evt);
            }
        });

        queryTermList.setModel(queryTermListModel);
        queryTermList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(queryTermList);

        deleteSearchTermButton.setText("Delete Query Term");
        deleteSearchTermButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSearchTermButtonActionPerformed(evt);
            }
        });

        clearQueryButton.setText("Reset Query");
        clearQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearQueryButtonActionPerformed(evt);
            }
        });

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        loadPMIDsButton.setText("Load PMIDs from file");
        loadPMIDsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPMIDsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(deleteSearchTermButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearQueryButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadPMIDsButton)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(clearQueryButton)
                .addComponent(searchButton)
                .addComponent(loadPMIDsButton))
            .addComponent(deleteSearchTermButton)
        );

        jLabel1.setText("Build Query");

        jLabel2.setText("Query Detail");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(600, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchFieldsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(queryTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addSearchTermButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(594, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addSearchTermButton)
                    .addComponent(searchFieldsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addSearchTermButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSearchTermButtonActionPerformed
        // add search term button
        String queryText = queryTextField.getText();
        queryText = queryText.trim();
        if (queryText != null) {
            if (queryText.length() > 0) {
                QueryField field = (QueryField) searchFieldsComboBox.getSelectedItem();
                if (field == null) {
                    JOptionPane.showMessageDialog(searchFieldsComboBox, "Please select a search field");
                } else {
                    addTerm(queryText, field);
                }
            } else {
                JOptionPane.showMessageDialog(queryTextField, "Please enter a query");
            }
        } else {
            JOptionPane.showMessageDialog(queryTextField, "Please enter a query");
        }
    }//GEN-LAST:event_addSearchTermButtonActionPerformed

    private void clearQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearQueryButtonActionPerformed
        // clear query button
        resetQuery();
    }//GEN-LAST:event_clearQueryButtonActionPerformed

    private void deleteSearchTermButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSearchTermButtonActionPerformed
        // TODO add your handling code here:
        deleteSelectedQueryTerm();
    }//GEN-LAST:event_deleteSearchTermButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        if (this.isReadyToGo()) {
            FinalGUI.getResultsPanel().doSearch();
        } else {
            JOptionPane.showMessageDialog(this, "You need to complete your query");
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void loadPMIDsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPMIDsButtonActionPerformed
        // TODO add your handling code here:
        pmidFileChooser.showDialog(this, "Load PMIDs");
        File chosenFile = pmidFileChooser.getSelectedFile();
        if (chosenFile != null) {
            LinkedHashSet<String> uniques = new LinkedHashSet<String>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(chosenFile));
                String line = "";
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    Matcher digitChecker = digitCheckerP.matcher(line);
                    if (digitChecker.matches()) {
                        uniques.add(line);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(QueryPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Could not find file: " + chosenFile.getAbsolutePath());

            } catch (IOException ex) {
                Logger.getLogger(QueryPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Could not access file: " + chosenFile.getAbsolutePath());
            }

            for (String string : uniques) {
                System.out.println(string);
            }
            LinkedList<String> ids = new LinkedList<String>();
            ids.addAll(uniques);
            FinalGUI.getResultsPanel().addStaticSetOfIDs(ids);
        }
    }//GEN-LAST:event_loadPMIDsButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSearchTermButton;
    private javax.swing.JButton clearQueryButton;
    private javax.swing.JButton deleteSearchTermButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadPMIDsButton;
    private javax.swing.JList queryTermList;
    private javax.swing.JTextField queryTextField;
    private javax.swing.JButton searchButton;
    private javax.swing.JComboBox searchFieldsComboBox;
    // End of variables declaration//GEN-END:variables
}
