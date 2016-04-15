package cz.muni.physics.pdr.backend.resolver;

import cz.muni.physics.pdr.backend.plugin.StreamGobbler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 15/04/16
 */
public abstract class VSXResolver<T> implements StarResolver<T> {

    private String dataDir;
    private String vsxDatFile;
    private BiPredicate<StarResolverResult, T> condition;

    protected VSXResolver(String dataDir, String vsxDatFile, BiPredicate<StarResolverResult, T> condition) {
        this.dataDir = dataDir;
        this.vsxDatFile = vsxDatFile;
        this.condition = condition;
    }

    @Override
    public List<StarResolverResult> getResults(T param) {
        try (FileInputStream fis = new FileInputStream(new File(dataDir, vsxDatFile))) {
            StreamGobbler<StarResolverResult> gobbler = new StreamGobbler<>(fis,
                    s -> {
                        String oid = s.substring(0, 6).trim();
                        String name = s.substring(8, 37).trim();
                        String RAdeg = s.substring(41, 49).trim();
                        String DEdeg = s.substring(51, 59).trim();
                        String epoch = null;
                        String period = null;
                        if (s.length() >= 124) {
                            epoch = s.substring(124, s.length() >= 135 ? 135 : s.length()).trim();
                            if (s.length() >= 139) {
                                period = s.substring(139, s.length() >= 154 ? 154 : s.length()).trim();
                            }
                        }
                        StarResolverResult result = new StarResolverResult();
                        result.setNames(Arrays.asList(name));
                        result.setJdedeg(DEdeg);
                        result.setJraddeg(RAdeg);
                        result.setEpoch(epoch);
                        result.setPeriod(period);
                        if (condition.test(result, param)) {
                            return result;
                        }
                        return null;
                    });
            return gobbler.get();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public StarResolverResult getResult(T param) {
        List<StarResolverResult> results = getResults(param);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean isAvailable() {
        return new File(dataDir, vsxDatFile).exists();
    }
}
