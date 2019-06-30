package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.CosmicCoordinates;

import java.util.List;
import java.util.Optional;

public interface VsxResolver {
    Optional<VariableStarInformationModel> findByName(String name);
    List<DistanceModel<VariableStarInformationModel>> findByCoords(CosmicCoordinates coordinates, double radiusDegrees);
}
