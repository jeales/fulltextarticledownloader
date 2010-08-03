/*
 * LinkAnalyser.java
 *
 * Created on June 2, 2007, 3:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fulltextarticledownloader.linkanalysis;

import fulltextarticledownloader.beans.LinkBean;
import fulltextarticledownloader.beans.MatchingRuleBean;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jameseales
 */
public class LinkAnalyser {

    private LinkedList<MatchingRuleBean> rules;
    private boolean debuggingOutput = false;
    private static final String RULES_FILE = "fulltextarticledownloader/files/rules.xml";

    public void setDebuggingOutput(boolean debuggingOutput) {
        this.debuggingOutput = debuggingOutput;
    }

    /** Creates a new instance of LinkAnalyser */
    public LinkAnalyser() {
        createRules();
    }

    private void createRules() {
        rules = new LinkedList<MatchingRuleBean>();

        XPath x = XPathFactory.newInstance().newXPath();
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ClassLoader.getSystemResource(RULES_FILE).toURI().toString());
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        //InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream("fulltextarticledownloader/files/rules.xml"));
        try {
            NodeList nl = (NodeList) x.evaluate("//rule", doc, XPathConstants.NODESET);
            Node n = null;
            //System.out.println(nl.getLength());
            for (int i = 0; i < nl.getLength(); i++) {
                n = nl.item(i);
                MatchingRuleBean b = new MatchingRuleBean();
                NamedNodeMap nnm = n.getAttributes();
                String name = nnm.getNamedItem("id").getNodeValue();
                String field = nnm.getNamedItem("field").getNodeValue();
                String pattern = nnm.getNamedItem("pattern").getNodeValue();
                Double weight = Double.parseDouble(nnm.getNamedItem("weight").getNodeValue());
                String polarity = nnm.getNamedItem("rulepolarity").getNodeValue();
                b.setName(name);
                b.setField(field);
                b.setPattern(pattern);
                b.setWeight(weight);
                b.setPolarity(polarity);
                rules.add(b);
            }
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }
        //System.out.println("");
    }

    public double analyseLink(LinkBean toAnalyse) {
        double r = 0.0d;

        String lt = toAnalyse.getLinkText();
        String url = toAnalyse.getURL();

        double perRule = 1.0 / (double) rules.size();

        for (MatchingRuleBean elem : rules) {
            boolean match = false;
            Pattern p = Pattern.compile(elem.getPattern(), Pattern.CASE_INSENSITIVE);
            Matcher m = null;
            String t = null;
            if (elem.getField().equals("linktext")) {
                t = lt;
            } else {
                t = url;
            }

            if (!t.equals("")) {
                m = p.matcher(t);
                if (m.find()) {
                    match = true;
                }
            }



            double d = 0.0d;
            if (match) {
                if (debuggingOutput) {
                    System.out.println(p.toString());
                }
                d = perRule * elem.getWeight();
            }
            if (elem.getPolarity().equals("negative")) {
                d -= d * 2;
            }

            r += d;
        }

        //System.out.println("");

        return r;
    }

    public static void main(String[] args) {
        LinkAnalyser app = new LinkAnalyser();
        System.out.println("");
    }
}
