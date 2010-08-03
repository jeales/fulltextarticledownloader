/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.agents;

import fulltextarticledownloader.downloadtarget.PdfDownloadTarget;

/**
 *
 * @author James Eales
 */
public class PdfAgent {

    private String url;
    private MySimpleAgent agent;

    public PdfAgent(String url) {
        this.url = url;
        agent = new MySimpleAgent(null, url, new PdfDownloadTarget());
    }

    public void runAgent() {
        agent.runAgent();
    }

    public DownloadResult getTypeSaved() {
        return agent.getAgentResult();
    }

    public byte[] getPdf() {
        return agent.getDownloadTarget();
    }

    public String getFinalURL() {
        return agent.getResolvedUrl();
    }
}
