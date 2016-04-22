package cz.muni.physics.pdr.backend.resolver.vsx;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.resolver.plugin.StreamGobbler;
import cz.muni.physics.pdr.backend.utils.AstroUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
public class VSXStarResolverImpl implements VSXStarResolver {

    private String vsxDatFilePath;

    public VSXStarResolverImpl(String vsxDatFilePath) {
        this.vsxDatFilePath = vsxDatFilePath;
    }

    @Override
    public StellarObject findByName(String name) {
        return getResults(obj -> obj.getNames().get(0).equalsIgnoreCase(name)).get(0);
    }

    @Override
    public List<StellarObject> findByCoordinates(CelestialCoordinates coords) {
        return getResults(obj -> {
            double distance = AstroUtils.distance(obj.getRightAscension(), obj.getDeclination(), coords.getRightAscension(), coords.getDeclination());
            obj.setDistance(distance);
            return coords.getRadius() >= distance;
        });
    }

    private List<StellarObject> getResults(Predicate<StellarObject> condition) {
        try (FileInputStream fis = new FileInputStream(new File(vsxDatFilePath))) {
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
                        if (condition.test(result)) {
                            return result;
                        }
                        return null;
                    });
            return gobbler.get();
        } catch (IOException e) {
            throw new RuntimeException("VSX dat file is not available", e);
        }
    }

    @Override
    public boolean isAvailable() {
        return new File(vsxDatFilePath).exists();
    }
}
