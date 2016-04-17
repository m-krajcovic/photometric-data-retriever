package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.resolver.VSXResolver;
import cz.muni.physics.pdr.backend.utils.AstroUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
@Component
public class VSXCoordsStarResolver extends VSXResolver<CelestialCoordinates> {

    private final static Logger logger = LogManager.getLogger(VSXCoordsStarResolver.class);

    @Autowired
    public VSXCoordsStarResolver(@Value("${user.home}${app.data.dir.path}") String dataDir,
                                 @Value("${vsx.dat.file.name}") String vsxDatFile) {
        super(dataDir, vsxDatFile, (result, coords) -> {
            double distance = AstroUtils.distance(result.getRightAscension(), result.getDeclination(), coords.getRightAscension(), coords.getDeclination());
            result.setDistance(distance);
            return coords.getRadius() >= distance;
        });
    }

    @Override
    public StellarObject getResult(CelestialCoordinates param) {
        return super.getResult(param); // todo return closest result
    }
}
