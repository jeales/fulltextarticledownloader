/*
 * TermSet.java
 *
 * Created on 14 March 2007, 09:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author James Eales
 */
public class PubMedTermSet {
    
    private LinkedList<String> terms;
    private boolean literal;
    
    /** Creates a new instance of TermSet */
    public PubMedTermSet() {
        terms = new LinkedList<String>();
    }
    
    /**
     * Accepts a <code>String</code> that either contains a single term
     * or is a space character delimited concatenation of terms.
     * e.g. <br><code>String term1 = "phylogenetic";<br>
     * String term2 = "phylogenetic phylogenomic workflow"<br>
     * PubMedTermSet tSet1 = new PubMedTermSet(term1);<br>
     * PubMedTermSet tSet2 = new PubMedTermSet(term2);<br>
     * result of tSet1.getNumberOfTerms(); is 1<br>
     * result of tSet2.getNumberOfTerms(); is 3<br>
     * result of tSet1.getTermString(); is "phylogenetic"<br>
     * result of tSet2.getTermString(); is "phylogenetic+phylogenomic+workflow"</code>
     * <br>
     * @param termString <code>String</code> to put into the termset list
     */
    public PubMedTermSet(String termString) {
        terms = new LinkedList<String>();
        termString = termString.trim();
        String[] ar = termString.split(" ");
        if (ar.length > 1) {
            for (int i = 0; i < ar.length; i++) {
                terms.add(ar[i]);
            }
        } else {
            terms.add(ar[0]);
        }
    }
    
    /**
     * Accepts a <code>List<String></code> that contains
     * any number of term <code>String</code>s.
     * <br>
     * @param listOfTerms <code>List<String></code> containing 1 or more <code>String</code> terms.
     */
    public PubMedTermSet(List<String> listOfTerms) {
        terms = new LinkedList<String>();
        for (String elem : listOfTerms) {
            terms.add(elem);
        }
    }
    
    public void setLiteralSearch(boolean literalSearch) {
        literal = literalSearch;
    }
    
    public boolean isLiteralSearch() {
        return literal;
    }
    
    public void addTerm(String term) {
        terms.add(term);
    }
    
    public String getTermString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        if (literal) {
            sb.append("%22");
        }
        for (String elem : terms) {
            if (first) {
                sb.append(elem);
                first = false;
            } else {
                sb.append("+"+elem);
            }
        }
        if (literal) {
            sb.append("%22");
        }
        return sb.toString();
    }
    /**
     * Overidden <code>java.lang.Object</code> method.<br>
     * returns the same as <code>getTermString();</code>
     *
     */
    public String toString() {
        return getTermString();
    }
    
    public int getNumberOfTerms() {
        return terms.size();
    }
    
}
