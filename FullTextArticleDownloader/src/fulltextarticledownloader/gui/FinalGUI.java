/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.gui;

import fulltextarticledownloader.gui.dialogs.ChooseNDialog;
import fulltextarticledownloader.gui.dialogs.OptionsDialog;
import fulltextarticledownloader.gui.dialogs.SearchingDialog;
import fulltextarticledownloader.gui.panels.DownloadPanel;
import fulltextarticledownloader.gui.panels.QueryPanel;
import fulltextarticledownloader.gui.panels.ResultsPanel;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 *
 * @author James Eales
 */
public class FinalGUI {

    private static JFrame frame;
    private static DownloadPanel downloadPanel;
    private static OptionsDialog optionsDialog;
    private static QueryPanel queryPanel;
    private static ResultsPanel resultsPanel;
    private static ChooseNDialog chooseNDialog;
    private static SearchingDialog searchingDialog;

    public FinalGUI() {
        setupFrame();
        frame.setVisible(true);
    }

    private void setupFrame() {
        frame = new JFrame("Full Text Article Downloader");
        optionsDialog = new OptionsDialog(frame);
        chooseNDialog = new ChooseNDialog(frame);
        searchingDialog = new SearchingDialog(frame);
        queryPanel = new QueryPanel();
        downloadPanel = new DownloadPanel();
        resultsPanel = new ResultsPanel();

        Container content = frame.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(queryPanel);
        content.add(resultsPanel);
        content.add(downloadPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    public static ChooseNDialog getChooseNDialog() {
        return chooseNDialog;
    }

    public static DownloadPanel getDownloadPanel() {
        return downloadPanel;
    }

    public static OptionsDialog getOptionsDialog() {
        return optionsDialog;
    }

    public static QueryPanel getQueryPanel() {
        return queryPanel;
    }

    public static ResultsPanel getResultsPanel() {
        return resultsPanel;
    }

    public static SearchingDialog getSearchingDialog() {
        return searchingDialog;
    }

    public static void main(String[] args) {
        FinalGUI app = new FinalGUI();
    }
}
