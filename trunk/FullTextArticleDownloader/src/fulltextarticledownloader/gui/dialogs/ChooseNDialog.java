/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui.dialogs;

import fulltextarticledownloader.gui.panels.ChooseNPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author James Eales
 */
public class ChooseNDialog extends JDialog {

    private ChooseNPanel chooseNPanel;

    public ChooseNDialog(JFrame parent) {
        super(parent,true);
        chooseNPanel = new ChooseNPanel(this);
        getRootPane().getContentPane().add(chooseNPanel);
        pack();
    }

    public int getN() {
        return chooseNPanel.getN();
    }

    public boolean wasCancelled() {
        return chooseNPanel.wasCancelled();
    }
}
