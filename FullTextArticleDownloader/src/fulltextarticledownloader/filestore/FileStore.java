/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.filestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
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
public class FileStore {

    public static final String FILESTORE_FILENAME = "filestore.xml";
    private FileStoreIndexFile fileStoreIndex;
    private boolean doneProcessing, fileStoreCreated, closed;
    private int totalNumFiles, numPdfFiles, numTextFiles, numXmlFiles;
    private Document document;
    private Element fileListElement;
    private LinkedList<FileStoreFileElement> pdfFiles;
    private LinkedList<FileStoreFileElement> textFiles;
    private LinkedList<FileStoreFileElement> xmlFiles;
    private final static String FILESTORE_SCHEMA = "/fulltextarticledownloader/files/filestoreschema.xsd";
    private File pdfDir, textDir, xmlDir, baseDir;
    private final static String PDF_DIR = "pdf/";
    private final static String TEXT_DIR = "text/";
    private final static String XML_DIR = "xml/";

    public FileStore(File directory, boolean create) {
        this(new FileStoreIndexFile(directory, FILESTORE_FILENAME), create);
    }

    private FileStore(FileStoreIndexFile xmlFile, boolean create) {
        setDoneProcessing(false);
        setFsf(xmlFile);

        if (xmlFile.exists()) {
            setFileStoreCreated(true);
            doProcessing();
        } else {
            setFileStoreCreated(false);
            if (create) {
                initialiseBaseFileStore();
            }
        }
    }

    public void create() {
        if (!isFileStoreCreated()) {
            initialiseBaseFileStore();
        }
    }

    private void initialiseBaseFileStore() {
        createXMLFile();
    }

    public synchronized boolean isFileStoreCreated() {
        return fileStoreCreated;
    }

    public synchronized int getTotalNumFiles() {
        return getTotalFileCount();
    }

    public synchronized int getNumOfPDFFiles() {
        return getFileCountByFileType(FileElementType.pdf);
    }

    public synchronized int getNumOfTextFiles() {
        return getFileCountByFileType(FileElementType.text);
    }

    public synchronized int getNumOfXMLFiles() {
        return getFileCountByFileType(FileElementType.xml);
    }

    private synchronized void flush() {
        if (isFileStoreCreated()) {
            saveFileListIndex();
        }
    }

    public synchronized void closeFileStore() {
        flush();
    }

