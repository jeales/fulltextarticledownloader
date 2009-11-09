/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.filelogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author James Eales
 */
public class FileBasedLogger {

    private static String LOG_FILE_NAME = "fulltextarticledownloader/filelogger/logfile.xml";
    private static InputStream FROM_LOG_FILE;
    private static OutputStream TO_LOG_FILE;
    private static FileBasedLogger singleton;

    public static synchronized FileBasedLogger getFileBasedLoggerRef() {
        if (singleton == null) {
            singleton = new FileBasedLogger();
        }
        return singleton;
    }

    private FileBasedLogger() {
    }

    private InputStream getLogInputStream() {
        FROM_LOG_FILE = ClassLoader.getSystemClassLoader().getResourceAsStream(LOG_FILE_NAME);
        return FROM_LOG_FILE;
    }

    private OutputStream getLogOutputStream() {
        if (TO_LOG_FILE == null) {
            try {
                TO_LOG_FILE = new FileOutputStream(LOG_FILE_NAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return TO_LOG_FILE;
    }

    private void generateBaseLogFile(File df) {
        Document dom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        dom = db.newDocument();
        Element top = dom.createElement("logfile");
        Element entries = dom.createElement("logentries");
        top.appendChild(entries);
        dom.appendChild(top);

        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(new DOMSource(dom), new StreamResult(df));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void addLogEntry(LogEntry entry) {
        Document domLog = readLog(getLogInputStream());
        if (domLog != null) {
            addEntry(domLog, entry);
        }
        writeLog(domLog, getLogOutputStream());
    }

    public synchronized LinkedList<LogEntry> getLogEntries() {
        Document domLog = readLog(getLogInputStream());
        LinkedList<LogEntry> entries = extractEntries(domLog);
        return entries;
    }

    private LinkedList<LogEntry> extractEntries(Document dom) {
        LinkedList<LogEntry> l = new LinkedList<LogEntry>();

        XPath x = XPathFactory.newInstance().newXPath();
        try {
            NodeList nl = (NodeList) x.evaluate("/logfile/logentries/logentry", dom, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                LogEntry le = new LogEntry();
                NamedNodeMap nnm = n.getAttributes();
                String et = nnm.getNamedItem("entrytype").getNodeValue();
                String it = nnm.getNamedItem("idtype").getNodeValue();
                String id = nnm.getNamedItem("uniqueid").getNodeValue();

                LogEntry.EntryType[] etypes = LogEntry.EntryType.values();
                for (int j = 0; j < etypes.length; j++) {
                    LogEntry.EntryType entryType = etypes[j];
                    if (et.equals(entryType.toString())) {
                        le.setEntryType(entryType);
                        break;
                    }
                }

                LogEntry.IDType[] itypes = LogEntry.IDType.values();
                for (int j = 0; j < itypes.length; j++) {
                    LogEntry.IDType idType = itypes[j];
                    if (it.equals(idType.toString())) {
                        le.setIdType(idType);
                        break;
                    }
                }

                le.setUniqueID(id);
                l.add(le);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return l;
    }

    private void writeLog(Document toWrite, OutputStream os) {

        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(new DOMSource(toWrite), new StreamResult(os));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        }
    }

    private void addEntry(Document dom, LogEntry toAdd) {
        Element le = dom.createElement("logentry");
        le.setAttribute("entrytype", toAdd.getEntryType().toString());
        le.setAttribute("idtype", toAdd.getIdType().toString());
        le.setAttribute("uniqueid", toAdd.getUniqueID());


        XPath x = XPathFactory.newInstance().newXPath();

        try {
            Node n = (Node) x.evaluate("/logfile/logentries", dom.getDocumentElement(), XPathConstants.NODE);
            n.appendChild(le);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private Document readLog(InputStream is) {
        Document d = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            d = db.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


        return d;
    }
}
