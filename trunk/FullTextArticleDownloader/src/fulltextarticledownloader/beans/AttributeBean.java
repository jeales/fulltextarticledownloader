/*
 * AttributeBean.java
 *
 * Created on 10 March 2007, 17:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fulltextarticledownloader.beans;

/**
 *
 * @author James Eales
 */
public class AttributeBean {
    
    private String name;
    private String value;
    
    /** Creates a new instance of AttributeBean */
    public AttributeBean() {
        name = "";
        value = "";
    }
    
    public AttributeBean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
  
}
