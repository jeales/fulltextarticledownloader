/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui.filechooser;

import fulltextarticledownloader.filestore.FileStore;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author James Eales
 */
public class FileStorePreview extends JPanel implements PropertyChangeListener {

    private File currentFile;
    private JTextArea textArea;

    public FileStorePreview(JFileChooser chooser) {
        super(new BorderLayout());
        chooser.addPropertyChangeListener(this);
        setPreferredSize(new Dimension(175, 100));
        textArea = new JTextArea("");
        textArea.setEditable(false);
        JScrollPane jsp = new JScrollPane(textArea);
        //jsp.setPreferredSize(new Dimension(100, 100));
        add(jsp,BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            currentFile = null;
            resetTextArea();
            repaint();
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            currentFile = (File) evt.getNewValue();
            reCalculateTextArea();
            repaint();
        }
    }

    private void reCalculateTextArea() {

        if (currentFile != null && currentFile.isDirectory()) {
            FileStore fs = new FileStore(currentFile,false);
            textArea.setText(fs.getFileStoreSummary());
        }
    }

    private void resetTextArea() {
        textArea.setText("");
    }
}
