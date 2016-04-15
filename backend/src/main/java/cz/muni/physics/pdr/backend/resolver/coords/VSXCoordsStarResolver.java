package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.resolver.StarCoordinates;
import cz.muni.physics.pdr.backend.resolver.VSXResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
@Component
public class VSXCoordsStarResolver extends VSXResolver<StarCoordinates> {

    @Autowired
    public VSXCoordsStarResolver(@Value("${user.home}${app.data.dir.path}") String dataDir,
                                 @Value("${vsx.dat.file.name}") String vsxDatFile) {
        super(dataDir, vsxDatFile, (result, coords) -> true); // todo check coords
    }
}
