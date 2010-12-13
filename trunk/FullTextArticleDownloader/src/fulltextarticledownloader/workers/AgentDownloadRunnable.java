/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.workers;

import fulltextarticledownloader.agents.DownloadResult;
import fulltextarticledownloader.agents.MySimpleAgent;
import fulltextarticledownloader.beans.DownloadTableMessage;
import fulltextarticledownloader.downloadtarget.PdfDownloadTarget;
import fulltextarticledownloader.filestore.FileElementType;
import fulltextarticledownloader.filestore.FileStore;
import fulltextarticledownloader.filestore.FileStoreInput;
import fulltextarticledownloader.pdfToText.PdfToTextAutomator;
import fulltextarticledownloader.urlaccess.EUtilsAccessController;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jbot.ncbi.beans.PubMedMetaData;
import jbot.ncbi.beans.UrlIdBean;
import jbot.ncbi.pubmed.PubMedFullTextLinkRetriever;
import jbot.ncbi.pubmed.PubMedMetaDataRetriever;
import org.w3c.dom.Document;

/**
 *
 * @author jameseales
 */
public class AgentDownloadRunnable extends SwingWorker<Void, String> implements StatusMonitor {

    private int rowID;
    private String pmid;
    private FileStore fileStore;
    private boolean pdf, plainText, xml, checkPDF;
    private byte[] xmlBlob, pdfBlob;
    private String plainTextString;
    private String articleTitle;
    private static boolean DO_LOGGING = false;

    static {
        Properties p = new Properties();
        try {
            p.load(new FileReader("fulltextarticledownloader.props"));
            String val = p.getProperty("logging");
            if (val != null) {
                if (val.equals("true")) {
                    DO_LOGGING = true;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AgentDownloadRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            Logger.getLogger(AgentDownloadRunnable.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public AgentDownloadRunnable(int rowID, String pmid, FileStore fileStore, boolean pdf, boolean plainText, boolean xml, boolean checkPDF) {
        this.rowID = rowID;
        this.pmid = pmid;
        this.fileStore = fileStore;
        this.pdf = pdf;
        this.plainText = plainText;
        this.xml = xml;
        this.checkPDF = checkPDF;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (!fileStore.isFileStoreCreated()) {
            fileStore.create();
        }

        doXMLDownload();
        doPDFDownload();
        doPlainTextDownload();

        return null;
    }

    private void doXMLDownload() {
        if (xml) {
            if (!fileStore.contains(pmid, FileElementType.xml)) {
                sendXMLMessage("Starting");
                LinkedList<String> l = new LinkedList<String>();
                l.add(pmid);
                sendURLMessage("Getting data for PubMed ID");
                EUtilsAccessController controller = EUtilsAccessController.getURLAccessControllerRef();
                controller.requestAccess();
                PubMedMetaDataRetriever ret = new PubMedMetaDataRetriever(l);
                Document d = ret.getEFetch().getLastRequest();
                xmlBlob = xmlToBlob(d);
                PubMedMetaData md = ret.getDataByID(pmid);
                articleTitle = md.getArticleTitle();
                sendXMLMessage("Downloaded");
                if (xmlBlob != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(xmlBlob);
                    FileStoreInput in = new FileStoreInput(pmid, FileElementType.xml, bais);
                    fileStore.addFileToFileStore(in);
                    sendXMLMessage("Saved");
                } else {
                    sendXMLMessage("Not Saved");
                }
            } else {
                sendXMLMessage("Already Saved");
            }
        } else {
            sendXMLMessage("N/A");
        }
    }

    private byte[] xmlToBlob(Document doc) {
        DOMSource dom = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", 3);
            Transformer transformer;
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(dom, result);
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] b = writer.toString().getBytes();
        return b;
    }

    private void doPDFDownload() {
        if (pdf) {
            if (!fileStore.contains(pmid, FileElementType.pdf)) {
                sendPDFMessage("Starting");
                sendURLMessage("Getting full text link");
                LinkedList<String> id = new LinkedList<String>();
                id.add(pmid);
                EUtilsAccessController controller = EUtilsAccessController.getURLAccessControllerRef();
                controller.requestAccess();
                PubMedFullTextLinkRetriever linkRet = new PubMedFullTextLinkRetriever(id);
                UrlIdBean linkData = linkRet.getLink(pmid);
                if (linkData != null) {
                    sendURLMessage(linkData.getUrl());
                } else {
                    sendURLMessage("Couldn't find full text link");
                    sendPDFMessage("N/A");
                    pdf = false;
                    plainText = false;
                    return;
                }

                try {
                    MySimpleAgent agent;
                    if (checkPDF) {
                        agent = new MySimpleAgent(this, linkData.getUrl(), new PdfDownloadTarget(articleTitle), DO_LOGGING);
                    } else {
                        agent = new MySimpleAgent(this, linkData.getUrl(), new PdfDownloadTarget(), DO_LOGGING);
                    }
                    agent.runAgent();
                    if (agent.getAgentResult() == DownloadResult.TARGET_FOUND) {
                        sendPDFMessage("Found");
                        pdfBlob = agent.getDownloadTarget();
                        if (pdfBlob != null) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(pdfBlob);
                            FileStoreInput in = new FileStoreInput(pmid, FileElementType.pdf, bais);
                            fileStore.addFileToFileStore(in);
                            sendPDFMessage("Saved");

                        } else {
                            sendPDFMessage("Not Saved");
                        }
                    } else {
                        sendPDFMessage("Not Found");
                    }
                } catch (Exception e) {
                    sendPDFMessage("Download Error : " + e.toString());
                }
            } else {
                sendPDFMessage("Already Saved");
            }
        } else {
            sendPDFMessage("N/A");
        }
    }

    private void doPlainTextDownload() {
        if (plainText) {
            if (!fileStore.contains(pmid, FileElementType.text)) {
                sendPlainTextMessage("Starting");
                PdfToTextAutomator pdfAuto = new PdfToTextAutomator();
                plainTextString = pdfAuto.doTextExtraction(pdfBlob);
                sendPlainTextMessage("Done");
            } else {
                sendPlainTextMessage("Already Saved");
                return;
            }
        } else {
            sendPlainTextMessage("N/A");


        }

        if (plainText) {
            if (plainTextString != null) {
                ByteArrayInputStream bais2 = new ByteArrayInputStream(plainTextString.getBytes());
                FileStoreInput in2 = new FileStoreInput(pmid, FileElementType.text, bais2);
                fileStore.addFileToFileStore(in2);
                sendPlainTextMessage("Saved");
            } else {
                sendPlainTextMessage("Not Saved");
            }
        }
    }

    public void sendURLMessage(String m) {
        sendTableMessage(1, m);
    }

    private void sendXMLMessage(String m) {
        sendTableMessage(2, m);
    }

    private void sendPDFMessage(String m) {
        sendTableMessage(3, m);
    }

    private void sendPlainTextMessage(String m) {
        sendTableMessage(4, m);
    }   

    private void sendTableMessage(int colID, String message) {
        DownloadTableMessage m = new DownloadTableMessage();
        m.setRowID(rowID);
        m.setColID(colID);
        m.setValue(message);
        firePropertyChange("tableupdate", "", m);
    }

    @Override
    protected void done() {
    }

    @Override
    protected void process(List<String> chunks) {
    }

    @Override
    public void reportStatus(String status) {
        sendURLMessage(status);
    }
}
