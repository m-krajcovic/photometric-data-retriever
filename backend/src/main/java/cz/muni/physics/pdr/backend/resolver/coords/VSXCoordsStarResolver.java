package cz.muni.physics.pdr.backend.resolver.coords;

import cz.muni.physics.pdr.backend.entity.CelestialCoordinates;
import cz.muni.physics.pdr.backend.resolver.StarResolver;
import cz.muni.physics.pdr.backend.entity.StellarObject;
import cz.muni.physics.pdr.backend.resolver.VSXResolver;
import cz.muni.physics.pdr.backend.utils.AppConfig;
import cz.muni.physics.pdr.backend.utils.AstroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 13/04/16
 */
@Component
public class VSXCoordsStarResolver extends VSXResolver<CelestialCoordinates> {

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

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        StarResolver<CelestialCoordinates> resolver = context.getBean(VSXCoordsStarResolver.class);

        List<StellarObject> results = resolver.getResults(new CelestialCoordinates(118.77167, +22.00139, 0.5));
        results.forEach(System.out::println);
        System.out.println(results.size());
    }
}
