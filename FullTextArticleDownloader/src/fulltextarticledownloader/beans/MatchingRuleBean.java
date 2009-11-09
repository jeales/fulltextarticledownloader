/*
 * MatchingRuleBean.java
 *
 * Created on June 2, 2007, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

/**
 *
 * @author jameseales
 */
public class MatchingRuleBean {
    
    private String pattern;
    private Double weight;
    private String name;
    private String field;
    private String polarity;

    public String getPolarity() {
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }
    


    public String getField() {
        return field;
    }

    public String getName() {
        return name;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    
    /** Creates a new instance of MatchingRuleBean */
    public MatchingRuleBean() {
        pattern = "";
        weight = 0.0d;
        field = "";
        name = "";
    }

    public String getPattern() {
        return pattern;
    }

    public Double getWeight() {
        return weight;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
       
}
