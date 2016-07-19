package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.entity.VizierQuery;

/**
 * @author Michal
 * @version 1.0
 * @since 7/18/2016
 */
public class VizierMachoStarResolver extends VizierResolverTsvImpl {


    public VizierMachoStarResolver() {
        super("http://vizier.u-strasbg.fr/viz-bin/asu-tsv", "II/247/machovar");
    }


    public static void main(String[] args) {
        VizierResolverTsvImpl machoResolver = new VizierMachoStarResolver();
        machoResolver.findByQuery(new VizierQuery(""));
    }
}
