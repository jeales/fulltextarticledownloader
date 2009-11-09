/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fulltextarticledownloader.downloadtarget;

import fulltextarticledownloader.pdfToText.PdfToTextAutomator;

/**
 *
 * @author James Eales
 */
public class PdfDownloadTarget implements DownloadTarget {

    private static byte[] PDF_FIRST_BYTES = {37, 80, 68, 70, 45, 49, 46};
    private String textToFind;
    private boolean checkTextContent;

    public PdfDownloadTarget(String textToFind) {
        if (textToFind != null) {
            this.textToFind = textToFind;
            checkTextContent = true;
        } else {
            checkTextContent = false;
        }
    }

    public PdfDownloadTarget() {
        this(null);
    }

    private String standardiseString(String toStandardise) {
        String r = toStandardise;
        //all to lower case
        r = r.toLowerCase();
        //remove all non a-z or 0-9 chars
        r = r.replaceAll("[^a-z^0-9]", "");
        //return a standardised string to match
        return r;
    }

    @Override
    public boolean isDownloadTarget(byte[] blob, String url) {
        boolean r = false;

        if (isBinaryIdentical(blob, url)) {
            if (checkTextContent) {
                PdfToTextAutomator auto = new PdfToTextAutomator();
                String articleText = auto.doTextExtraction(blob);
                if (articleText != null) {
                    String toLookIn = articleText.substring(0, articleText.length() / 2);
                    toLookIn = standardiseString(toLookIn);
                    String toFind = standardiseString(textToFind);
                    if (toLookIn.contains(toFind)) {
                        r = true;
                    }
                } else {
                    //we are not checking whether the article text matches
                    r = true;
                }
            } else {
                r = true;
            }
        }

        return r;
    }

    private boolean isBinaryIdentical(byte[] blob, String url) {
        //default to true but can be false and returned if one byte does
        //not match PDF_FIRST_BYTES
        boolean r = true;

        if (blob.length >= PDF_FIRST_BYTES.length) {
            for (int i = 0; i < PDF_FIRST_BYTES.length; i++) {
                if (PDF_FIRST_BYTES[i] != blob[i]) {
                    r = false;
                    break;
                }
            }
        }

        return r;
    }

    public static void main(String[] args) {
        PdfDownloadTarget t = new PdfDownloadTarget("UNIQUESTRING");
        System.out.println(t.isDownloadTarget(PDF_FIRST_BYTES, "http://"));
        System.out.println(t.isDownloadTarget(PDF_FIRST_BYTES, "http://"));
    }
}
