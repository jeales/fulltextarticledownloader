/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui;

import fulltextarticledownloader.beans.DownloadTableMessage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker.StateValue;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jameseales
 */
public class DownloadingPropertyChangeHandler implements PropertyChangeListener {

    private DefaultTableModel model;

    public DownloadingPropertyChangeHandler(DefaultTableModel model) {
        this.model = model;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("Event - Property Name: " + evt.getPropertyName() + " New Value: " + evt.getNewValue() + " Old Value: " + evt.getOldValue());
        String evtName = evt.getPropertyName();
        if (evtName.equals("tableupdate")) {
            updateTableModel(evt.getNewValue());
        } else if (evtName.equals("state")) {
            if (evt.getNewValue() == StateValue.DONE) {
            }
        }
    }

    private void updateTableModel(Object message) {
        DownloadTableMessage m = (DownloadTableMessage) message;
        model.setValueAt(m.getValue(), m.getRowID(), m.getColID());
    }
}
