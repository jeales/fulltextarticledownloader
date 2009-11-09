/*
 * XMLTextExtractor.java
 *
 * Created on 08 October 2007, 00:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.util;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 * @author James Eales
 */
public class XMLTextExtractor {
    
    private boolean spaces;
    private Node theNode;
    private boolean extractionDone;
    private String textContent;
    
    public boolean isSpaces() {
        return spaces;
    }
    
    public Node getTheNode() {
        return theNode;
    }
    
    public void setSpaces(boolean spaces) {
        this.spaces = spaces;
    }
    
    public void setTheNode(Node theNode) {
        this.theNode = theNode;
    }
    
    /** Creates a new instance of XMLTextExtractor */
    public XMLTextExtractor(Node node) {
        setTheNode(node);
        setSpaces(true);
        extractionDone = false;
    }
    
    public XMLTextExtractor() {
        setSpaces(true);
        extractionDone = false;
    }
    
    public XMLTextExtractor(Node node, boolean insertSpaces) {
        setTheNode(node);
        setSpaces(insertSpaces);
        extractionDone = false;
    }
    
    public String getTextContent() {
        if (extractionDone) {
            return textContent;
        } else {
            if (getTheNode() != null) {
                textContent = extractTextContent(getTheNode());
                extractionDone = true;
                return textContent;
            } else {
                throw new NullPointerException("You have not supplied the XMLTextExtractor object with a node to extract text from");
            }
        }
    }
    
    
    private String extractTextContent(Node n) {
        StringBuilder buff = new StringBuilder();
        
        //check if top level is text and print if it is
        if (n.getNodeType() == Node.TEXT_NODE ) {
            if (!isThisWhiteSpace((Text)n)) {
                addText(buff,((Text)n).getNodeValue(),spaces);
            }
        }
        
        //do children of n
        Node child = n.getFirstChild();
        
        //iterate through children
        while (child != null) {
            if (child.getNodeType() == Node.TEXT_NODE ) {
                if (!isThisWhiteSpace((Text)child)) {
                    addText(buff,((Text)child).getNodeValue(),spaces);
                }
            }
            if (child.hasChildNodes()) {
                extractTextContent(child,buff);
            }
            child = child.getNextSibling();
        }
        
        return buff.toString();
    }
    
    private void extractTextContent(Node n, StringBuilder b) {
        //check n first
        if (n.getNodeType() == Node.TEXT_NODE ) {
            if (!isThisWhiteSpace((Text)n)) {
                addText(b,((Text)n).getNodeValue(),spaces);
            }
        }
        
        //then do children
        Node child = n.getFirstChild();
        
        while (child != null) {
            if (child.getNodeType() == Node.TEXT_NODE ) {
                if (!isThisWhiteSpace((Text)child)) {
                    addText(b,((Text)child).getNodeValue(),spaces);
                }
            }
            if (child.hasChildNodes()) {
                extractTextContent(child,b);
            }
            child = child.getNextSibling();
        }
        
    }
    
    private void addText(StringBuilder b, String text, boolean do_space) {
        b.append(text);
        if (do_space) {
            b.append(" ");
        }
        
    }
    
    private boolean isThisWhiteSpace(Text n) {
        boolean answer = false;
        
        String text = n.getNodeValue();
        text = text.trim();
        if (text.equals("")) {
            answer = true;
        }
        
        
        return answer;
    }
}
