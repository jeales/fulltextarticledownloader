/*
 * MySimpleAgent.java
 *
 * Created on 05 October 2007, 16:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fulltextarticledownloader.agents;

import com.dappit.Dapper.parser.MozillaParser;
import fulltextarticledownloader.beans.LinkBean;
import fulltextarticledownloader.beans.WaitingDetails;
import fulltextarticledownloader.downloadtarget.DownloadTarget;
import fulltextarticledownloader.downloadtarget.PdfDownloadTarget;
import fulltextarticledownloader.urlaccess.URLAccessController;
import fulltextarticledownloader.util.XMLTextExtractor;
import fulltextarticledownloader.workers.StatusMonitor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.dom4j.dom.DOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author James
 */
public class MySimpleAgent {

    private int currentRecurseLevel = 0;
    private static int MAX_RECURSE_LEVEL = 3;
    private static int ITER_LIMIT = 3;
    private XPathFactory xpFactory;
    private XPathExpression linkTagFinder, linkAttFinder;
    private String startingUrl;
    private DownloadTarget downloadTargetTest;
    private String resolvedUrl;
    private boolean target_found = false;
    private boolean html_found = false;
    private String cpolicy = CookiePolicy.BROWSER_COMPATIBILITY;
    private MozillaParser parser;
    private Document dom;
    private MySimpleAgent parent;
    private String page;
    private byte[] downloadTarget;
    private String currentURL;
    //64k downloadTarget buffer
    private int fileblobsize = 1024 * 64;
    private static boolean loggingEnabled;
    private static Logger logger;
    private StatusMonitor statusMonitor;
    private static final String USER_AGENT_STRING = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-gb) AppleWebKit/531.9 (KHTML, like Gecko) Version/4.0.3 Safari/531.9";
    private URLAccessController theFatController;
    private HttpClient httpClient;

    public MySimpleAgent(StatusMonitor statusMonitor, String url, DownloadTarget downloadTarget) {
        this(statusMonitor, url, downloadTarget, false);
    }

    public MySimpleAgent(StatusMonitor statusMonitor, String url, DownloadTarget downloadTarget, boolean enableLogging) {
        if (enableLogging) {
            enableLogging();
        }

        setupParser();

        this.statusMonitor = statusMonitor;
        this.currentRecurseLevel = 1;
        this.startingUrl = url;
        this.downloadTargetTest = downloadTarget;
        currentURL = url;
        theFatController = URLAccessController.getURLAccessControllerRef();
        httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
        httpClient.getParams().setParameter("http.useragent", USER_AGENT_STRING);
        logTopURL(startingUrl);
        xpFactory = XPathFactory.newInstance();
        try {
            linkTagFinder = xpFactory.newXPath().compile("//a | //frame | //meta");
            linkAttFinder = xpFactory.newXPath().compile("//@href");
        } catch (XPathExpressionException ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    //the constructor that is called recursively
    private MySimpleAgent(String url, DownloadTarget downloadTarget, boolean recurse, int level, MySimpleAgent parental) {
        currentRecurseLevel = level;
        startingUrl = url;
        downloadTargetTest = downloadTarget;
        currentURL = url;
        logRecursiveTop(level);
        logRecursiveTopURL(currentURL, level);
        parent = parental;
        theFatController = URLAccessController.getURLAccessControllerRef();
        xpFactory = XPathFactory.newInstance();
        try {
            linkTagFinder = xpFactory.newXPath().compile("//a | //frame | //meta");
            linkAttFinder = xpFactory.newXPath().compile("//@href");
        } catch (XPathExpressionException ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        try {
            if (!url.equals("") && url.contains("http://")) {

                HttpClient c = parent.getHttpClient();
                if (c == null) {
                    httpClient = new HttpClient();
                    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
                    httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
                    httpClient.getParams().setParameter("http.useragent", USER_AGENT_STRING);

                } else {
                    httpClient = c;
                }

                try {
                    WaitingDetails waiting = new WaitingDetails();
                    GetMethod g = theFatController.requestURL(url, waiting);
                    logWaitingDetails(url, waiting);
                    doStatusReport(url);
                    g.getParams().setCookiePolicy(cpolicy);
                    g.setFollowRedirects(true);
                    int rcode = httpClient.executeMethod(g);
                    resolvedUrl = g.getURI().toString();
                    currentURL = resolvedUrl;
                    logRecursiveResolvedTopURL(resolvedUrl, level);
                    org.apache.commons.httpclient.URI base = g.getURI();
                    byte[] tPage = readResponse(g.getResponseBodyAsStream());

                    if (isDownloadTarget(tPage, resolvedUrl)) {
                        targetFound(tPage);
                    } else {
                        pageFound(tPage);
                        try {
                            if (parser == null) {
                                setupParser();
                            }
                            dom = parser.parse(new String(tPage));
                            //dom = parser.parse(new String(tPage));
                            //MozillaAutomation.blockingLoadHTML(parser, new String(tPage), null);
                            //MozillaAutomation.triggerLoadHTML(parser, new String(tPage), null);
                            //parser.loadHTML(new String(tPage));
                            //dom = parser.getDocument();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        LinkedList<LinkBean> links = extractLinks(dom);

                        if (this.html_found) {
                            if (recurse) {
                                Collections.sort(links);
                                logAllSortedLinks(links);
                                for (LinkBean elem : links) {
                                    logChosenAndStartingLink(elem, startingUrl);
                                    if (!(elem.getURL().equals(this.startingUrl)) || !(elem.getURL().equals(this.resolvedUrl))) {
                                        if (currentRecurseLevel + 1 <= MAX_RECURSE_LEVEL) {
                                            logChosenLink(elem);
                                            MySimpleAgent p = new MySimpleAgent(elem.getURL(), this.downloadTargetTest, true, this.currentRecurseLevel + 1, this);
                                            if (p.getAgentResult() == DownloadResult.TARGET_FOUND) {
                                                tellParent(p.getAgentResult());
                                                this.downloadTarget = p.getDownloadTarget();
                                                break;
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception ex) {
        }
    }

    public static void enableLogging() {
        loggingEnabled = true;
        if (logger == null) {
            setupLogger();
        }
    }

    private static void setupLogger() {
        try {
            MySimpleAgent.logger = Logger.getLogger(MySimpleAgent.class);
            MySimpleAgent.logger.setLevel(Level.ALL);
            FileAppender app = new FileAppender(new SimpleLayout(), "MySimpleAgent.log");
            MySimpleAgent.logger.addAppender(app);
            //ConsoleAppender cons = new ConsoleAppender(new SimpleLayout(), "System.err");
            //MySimpleAgent.logger.addAppender(cons);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public void disableLogging() {
        loggingEnabled = false;
    }

    private static void logEvent(String message) {
        if (loggingEnabled) {
            MySimpleAgent.logger.log(Level.DEBUG, "THREAD_ID: " + Thread.currentThread().getId() + " " + message);
        }
    }

    private void logWaitingDetails(String url, WaitingDetails wait) {
        logEvent("URLAccessRequest: " + url + " Host: " + wait.getHost() + " LastRequestToHost: " + wait.getLastRequestToHost() + " WaitTime: " + wait.getWaitTime());
    }

    private void logTopURL(String topURL) {
        logEvent("TopURL: " + topURL);
    }

    private void logResolvedTopURL(String resolvedTopURL) {
        logEvent("ResolvedTopURL: " + resolvedTopURL);
    }

    private void logRecursiveTop(int recursiveLevel) {
        logEvent("RecursiveLevel: " + recursiveLevel);
    }

    private void logRecursiveTopURL(String recursiveTopURL, int recursiveLevel) {
        logEvent("RecursiveLevel: " + recursiveLevel + " RecursiveTopURL: " + recursiveTopURL);
    }

    private void logRecursiveResolvedTopURL(String recursiveResolvedTopURL, int recursiveLevel) {
        logEvent("RecursiveLevel: " + recursiveLevel + " RecursiveResolvedTopURL: " + recursiveResolvedTopURL);
    }

    private void logIsTarget(boolean isTarget) {
        logEvent("IsTarget: " + isTarget);
    }

    private void logExtractLinks(int numLinksFound) {
        logEvent("NumLinksFound: " + numLinksFound);
    }

    private void logChosenLink(LinkBean chosenLink) {
        logEvent("ChosenLink: " + chosenLink.toString());
    }

    private void logChosenAndStartingLink(LinkBean chosenLink, String startingLink) {
        logEvent("StartingLink: " + startingLink + " ChosenLink: " + chosenLink.getURL());
    }

    private void logAllSortedLinks(LinkedList<LinkBean> allSortedLinks) {
        for (LinkBean linkBean : allSortedLinks) {
            logEvent("AllSortedLink: " + linkBean.toString());
        }
    }

    private void logIterationNumber(int iterationNumber) {
        logEvent("TopURL: starting iteration " + iterationNumber);
    }

    public String getStartingUrl() {
        return startingUrl;
    }

    public String getCurrentURL() {
        return currentURL;
    }

    private void doStatusReport(String statusReport) {
        if (statusMonitor != null) {
            statusMonitor.reportStatus(statusReport);
        }
    }

    public void runAgent() {

        if (parser == null) {
            return;
        }

        try {
            if (!getCurrentURL().equals("") && getCurrentURL().contains("http://")) {
                int currentIteration = 0;
                try {
                    WaitingDetails waiting = new WaitingDetails();
                    GetMethod g = theFatController.requestURL(getCurrentURL(), waiting);
                    logWaitingDetails(getCurrentURL(), waiting);
                    doStatusReport(getCurrentURL());
                    g.getParams().setCookiePolicy(cpolicy);
                    g.setFollowRedirects(true);
                    int rcode = httpClient.executeMethod(g);
                    resolvedUrl = g.getURI().toString();
                    currentURL = resolvedUrl;
                    logResolvedTopURL(resolvedUrl);
                    //URI base = g.getURI();
                    byte[] tPage = readResponse(g.getResponseBodyAsStream());

                    if (isDownloadTarget(tPage, resolvedUrl)) {
                        targetFound(tPage);
                    } else {
                        pageFound(tPage);
                        try {
                            if (parser == null) {
                                setupParser();
                            }
                            dom = parser.parse(new String(tPage));
                            //parser.callNativeHtmlParser(new String(tPage));

                            //dom = parser.parse(tPage, "UTF-8");
                            //dom = parser.parse(new String(tPage));
                            //MozillaAutomation.blockingLoadHTML(parser, new String(tPage), null);
                            //parser.loadHTML(new String(tPage));
                            //dom = parser.getDocument();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            //System.exit(1);
                        }

                        LinkedList<LinkBean> links = extractLinks(dom);

                        if (this.html_found) {
                            Collections.sort(links);
                            logAllSortedLinks(links);
                            for (LinkBean elem : links) {
                                if (currentIteration < ITER_LIMIT) {
                                    logIterationNumber(currentIteration);
                                    logChosenAndStartingLink(elem, startingUrl);
                                    if (!(elem.getURL().equals(this.startingUrl)) || !(elem.getURL().equals(this.resolvedUrl))) {
                                        logChosenLink(elem);
                                        MySimpleAgent p = new MySimpleAgent(elem.getURL(), this.downloadTargetTest, true, this.currentRecurseLevel + 1, this);
                                        if (p.getAgentResult() == DownloadResult.TARGET_FOUND) {
                                            target_found = true;
                                            this.downloadTarget = p.getDownloadTarget();
                                            break;
                                        }
                                        currentIteration++;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    //just keep on truckin'
                    //System.out.println(e.toString());
                    e.printStackTrace();
                    //System.exit(0);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private boolean isDownloadTarget(byte[] blob, String url) {
        boolean a = false;

        a = downloadTargetTest.isDownloadTarget(blob, url);
        logIsTarget(a);

        return a;
    }

    private byte[] readResponse(InputStream stream) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            byte[] buf = new byte[fileblobsize];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) != -1) {
                bos.write(buf, 0, bytesRead);
            }

            bos.flush();
            bos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        byte[] bar = bos.toByteArray();

        return bar;
    }

    private void pageFound(byte[] blob) {
        this.page = new String(blob);
        html_found = true;
    }

    private void targetFound(byte[] blob) {
        this.downloadTarget = blob;
        target_found = true;
    }

    public String getResolvedUrl() {
        return resolvedUrl;
    }

    private void setupParser() {

        try {
            String os = System.getProperty("os.name");
            os = os.toLowerCase();
            File parserLibrary = null;
            if (os.startsWith("win")) {
                parserLibrary = new File("native/MozillaParser.dll");
            } else if (os.startsWith("mac")) {
                parserLibrary = new File("native/libMozillaParser.jnilib");
            } else {
                parserLibrary = new File("native/libMozillaParser.so");
            }

            MozillaParser.init(parserLibrary.getAbsolutePath(), new File("native/").getAbsolutePath());
            parser = new MozillaParser();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
        }


        
        //boolean isPPC = System.getProperty("os.arch").equals("ppc");
        //boolean isIntel = System.getProperty("os.arch").equals("i386");

        /*
        String os = System.getProperty("os.name");
        String sharedLibrary, componentsBase;
        if (os.startsWith("Win")) {
        File lib = new File("libraries/windows/MozillaParser.dll");
        sharedLibrary = lib.getAbsolutePath();
        File comp = new File("libraries/windows");
        componentsBase = comp.getAbsolutePath();
        } else if (os.startsWith("Mac")) {
        //File parserWorker = new File("native/macosx/mozilla/parserWorker");
        //String pwPath = parserWorker.getAbsolutePath();
        //System.load(pwPath);
        if (isIntel) {
        File base = new File("libraries/macosx/i386/");
        //System.load(base.getAbsolutePath() + "/libjsj.dylib");
        //System.load("/libnspr4.dylib");
        //System.load("/libplc4.dylib");
        //System.load("/libplds4.dylib");
        //System.load("/libxpcom_compat.dylib");
        //System.load("/libxpcom_core.dylib");
        //System.load("/libxpcom.dylib");
        //System.load("/libxpistub.dylib");
        //File l = new File(base, "libxpcom.dylib");
        //String libPath = l.getAbsolutePath();
        //System.load(libPath);
        File lib = new File("libraries/macosx/i386/MozillaParser.jnilib");
        sharedLibrary = lib.getAbsolutePath();
        File comp = new File("libraries/macosx/i386/mozilla/components/");
        componentsBase = comp.getAbsolutePath();
        try {
        MozillaParser.init(sharedLibrary, componentsBase);
        //MozillaParser.init();
        parser = new MozillaParser();
        //MozillaInitialization.initialize();
        //parser = new MozillaParser();
        //parser.setVisible(true);
        //parser.setVisible(true);
        } catch (ParserInitializationException ex) {
        java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        ex.printStackTrace();
        System.exit(1);
        }
        }
        } else {
        // File parserWorker = new File("native/linux/i386/parserWorker");
        // String pwPath = parserWorker.getAbsolutePath();
        // System.load(pwPath);
        File lib = new File("libraries/linux/i386/libMozillaParser.so");
        sharedLibrary = lib.getAbsolutePath();
        File comp = new File("libraries/linux");
        componentsBase = comp.getAbsolutePath();
        }
         */
        //  System.out.println(System.getProperties());
        //MozillaInitialization.initialize();
        //System.loadLibrary("MozillaParser");
        //System.load(new File("libxpcom.dylib").getAbsolutePath());

        //System.out.println(System.getProperty("java.library.path"));

        //Properties p = System.getProperties();

        //try {
        // Map<String, String> env = System.getenv();
        // System.out.println("PATH : " + env.get("PATH"));
        //for (String string : env.keySet()) {
        //    System.out.println(string + " = " + env.get(string));
        //}

        //Thread.sleep(1000);
        //File lib = new File("libMozillaParser.jnilib");
        //MozillaParser.init(lib.getAbsolutePath(), null);
        //System.loadLibrary("MozillaParser");
        //File lib = new File("libnspr4.dylib");
        //System.load(lib.getAbsolutePath());
        //lib = new File("libplds4.dylib");
        //System.load(lib.getAbsolutePath());
        //lib = new File("libxpcom_core.dylib");
        //System.load(lib.getAbsolutePath());
        //File lib = new File("libxpcom.dylib");
        //System.loadLibrary("nspr4");
        //System.loadLibrary("softokn3");
        //System.loadLibrary("softokn3");


        //System.loadLibrary("nss3");
        //System.loadLibrary("mozz");
        //System.loadLibrary("plds4");
        //System.loadLibrary("xpcom_core");
        //System.loadLibrary("xpcom");
        //File lib = new File("MozillaParser.jnilib");
        //System.loadLibrary("MozillaParser");


        //} catch (Exception e) {
        //    e.printStackTrace();
        //    System.exit(1);
        //}

        /*
        File lib = new File("MozillaParser.jnilib");
        try {
        MozillaParser.init(lib.getAbsolutePath(), null);
        } catch (Exception ex) {
        java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        ex.printStackTrace();
        System.exit(1);
        }
         */


        //parser.setVisible(true);
        //parser.setVisible(true);


        //parser.setVisible(true);

        //parser.setVisible(true);


    }

    public DownloadResult getAgentResult() {
        if (target_found) {
            return DownloadResult.TARGET_FOUND;
        } else {
            return DownloadResult.HTML_FOUND;
        }
    }

    private String getDocumentAsString(Document domDoc) {
        String r = "";
        try {
            TransformerFactory fact = TransformerFactory.newInstance();
            Transformer trans = fact.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(domDoc), new StreamResult(sw));
            r = sw.toString();
        } catch (TransformerConfigurationException ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
        return r;
    }

    private NodeList getAllMatchingNodes(Document dom, XPathExpression exp) {
        try {
            NodeList n = (NodeList) exp.evaluate(dom, XPathConstants.NODESET);
            return n;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }
    }

    private LinkedList<LinkBean> extractLinks(Document d) {
        LinkedList<LinkBean> ls = new LinkedList<LinkBean>();

        NodeList list = getAllMatchingNodes(d, linkTagFinder);
        if (list != null) {
            if (list.getLength() > 0) {
                int c = 1;
                for (int i = 0; i < list.getLength(); i++) {
                    Node n = list.item(i);
                    LinkBean b = new LinkBean();
                    String href = "";
                    Node att = n.getAttributes().getNamedItem("content");
                    if (att != null) {
                        href = att.getNodeValue();
                    }
                    Node att2 = n.getAttributes().getNamedItem("src");
                    if (att2 != null) {
                        href = att2.getNodeValue();
                    }
                    Node att3 = n.getAttributes().getNamedItem("href");
                    if (att3 != null) {
                        href = att3.getNodeValue();
                    }

                    String text = "";
                    DOMElement elem = (DOMElement) n;

                    text = elem.getText();
                    text = text.trim();

                    if (text == null || text.length() == 0) {
                        text = elem.valueOf(".//text()");
                        if (text != null) {
                            text = text.trim();
                        }
                    }


                    if (text.length() == 0) {
                        Node nameAtt = n.getAttributes().getNamedItem("name");
                        if (nameAtt != null) {
                            text = n.getNodeValue();
                        }
                    }

                    if (text == null) {
                        text = "";
                    }

                    URL tUrl = null;
                    try {
                        tUrl = new URL(new URL(currentURL), href);
                        tUrl = removeFragmentFromURL(tUrl);
                        b.setURL(tUrl.toString());
                        b.setLinkText(text);
                        b.setDocIndex(c);
                        ls.add(b);
                        c++;
                    } catch (MalformedURLException e) {
                        //just keep on truckin'
                    }
                }
                logExtractLinks(ls.size());
                return ls;
            }
        }

        list = getAllMatchingNodes(d, linkAttFinder);
        if (list != null) {

            if (list.getLength() > 0) {
                int c = 1;
                for (int i = 0; i < list.getLength(); i++) {
                    Node n = list.item(i);
                    LinkBean b = new LinkBean();
                    String href = n.getNodeValue();
                    URL tUrl = null;
                    try {
                        tUrl = new URL(new URL(currentURL), href);
                        tUrl = removeFragmentFromURL(tUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    b.setURL(tUrl.toString());
                    b.setDocIndex(c);
                    ls.add(b);
                    c++;
                }
            }
            logExtractLinks(ls.size());
            return ls;
        } else {
            logExtractLinks(ls.size());
            return ls;
        }

    }
    /*

    StringWriter sw = new StringWriter();
    try {
    dom.write(sw);
    } catch (IOException ex) {
    java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    LinkedList<LinkBean> ls = new LinkedList<LinkBean>();
    try {
    XPathFactory xpf = XPathFactory.newInstance();
    XPathExpression aTags = xpf.newXPath().compile("//a");
    XPathExpression href = xpf.newXPath().compile("@href");

    try {
    NodeList nl = (NodeList)aTags.evaluate(d.getDocumentElement(), XPathConstants.NODESET);
    if (nl != null) {
    for (int i = 0; i < nl.getLength(); i++) {
    Node t = nl.item(i);
    LinkBean b = new LinkBean();

    }




    int index = 1;
    for (int i = 0; i < nl.getLength(); i++) {
    LinkBean b = new LinkBean();
    Node t = nl.item(i);
    String u = getURL(t);
    URL tUrl = null;
    try {
    tUrl = new URL(new URL(currentURL), u);
    b.setURL(tUrl.toString());
    String tex = getText(t);
    b.setLinkText(tex);
    b.setDocIndex(index);
    index++;
    ls.add(b);
    } catch (MalformedURLException ex) {
    //Logger.getLogger(MySimpleAgent.class.getName()).log(Level.FATAL, null, ex);
    }
    }
    }
    } catch (XPathExpressionException ex) {
    ex.printStackTrace();
    }
    if (ls.size() == 0) {
    }

    } catch (XPathExpressionException ex) {
    java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    logExtractLinks(ls.size());

    return ls;
     */

    private URL removeFragmentFromURL(URL urlWithFragment) {

        String protocol = urlWithFragment.getProtocol();
        String host = urlWithFragment.getHost();
        int port = urlWithFragment.getPort();
        String file = urlWithFragment.getFile();

        URL r = null;
        try {
            r = new URL(protocol, host, port, file);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            r = urlWithFragment;
        }

        return r;
    }

    private String getURL(Node a) {
        String r = "";
        String x = "@href";
        XPath xp = XPathFactory.newInstance().newXPath();
        try {
            r = xp.evaluate(x, a);
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }
        return r;
    }

    private String getText(Node b) {
        XMLTextExtractor xt = new XMLTextExtractor(b);
        String t = xt.getTextContent();

        return t;
    }

    private boolean tellParent(DownloadResult response) {
        boolean told = false;
        if (parent.hasParent()) {
            parent.tellParent(response);
            told = true;
        } else {
            told = false;

        }

        html_found = false;
        target_found = true;

        return told;
    }

    private boolean hasParent() {
        if (parent == null) {
            return false;
        } else {
            return true;
        }
    }

    public byte[] getDownloadTarget() {
        return this.downloadTarget;
    }

    public String getPage() {
        return this.page;
    }

    private static void readAndWriteStreamsFully(InputStream source, OutputStream sink) {
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

    public static void main(String[] args) {
        //LinkedList<String> lsit = new LinkedList<String>();
        //lsit.add("10198066");
        //FullTextLinkExtractor lext = new FullTextLinkExtractor(lsit);
        //LinkedList<LinkBean> list = lext.getLinks();

        //String urls = list.getFirst().getURL();

//        String[] urls = {"http://www.jbc.org/cgi/pmidlookup?view=long&pmid=1551875",
        //          "http://www.molbiolcell.org/cgi/pmidlookup?view=long&pmid=10198066",
        //        "http://www.jcb.org/cgi/pmidlookup?view=long&pmid=10908585",
        //      "http://www.pnas.org/cgi/pmidlookup?view=long&pmid=16330756",
        //    "http://www.genetics.org/cgi/pmidlookup?view=long&pmid=15020472"};


        String[] urls = {
            // "http://www.jrheum.com/subscribers/08/11/2113.html",
            //"http://carcin.oxfordjournals.org/cgi/pmidlookup?view=long&pmid=18676680",
            // "http://www.jleukbio.org/cgi/pmidlookup?view=long&pmid=18451325",
            // "http://www.informaworld.com/openurl?genre=article&doi=10.1080/14653240801927032&magic=pubmed||1B69BA326FFE69C3F0A8F227DF8201D0",
            // "http://www.retrovirology.com/content/5//32",
            // "http://meta.wkhealth.com/pt/pt-core/template-journal/lwwgateway/media/landingpage.htm?an=00006676-200803000-00005",
            // "http://jvi.asm.org/cgi/pmidlookup?view=long&pmid=18353949",
            // "http://dx.doi.org/10.1111/j.1600-6143.2007.02134.x",
            // "http://www.bloodjournal.org/cgi/pmidlookup?view=long&pmid=17522341"
            //"http://dx.doi.org/10.1186/1471-2105-9-359"
            //"http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2553348/?tool=pubmed"
            //"http://ukpmc.ac.uk/articlerender.cgi?accid=PMC2631451&tool=pmcentrez", "http://ukpmc.ac.uk/articlerender.cgi?accid=PMC2559991&tool=pmcentrez"
            //"http://linkinghub.elsevier.com/retrieve/pii/S1055-7903(09)00347-9"
            ///"http://gnode1.mib.man.ac.uk:8080/ArticleSectionClassifierWebApp/static.html"
            //"http://code.google.com/p/articlesectionclassifier/"
            //"http://www.biomedcentral.com/1752-0509/2/65"
            "?http://dx.plos.org/10.1371/journal.ppat.1000239"
        };


        //String urls[] = {"http://pubs.acs.org/doi/abs/10.1021/bi061732s"};

        System.out.println("" + "");
        for (String string : urls) {
            MySimpleAgent app = new MySimpleAgent(null, string, new PdfDownloadTarget(), true);
            app.runAgent();
            System.out.println(app.getAgentResult() + " : " + string);
            if (app.getAgentResult() == DownloadResult.TARGET_FOUND) {
                try {
                    File temp = File.createTempFile("simpleagent", ".pdf");
                    try {
                        FileOutputStream fos = new FileOutputStream(temp);
                        MySimpleAgent.readAndWriteStreamsFully(new ByteArrayInputStream(app.getDownloadTarget()), fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException f) {
                        f.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String os = null;
                    try {
                        os = System.getProperty("os.name");
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    //String[] cmd = null;
                    if (os.contains("Win")) {
                        String[] cmd = {"rundll32.exe", "url.dll,FileProtocolHandler", temp.toString()};
                        Process p = Runtime.getRuntime().exec(cmd);
                    } else if (os.contains("Mac")) {
                        String[] cmd = {"open", temp.toString()};
                        Process p = Runtime.getRuntime().exec(cmd);
                    } else {
                        System.out.println(temp.toString());
                    }

                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(MySimpleAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        }

        System.exit(0);
    }
}
