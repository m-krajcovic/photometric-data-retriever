package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.CosmicCoordinates;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class VsxResolverVizierTapImpl implements VsxResolver {

    private static final String VSX_CAT = "\"B/vsx/vsx\"";
    private static final String QUERY_FIELDS = MessageFormat.format("{0}.OID, {0}.Name, {0}.Type, {0}.Epoch, {0}.Period, {0}.RAJ2000, {0}.DEJ2000", VSX_CAT);

    private TAPVizierService tapVizierService;

    public VsxResolverVizierTapImpl(TAPVizierService tapVizierService) {
        this.tapVizierService = tapVizierService;
    }

    @Override
    public Optional<VariableStarInformationModel> findByName(String name) {
        String query = MessageFormat.format("SELECT TOP 1 {0} FROM {1} WHERE {1}.Name LIKE ''{2}''", QUERY_FIELDS, VSX_CAT, name.replace("'", "''"));
        TAPVizierService.TAPVizierResult tapResult = tapVizierService.query(query);
        if (!tapResult.getData().isEmpty() && tapResult.getData().get(0).size() == 7) {
            return Optional.of(getSingleModel(tapResult.getData().get(0)));
        }
        return Optional.empty();
    }

    @Override
    public List<DistanceModel<VariableStarInformationModel>> findByCoords(CosmicCoordinates coordinates, double radiusDegrees) {
        TAPVizierService.TAPVizierResult tapResult = tapVizierService.query(TAPVizierService.buildDistanceQuery(
                VSX_CAT, QUERY_FIELDS, coordinates, radiusDegrees, 50
        ));
        List<DistanceModel<VariableStarInformationModel>> result = new ArrayList<>();
        tapResult.getData().forEach(data -> {
            if (data.size() == 8) {
                Double distance = data.get(7) != null ? Double.parseDouble(data.get(7)) : null;
                if (distance != null) {
                    result.add(new DistanceModel<>(distance, getSingleModel(data)));
                }
            }
        });
        return result;
    }

    private VariableStarInformationModel getSingleModel(List<String> data) {
        Long vsxId = data.get(0) != null ? Long.parseLong(data.get(0)) : null;
        String name = data.get(1);
        String type = data.get(2);
        BigDecimal epoch = data.get(3) != null && !data.get(3).isEmpty() ? new BigDecimal(data.get(3)) : null;
        BigDecimal period = data.get(4) != null && !data.get(4).isEmpty() ? new BigDecimal(data.get(4)) : null;
        Double ra = data.get(5) != null && !data.get(5).isEmpty() ? Double.parseDouble(data.get(5)) : null;
        Double dec = data.get(6) != null && !data.get(6).isEmpty() ? Double.parseDouble(data.get(6)) : null;
        CosmicCoordinates coords = null;
        if (ra != null && dec != null) {
            coords = new CosmicCoordinates(ra, dec);
        }
        HashSet<String> names = new HashSet<>();
        names.add(name);
        return new VariableStarInformationModel(
                coords, name, names, type, epoch, period, vsxId
        );
    }
}
