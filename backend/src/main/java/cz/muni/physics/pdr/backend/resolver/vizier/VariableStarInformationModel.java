package cz.muni.physics.pdr.backend.resolver.vizier;

import cz.muni.physics.pdr.backend.CosmicCoordinates;

import java.math.BigDecimal;
import java.util.Set;

public class VariableStarInformationModel {

    private CosmicCoordinates coordinates;
    private String originalName;
    private Set<String> names;
    private String type;
    private BigDecimal m0;
    private BigDecimal period;
    private Long vsxId;

    public VariableStarInformationModel() {
    }

    public VariableStarInformationModel(CosmicCoordinates coordinates, String originalName, Set<String> names, String type, BigDecimal m0, BigDecimal period, Long vsxId) {
        this.coordinates = coordinates;
        this.originalName = originalName;
        this.names = names;
        this.type = type;
        this.m0 = m0;
        this.period = period;
        this.vsxId = vsxId;
    }

    public CosmicCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CosmicCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getM0() {
        return m0;
    }

    public void setM0(BigDecimal m0) {
        this.m0 = m0;
    }

    public BigDecimal getPeriod() {
        return period;
    }

    public void setPeriod(BigDecimal period) {
        this.period = period;
    }

    public Long getVsxId() {
        return vsxId;
    }

    public void setVsxId(Long vsxId) {
        this.vsxId = vsxId;
    }
}
