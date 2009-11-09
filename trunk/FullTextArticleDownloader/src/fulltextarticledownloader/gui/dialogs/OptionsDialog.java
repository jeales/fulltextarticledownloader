/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui.dialogs;

import fulltextarticledownloader.filestore.FileStore;
import fulltextarticledownloader.gui.panels.OptionsPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author James Eales
 */
public class OptionsDialog extends JDialog {

    private OptionsPanel optionsPanel;

    public OptionsDialog(JFrame parent) {
        super(parent,true);
        optionsPanel = new OptionsPanel(this);
        getRootPane().getContentPane().add(optionsPanel);
        pack();
    }

    public FileStore getFileStore() {
        return optionsPanel.getFileStore();
    }

    public boolean isSavePDF() {
        return optionsPanel.isSavePDF();
    }

    public boolean isSavePlainText() {
        return optionsPanel.isSavePlainText();
    }

    public boolean isSaveXML() {
        return optionsPanel.isSaveXML();
    }

    public boolean wasCancelled() {
        return optionsPanel.wasCancelled();
    }
}
