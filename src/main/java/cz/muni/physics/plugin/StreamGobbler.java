package cz.muni.physics.plugin;

import java.io.*;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 05/04/16
 */
public class StreamGobbler extends Thread {
    private InputStream is;
    private LineProcessor lineProcessor;
    private Callback finished;

    public StreamGobbler(String name, InputStream is, LineProcessor lineProcessor) {
        super(name);
        this.is = is;
        this.lineProcessor = lineProcessor;
    }

    public StreamGobbler(InputStream is) {
        this(is, null);
    }

    public StreamGobbler(InputStream is, LineProcessor lineProcessor) {
        this.is = is;
        this.lineProcessor = lineProcessor;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (lineProcessor == null) {
                    System.out.println(line);
                } else {
                    lineProcessor.call(line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (finished != null) finished.call();
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public LineProcessor getLineProcessor() {
        return lineProcessor;
    }

    public void setLineProcessor(LineProcessor lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public Callback getFinished() {
        return finished;
    }

    public void setFinished(Callback finished) {
        this.finished = finished;
    }

    public interface LineProcessor {
        void call(String line);
    }

    public interface Callback {
        void call();
    }
}
