package cz.muni.physics.pdr.backend.resolver.vizier;

/**
 * Created by Michal on 28-Apr-16.
 */
public class VizierVSXStarResolver extends VizierResolverTsvImpl {

    public VizierVSXStarResolver() {
        super("https://vizier.u-strasbg.fr/viz-bin/asu-tsv", "B/vsx/vsx");
    }
}
