/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui;

import fulltextarticledownloader.gui.panels.ResultsPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker.StateValue;

/**
 *
 * @author jameseales
 */
public class SearchingPropertyChangeHandler implements PropertyChangeListener {

    private ResultsPanel parent;

    public SearchingPropertyChangeHandler(ResultsPanel parent) {
        this.parent = parent;
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("Event - Property Name: " + evt.getPropertyName() + " New Value: " + evt.getNewValue() + " Old Value: " + evt.getOldValue());
        if (evt.getPropertyName().equals("state")) {
            if (evt.getNewValue() == StateValue.DONE) {
                parent.searchingDone();
            }
        } 
    }
}
