package com.philips.research.collector.core.domain;

public class License {
    private final String identifier;

    private Package.Relation maxRelation;
    private Project.Distribution maxDistribution;

    public License(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Package.Relation getMaxRelation() {
        return maxRelation;
    }

    public License setMaxRelation(Package.Relation maxRelation) {
        this.maxRelation = maxRelation;
        return this;
    }

    public Project.Distribution getMaxDistribution() {
        return maxDistribution;
    }

    public License setMaxDistribution(Project.Distribution maxDistribution) {
        this.maxDistribution = maxDistribution;
        return this;
    }
}
