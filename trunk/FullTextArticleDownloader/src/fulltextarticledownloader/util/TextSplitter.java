/*
 * TextSplitter.java
 *
 * Created on 06 October 2007, 11:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.util;

import java.util.LinkedList;

/**
 *
 * @author James Eales
 */
public class TextSplitter {
    
    private LinkedList<String> textBits;
    private String regexToSplitAround;
    private String textToSplit;
    private static String defaultRegex = "(\r\n\r\n)(\\p{Upper})";
    
    public String getRegexToSplitAround() {
        return regexToSplitAround;
    }
    
    public void setRegexToSplitAround(String regexToSplitAround) {
        this.regexToSplitAround = regexToSplitAround;
    }
    
    public LinkedList<String> getTextBits() {
        return textBits;
    }
    
    public void setTextBits(LinkedList<String> textBits) {
        this.textBits = textBits;
    }
    
    public String getTextToSplit() {
        return textToSplit;
    }
    
    public void setTextToSplit(String textToSplit) {
        this.textToSplit = textToSplit;
    }
    
    
    
    /** Creates a new instance of TextSplitter it will split the text using its default regex <code>(\r\n\r\n)(\\p{Upper})</code>*/
    public TextSplitter(String textToSplit) {
        setTextToSplit(textToSplit);
        setRegexToSplitAround(this.defaultRegex);
        textBits = new LinkedList<String>();
        doSplitting();
    }
    
    /** Creates a new instance of TextSplitter it will split the text using the user supplied regex*/
    public TextSplitter(String textToSplit, String regexToSplitWith) {
        setTextToSplit(textToSplit);
        setRegexToSplitAround(regexToSplitWith);
        textBits = new LinkedList<String>();
        doSplitting();
    }
    
    private void doSplitting() {
        String temp = getTextToSplit().replaceAll(getRegexToSplitAround(),"<section>$2");
        String a[] = temp.split("<section>");
        for (int i = 0; i < a.length; i++) {
            textBits.add(new String(a[i]));
        }
    }
    
}
