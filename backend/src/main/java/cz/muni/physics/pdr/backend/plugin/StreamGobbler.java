package cz.muni.physics.pdr.backend.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 05/04/16
 */
public class StreamGobbler<T> implements Supplier<List<T>> {
    private InputStream is;
    private Function<String, T> lineProcessor;

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    public StreamGobbler(InputStream is, Function<String, T> lineProcessor) {
        this.is = is;
        this.lineProcessor = lineProcessor;
    }

    @Override
    public List<T> get() {
        List<T> lines = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                T obj;
                if (lineProcessor != null && (obj = lineProcessor.apply(line)) != null) {
                    lines.add(obj);
                } else {
                    System.out.println(line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return lines;
    }

    public Function<String, T> getLineProcessor() {
        return lineProcessor;
    }

    public void setLineProcessor(Function<String, T> lineProcessor) {
        this.lineProcessor = lineProcessor;
    }
}
