/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fulltextarticledownloader.gui.dialogs;

import fulltextarticledownloader.gui.panels.SearchingPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author jameseales
 */
public class SearchingDialog extends JDialog {

    private static final String TITLE = "Searching PubMed";
    private SearchingPanel panel;

    public SearchingDialog(JFrame parent) {
        super(parent, TITLE, true);
        panel = new SearchingPanel(this);
        getRootPane().getContentPane().add(panel);
        pack();
    }


}