    private int getFileCountByFileType(FileElementType fileElementType) {
        int r = 0;

        if (isFileStoreCreated()) {
            try {
                XPathExpression exp = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='" + fileElementType.toString() + "']");
                r = ((NodeList) exp.evaluate(document, XPathConstants.NODESET)).getLength();
            } catch (XPathExpressionException ex) {
                Logger.getLogger(FileStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            r = 0;
        }

        return r;
    }

    private int getTotalFileCount() {
        int r = 0;

        if (isFileStoreCreated()) {
            try {
                XPathExpression exp = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file");
                r = ((NodeList) exp.evaluate(document, XPathConstants.NODESET)).getLength();
            } catch (XPathExpressionException ex) {
                Logger.getLogger(FileStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            r = 0;
        }

        return r;
    }

    private void setTotalNumFiles(int totalNumFiles) {
        this.totalNumFiles = totalNumFiles;
    }

    private void setFileStoreCreated(boolean fileStoreCreated) {
        this.fileStoreCreated = fileStoreCreated;
    }

    private void setNumOfPDFFiles(int numPdfFiles) {
        this.numPdfFiles = numPdfFiles;
    }

    private void setNumOfTextFiles(int numTextFiles) {
        this.numTextFiles = numTextFiles;
    }

    private void setNumOfXMLFiles(int numXmlFiles) {
        this.numXmlFiles = numXmlFiles;
    }

    private Element getFileListElement() {
        return fileListElement;
    }

    private void setFileListElement(Element fileListElement) {
        this.fileListElement = fileListElement;
    }

    private void setFsf(FileStoreIndexFile fsf) {
        fileStoreIndex = fsf;
    }

    private FileStoreIndexFile getFsf() {
        return fileStoreIndex;
    }

    private void setDoneProcessing(boolean b) {
        doneProcessing = b;
    }

    private boolean isDoneProcessing() {
        return doneProcessing;
    }

    public synchronized void addFileToFileStore(FileStoreInput fileStoreInput) {
        if (!isFileStoreCreated()) {
            createXMLFile();
        }

        if (!contains(fileStoreInput)) {
            saveFileToFileStore(fileStoreInput);
            addFileRecordToIndex(fileStoreInput.getPmid(), fileStoreInput.getInputType());
            doFileCounting();
            flush();
        }
    }

    public boolean contains(String id, FileElementType fileElementType) {
        //default that the input should be allowed
        boolean r = false;

        r = doesDOMContain(id, fileElementType);

        return r;
    }

    private boolean contains(FileStoreInput potentialInput) {
        //default that the input should be allowed
        boolean r = false;

        r = contains(potentialInput.getPmid(), potentialInput.getInputType());

        return r;
    }

    private boolean doesDOMContain(String pmid, FileElementType fileElementType) {
        boolean r = false;
        try {
            String query = "/filestore/filelist/file[@pmid='" + pmid + "' and @type='" + fileElementType.toString() + "']";
            XPathExpression exp = XPathFactory.newInstance().newXPath().compile(query);
            int size = ((NodeList) exp.evaluate(document, XPathConstants.NODESET)).getLength();
            if (size > 0) {
                r = true;
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(FileStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    private void addFileRecordToIndex(String pmid, FileElementType fileElementType) {
        Element file = document.createElement("file");
        file.setAttribute(FileElementAttributes.pmid.toString(), pmid);
        file.setAttribute(FileElementAttributes.type.toString(), fileElementType.toString());
        addFileElementToFileListElement(file);
    }

    private void addFileElementToFileListElement(Element toAdd) {
        fileListElement.appendChild(toAdd);
    }

    private void saveFileToFileStore(FileStoreInput fileStoreInput) {
        File dir = getDirectoryToSave(fileStoreInput.getInputType());
        if (dir != null) {
            File toSave = new File(dir, fileStoreInput.getPmid() + fileStoreInput.getInputType().getFileSuffix());
            try {
                FileOutputStream fos = new FileOutputStream(toSave);
                readAndWriteStreamsFully(fileStoreInput.getInputStream(), fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void readAndWriteStreamsFully(InputStream source, OutputStream sink) {
        //16kB binary buffer
        byte[] buffer = new byte[1024 * 16];
        int bytesRead = 0;

        try {
            while ((bytesRead = source.read(buffer)) != -1) {
                sink.write(buffer, 0, bytesRead);
            }

            source.close();
            sink.flush();
            sink.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File getDirectoryToSave(FileElementType fileElementType) {
        File dir = null;

        if (baseDir == null) {
            baseDir = getFsf().getRootDir();
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
        }

        if (fileElementType == FileElementType.pdf) {
            if (pdfDir == null) {
                pdfDir = new File(baseDir, PDF_DIR);
                pdfDir.mkdirs();
            }
            dir = pdfDir;
        } else if (fileElementType == FileElementType.text) {
            if (textDir == null) {
                textDir = new File(baseDir, TEXT_DIR);
                textDir.mkdirs();
            }
            dir = textDir;
        } else if (fileElementType == FileElementType.xml) {
            if (xmlDir == null) {
                xmlDir = new File(baseDir, XML_DIR);
                xmlDir.mkdirs();
            }
            dir = xmlDir;
        }

        return dir;
    }

    private void createXMLFile() {
        getFsf().createFileStoreDirectory();

        try {
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fact.newDocumentBuilder();
            document = db.newDocument();
            Element filestore = document.createElement("filestore");
            Element filelist = document.createElement("filelist");
            filestore.appendChild(filelist);
            document.appendChild(filestore);
            setFileListElement(filelist);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FileStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        saveFileListIndex();
        setFileStoreCreated(true);
        doProcessing();
    }

    private void saveFileListIndex() {
        TransformerFactory fact = TransformerFactory.newInstance();
        try {
            Transformer transformer = fact.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMSource source = new DOMSource(document);
            FileStoreIndexFile fsif = getFsf();
            StreamResult sink = new StreamResult(fsif);
            transformer.transform(source, sink);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void doProcessing() {
        //this method only operates on the FileStoreIndexFile object i.e. the actual file on the disk.

        int t = 0, i = 0;
        pdfFiles = new LinkedList<FileStoreFileElement>();
        textFiles = new LinkedList<FileStoreFileElement>();
        xmlFiles = new LinkedList<FileStoreFileElement>();

        try {
            //SchemaFactory f = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            //URL u = this.getClass().getResource(FILESTORE_SCHEMA);
            //Schema myFileStoreSchema = f.newSchema(u);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            //fact.setSchema(myFileStoreSchema);
            DocumentBuilder db = fact.newDocumentBuilder();

            XPathExpression pdf = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='pdf']");
            XPathExpression text = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='text']");
            XPathExpression xml = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='xml']");
            XPathExpression filelist = XPathFactory.newInstance().newXPath().compile("/filestore/filelist");

            document = db.parse(fileStoreIndex);

            Element temp = (Element) filelist.evaluate(document.getDocumentElement(), XPathConstants.NODE);

            if (temp != null) {
                setFileListElement(temp);
            }

            NodeList pdfNL = (NodeList) pdf.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
            if (pdfNL != null) {
                addNodesToList(pdfNL, pdfFiles);
                i = pdfFiles.size();
                t += i;
                setNumOfPDFFiles(i);
            }

            NodeList textNL = (NodeList) text.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
            if (textNL != null) {
                addNodesToList(textNL, textFiles);
                i = textFiles.size();
                t += i;
                setNumOfTextFiles(i);
            }

            NodeList xmlNL = (NodeList) xml.evaluate(document.getDocumentElement(), XPathConstants.NODESET);
            if (xmlNL != null) {
                addNodesToList(xmlNL, xmlFiles);
                i = xmlFiles.size();
                t += i;
                setNumOfXMLFiles(i);
            }

            setTotalNumFiles(t);
            setDoneProcessing(true);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void doFileCounting() {
        //this method only operates on the in memory DOM
        try {
            int tot = 0;

            XPathExpression pdf = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='pdf']");
            int pdfC = ((NodeList) pdf.evaluate(document, XPathConstants.NODESET)).getLength();
            setNumOfPDFFiles(pdfC);
            tot += pdfC;

            XPathExpression text = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='text']");
            int textC = ((NodeList) text.evaluate(document, XPathConstants.NODESET)).getLength();
            setNumOfTextFiles(textC);
            tot += textC;

            XPathExpression xml = XPathFactory.newInstance().newXPath().compile("/filestore/filelist/file[@type='xml']");
            int xmlC = ((NodeList) xml.evaluate(document, XPathConstants.NODESET)).getLength();
            setNumOfXMLFiles(xmlC);
            tot += xmlC;

            setTotalNumFiles(tot);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

    }

    private void addNodesToList(NodeList nodeList, LinkedList<FileStoreFileElement> fileElements) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            NamedNodeMap atts = n.getAttributes();
            String pmid = atts.getNamedItem(FileElementAttributes.pmid.toString()).getNodeValue();
            FileElementType type = FileElementType.getTypeFromString(atts.getNamedItem(FileElementAttributes.type.toString()).getNodeValue());
            File file = new File(getDirectoryToSave(type), pmid + "." + type.toString());
            FileStoreFileElement fsfe = new FileStoreFileElement(pmid, type, file);
            fileElements.add(fsfe);
        }
    }

    public synchronized String getFileStoreSummary() {
        StringBuilder sb = new StringBuilder();

        if (isFileStoreCreated()) {
            sb.append("FileStore Contents Summmary\n\n");
            sb.append("PDF Files:   " + getNumOfPDFFiles() + "\n");
            sb.append("Text Files:  " + getNumOfTextFiles() + "\n");
            sb.append("XML Files:   " + getNumOfXMLFiles() + "\n");
            sb.append("Total Files: " + getTotalNumFiles());
        } else {
            sb.append("Not A FileStore");
        }

        return sb.toString();
    }

    @Override
    public synchronized String toString() {
        return getFileStoreSummary();
    }
}
