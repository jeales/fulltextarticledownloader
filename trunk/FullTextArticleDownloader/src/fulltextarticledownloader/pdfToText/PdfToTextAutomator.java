/*
 * PdfToTextAutomator.java
 *
 * Created on 29 July 2007, 01:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fulltextarticledownloader.pdfToText;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James Eales
 */
public class PdfToTextAutomator {

    private Random ran;
    private DecimalFormat df;
    private int fileblobsize = 1024 * 10;
    //private static PdfToTextAutomator singleton;
    private String os = System.getProperty("os.name");

    /** Creates a new instance of PdfToTextAutomator */
    public PdfToTextAutomator() {
        ran = new Random();
        df = new DecimalFormat("00000000000");
    }

    public String doTextExtraction(byte[] pdfBlob) {
        String prefix = getNextPrefix();

        ByteArrayInputStream bais = new ByteArrayInputStream(pdfBlob);

        File pdfFile = saveTempPDF(prefix, bais);
        File textFile = executeCommand(pdfFile, prefix);
        String text = readTextFileAsString(textFile, prefix);

        pdfFile.delete();
        textFile.delete();

        return text;
    }

    private String readTextFileAsString(File textFile, String prefix) {
        StringBuilder s = new StringBuilder();

        try {
            FileReader fr = new FileReader(textFile);
            char[] car = new char[1024];
            int c = 0;
            while ((c = fr.read(car)) != -1) {
                s.append(car, 0, c);
            }
            fr.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String t = s.toString();

        return t;
    }

    private File saveTempPDF(String prefix, InputStream pdfstream) {

        File f = getTempFile(prefix, ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buf = new byte[fileblobsize];
            int bytesRead;
            while ((bytesRead = pdfstream.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }

            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return f;


    }

    private synchronized File getTempFile(String prefix, String suffix) {
        File r = null;
        try {
            r = File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            Logger.getLogger(PdfToTextAutomator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    private File executeCommand(File pdf, String prefix) {
        File textFile = getTempFile(prefix, ".txt");
        Runtime r = Runtime.getRuntime();
        /*,"-q",Main.temp_dir+prefix+".pdf",Main.temp_dir+prefix+".txt"*/
        String[] rs = {"", "-q", pdf.toString(), textFile.toString()};
        if (os.startsWith("Windows")) {
            rs[0] = "pdftotext.exe";
        } else {
            rs[0] = "./pdftotext";
        }

        File processStdOut = null;
        File processStdErr = null;

        processStdOut = getTempFile(prefix, ".out");
        processStdErr = getTempFile(prefix, ".err");

        ProcessBuilder pb = new ProcessBuilder(rs);
        //String[] rs = {"pwd"};
        try {
            //pb.directory(new File());
            Process p = r.exec(rs);
            OutputHandler handler = new OutputHandler(p.getInputStream(), new PrintStream(processStdOut));
            handler.start();
            OutputHandler handler2 = new OutputHandler(p.getErrorStream(), new PrintStream(processStdErr));
            handler2.start();
            p.waitFor();
            //System.out.println(p.exitValue());
            p.destroy();
            handler.interrupt();
            handler2.interrupt();
            processStdOut.delete();
            processStdErr.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("Couldn't find pdftotext, it needs to go in: " + pb.environment().get("PATH"));
            ex.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return textFile;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    private synchronized String getNextPrefix() {
        int i = ran.nextInt(1000000000);
        String s = df.format((long) i);
        return s;
    }
}

class OutputHandler extends Thread {

    InputStream is;
    PrintStream ps;
    static int THREADCOUNT;
    static long DEFAULT_TIMEOUT = 2000000;
    boolean done_write = false;

    //CheckTimeout timer;
    //Thread thisThread;
    public OutputHandler(InputStream istream, PrintStream pstream) {
        is = istream;
        ps = pstream;
    }

    @Override
    public void run() {

        try {
            BufferedInputStream isr = new BufferedInputStream(is);

            String line = null;
            int i;
            while ((i = isr.read()) != -1) {
                //System.out.println("Reading");
                done_write = true;
                //System.out.print((char)i);
                ps.print((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        ps.close();
        try {
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
