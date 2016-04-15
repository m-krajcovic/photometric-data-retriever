package cz.muni.physics.pdr.backend.resolver.name;

import cz.muni.physics.pdr.backend.resolver.StarName;
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
public class VSXNameResolver extends VSXResolver<StarName> {

    @Autowired
    public VSXNameResolver(@Value("${user.home}${app.data.dir.path}") String dataDir,
                           @Value("${vsx.dat.file.name}") String vsxDatFile) {
        super(dataDir, vsxDatFile, (result, name) -> result.getNames().stream().anyMatch(n -> n.equalsIgnoreCase(name.getValue())));
    }
}
