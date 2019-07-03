package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.CosmicCoordinates;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public interface TAPVizierService {

    public static String buildDistanceQuery(String catalogue, String queryFields, CosmicCoordinates coordinates, double radiusDegrees, int limit) {
        MessageFormat distanceFormat = new MessageFormat("DISTANCE(POINT(''ICRS'',{0}, {1}), POINT(''ICRS'',{2}.RAJ2000, {2}.DEJ2000)) as \"DISTANCE\"", Locale.ENGLISH);
        String distanceField = distanceFormat.format(new Object[]{coordinates.getRightAscension(), coordinates.getDeclination(), catalogue});
        MessageFormat queryFormat = new MessageFormat("SELECT TOP {0} {1}, {2} FROM {3} WHERE 1=CONTAINS(POINT(''ICRS'',{3}.RAJ2000,{3}.DEJ2000), CIRCLE(''ICRS'', {4}, {5}, {6})) ORDER BY \"DISTANCE\"", Locale.ENGLISH);
        return queryFormat.format(new Object[]{limit, queryFields, distanceField, catalogue, coordinates.getRightAscension(), coordinates.getDeclination(), radiusDegrees});
    }

    TAPVizierResult query(String query);

    class TAPVizierResult {
        private List<TAPVizierMetadata> metadata;
        private List<List<String>> data;

        public List<TAPVizierMetadata> getMetadata() {
            return metadata;
        }

        public void setMetadata(List<TAPVizierMetadata> metadata) {
            this.metadata = metadata;
        }

        public List<List<String>> getData() {
            return data;
        }

        public void setData(List<List<String>> data) {
            this.data = data;
        }
    }

    class TAPVizierMetadata {
        private String name;
        private String description;
        private String datatype;
        private String unit;
        private String ucd;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUcd() {
            return ucd;
        }

        public void setUcd(String ucd) {
            this.ucd = ucd;
        }
    }
}
