package cz.muni.physics.pdr.backend.resolver.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class for reading input stream
 * @author Michal Krajčovič
 * @version 1.0
 * @since 05/04/16
 */
public class StreamGobbler<T> implements Supplier<List<T>> {
    private InputStream is;
    private Function<String, T> lineProcessor;
    private boolean sout = false;

    /**
     * Creates StreamGobbler with given InputStream without mapping function.
     * @param is
     */
    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    /**
     * Creates StreamGobbler with given InputStream and mapping function
     * @param is
     * @param lineProcessor mapping function for mapping line to another object from stream
     */
    public StreamGobbler(InputStream is, Function<String, T> lineProcessor) {
        this.is = is;
        this.lineProcessor = lineProcessor;
    }

    /**
     * Reads inputStream and maps every line to object T, which is added to resulting list.
     * If no mapping function was specified, every line is written on standard output using System.out.println
     * If result from mapping function is null, line is written on standard output
     * @return resulting list of mapped objects
     */
    @Override
    public List<T> get() {
        List<T> lines = new ArrayList<>();
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                T obj;
                if (lineProcessor != null && (obj = lineProcessor.apply(line)) != null) {
                    lines.add(obj);
                } else if (sout) {
                    System.out.println(line);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return lines;
    }

    public Function<String, T> getLineProcessor() {
        return lineProcessor;
    }

    /**
     * Set mapping function for every line -> T
     * @param lineProcessor
     */
    public void setLineProcessor(Function<String, T> lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public InputStream getIs() {
        return is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public boolean isSout() {
        return sout;
    }

    /**
     * Sets if lines should be written on standard output if mapping function is not available
     * @param sout
     */
    public void setSout(boolean sout) {
        this.sout = sout;
    }
}
