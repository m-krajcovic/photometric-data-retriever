package cz.muni.physics.pdr.backend.resolver;

import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.plugin.StreamGobbler;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
    private BiPredicate<StellarObject, T> condition;

    protected VSXResolver(String dataDir, String vsxDatFile, BiPredicate<StellarObject, T> condition) {
        this.dataDir = dataDir;
        this.vsxDatFile = vsxDatFile;
        this.condition = condition;
    }

    @Override
    public List<StellarObject> getResults(T param) {
        try (FileInputStream fis = new FileInputStream(new File(dataDir, vsxDatFile))) {
            StreamGobbler<StellarObject> gobbler = new StreamGobbler<>(fis,
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
                        StellarObject result = new StellarObject();
                        result.getIds().put("vsx", oid);
                        result.getNames().add(name);
                        result.setDeclination(Double.parseDouble(DEdeg));
                        result.setRightAscension(Double.parseDouble(RAdeg));
                        if (!StringUtils.isBlank(epoch)) {
                            result.setEpoch(Double.parseDouble(epoch));
                        }
                        if (!StringUtils.isBlank(period)) {
                            result.setPeriod(Double.parseDouble(period));
                        }
                        if (condition.test(result, param)) {
                            return result;
                        }
                        return null;
                    });
            return gobbler.get();
        } catch (FileNotFoundException e) {
            e.printStackTrace(); //todo
        } catch (IOException e) {
            e.printStackTrace(); //todo
        }
        return new ArrayList<>();
    }

    @Override
    public StellarObject getResult(T param) {
        List<StellarObject> results = getResults(param);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean isAvailable() {
        return new File(dataDir, vsxDatFile).exists();
    }
}
