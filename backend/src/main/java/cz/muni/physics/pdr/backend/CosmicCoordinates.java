package cz.muni.physics.pdr.backend;


import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CosmicCoordinates {

    private final static String RA_NUMBER_PATTERN = "(\\d*(\\.\\d+)?)";
    private final static String DEC_NUMBER_PATTERN = "([+\\-]?\\d*(\\.\\d+)?)";
    private final static Pattern RA_STRING_PATTERN = Pattern.compile("(\\d{1,2})[\\s:](\\d{1,2})[\\s:](\\d{0,2}(\\.\\d+)?)");
    private final static Pattern DEC_STRING_PATTERN = Pattern.compile("([+\\-]?\\d{1,2})[\\s:](\\d{1,2})[\\s:](\\d{0,2}(\\.\\d+)?)");

    private double rightAscension;
    private double declination;

    public CosmicCoordinates(double rightAscension, double declination) {
        this.rightAscension = rightAscension;
        this.declination = declination;
    }

    // todo: use factory?
    public CosmicCoordinates(String coordinates) {
        this(coordinates.split(" ")[0], coordinates.split(" ")[1]);
    }

    public CosmicCoordinates(String ra, String dec) {
        this.rightAscension = raFromString(ra);
        this.declination = decFromString(dec);
    }

    public double getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public double getDeclination() {
        return declination;
    }

    public void setDeclination(double declination) {
        this.declination = declination;
    }

    private double raFromString(String ra) {
        if (ra.matches(RA_NUMBER_PATTERN)) {
            return Double.valueOf(ra);
        }
        Matcher matcher = RA_STRING_PATTERN.matcher(ra);
        if (matcher.matches()) {
            double hours = Double.valueOf(matcher.group(1));
            double minutes = Double.valueOf(matcher.group(2));
            double seconds = Double.valueOf(matcher.group(3));

            return hours * 15 + minutes / 4 + seconds / 240;
        }
        throw new IllegalArgumentException("invalid format of ra");
    }

    private double decFromString(String dec) {
        if (dec.matches(DEC_NUMBER_PATTERN)) {
            return Double.valueOf(dec);
        }
        Matcher matcher = DEC_STRING_PATTERN.matcher(dec);
        if (matcher.matches()) {
            double degrees = Double.valueOf(matcher.group(1));
            double arcmin = Double.valueOf(matcher.group(2));
            double arcsec = Double.valueOf(matcher.group(3));

            BiFunction<Double, Double, Double> op = (degrees > 0) ? (a, b) -> a + b : (a, b) -> a - b;

            return op.apply(degrees, arcmin / 60 + arcsec / 3600);

        }
        throw new IllegalArgumentException("invalid format of dec");
    }
}

