/*
 * LinkDataObject.java
 *
 * Created on 30 January 2007, 12:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

import fulltextarticledownloader.linkanalysis.LinkAnalyser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author James
 */
public class LinkBean implements Comparable {
    
    private String URL;
    private String linkText;
    private LinkedList<AttributeBean> attributes;
    private String pmid;
    private double probability;
    private boolean prob_done = false;
    private int docIndex;
    private static LinkAnalyser analyser;

    static {
        analyser = new LinkAnalyser();
    }
    
    public int getDocIndex() {
        return docIndex;
    }
    
    public void setDocIndex(int docIndex) {
        this.docIndex = docIndex;
    }
    
    public double getProbability() {
        if (!prob_done) {
            doProbability();
        }
        return probability;
    }
    
    public void setProbability(double probability) {
        this.probability = probability;
    }
    
    private void doProbability() {
        
        this.probability = analyser.analyseLink(this);
        double index_prob = 0.1 / (double)this.docIndex;
        this.probability += index_prob;
        this.prob_done = true;
        
        /*
        Pattern p = Pattern.compile("pdf",Pattern.CASE_INSENSITIVE);
        Pattern p2 = Pattern.compile("(fulltext)|(full text)",Pattern.CASE_INSENSITIVE);
        Matcher m = null;
        Matcher m2 = null;
        
        double t = 0.0;
        if (!URL.equals("")) {
            m = p.matcher(URL);
            if (m.find()) {
                t += 0.5;
            }
        }
        if (!linkText.equals("")) {
            m.reset(linkText);
            if (m.find()) {
                t+=0.5;
            }
        }   
        
        if (!URL.equals("")) {
            m2 = p2.matcher(URL);
            if (m2.find()) {
                t += 0.5;
            }
        }
        if (!linkText.equals("")) {
            m2.reset(linkText);
            if (m2.find()) {
                t+=0.5;
            }
        }
        
        if (!linkText.equals("")) {
            if (linkText.contains("nav")) {
                t-=0.5;
            }
        }
        
        double index_prob = 0.25 / (double)this.docIndex;
        t+= index_prob;
        
        probability = t;
        prob_done = true;
         */
        
        
    }
    
    public String getPmid() {
        return pmid;
    }
    
    public void setPmid(String pmid) {
        this.pmid = pmid;
    }
    
    public String getLinkText() {
        return linkText;
    }
    
    public String getURL() {
        return URL;
    }
    
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
    
    public void setURL(String URL) {
        this.URL = URL;
    }
    
    @Override
    public String toString() {
        return "LINK: " + URL + " LINK TEXT: " + linkText + " PROBABILITY: " + getProbability();
    }
    
    public LinkedList<AttributeBean> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(LinkedList<AttributeBean> attributes) {
        this.attributes = attributes;
    }
    
    public URL toURL() {
        URL t = null;
        try {
            t = new URL(this.URL);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return t;
    }
    
    public int compareTo(Object o) {
        int r = 0;
        
        if (o instanceof LinkBean) {
            LinkBean ob = (LinkBean)o;
            double other = ob.getProbability();
            double here = this.getProbability();
            
            if ( other > here) {
                r = 1;
            } else if (other < here) {
                r = -1;
            } else {
                r = 0;
            }
        }
        
        return r;
    }
    
    
    /** Creates a new instance of LinkDataObject */
    public LinkBean(String url, String text, LinkedList<AttributeBean> atts) {
        URL = url;
        linkText = text;
        attributes = atts;
        pmid = "";
        probability = 0.0;
    }
    
    public LinkBean(String url , String linktext) {
        URL = url;
        linkText = linktext;
        attributes = new LinkedList<AttributeBean>();
        pmid = "";
        probability = 0.0;
    }
    
    public LinkBean() {
        URL = "";
        linkText = "";
        attributes = new LinkedList<AttributeBean>();
        pmid = "";
        probability = 0.0;
    }
    
    public LinkBean(String url, String text, LinkedList<AttributeBean> atts, String pmid) {
        URL = url;
        linkText = text;
        attributes = atts;
        this.pmid = pmid;
        probability = 0.0;
    }
    
    public LinkBean(String pmid) {
        this.pmid = pmid;
        attributes = new LinkedList<AttributeBean>();
        URL = "";
        linkText = "";
    }    
}
