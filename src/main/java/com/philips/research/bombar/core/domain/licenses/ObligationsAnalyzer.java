package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Relation;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class ObligationsAnalyzer {
    private final LicenseRegistry registry;
    private final Project project;

    public ObligationsAnalyzer(LicenseRegistry registry, Project project) {
        this.registry = registry;
        this.project = project;
    }

    /**
     * @return map of license obligations mapped to the present dependencies including the license
     */
    public Map<String, Set<Dependency>> findObligations() {
        Map<String, Set<Dependency>> obligations = new HashMap<>();
        project.getDependencies().forEach(dep -> {
            final var relationship = dep.getStrongestUsage().orElse(Relation.Relationship.weakest());
            final var distribution = project.getDistribution();
            dep.getLicenses().stream()
                    .flatMap(license -> obligationsFor(license, distribution, relationship).stream())
                    .forEach(obligation -> obligations.compute(obligation, (k, v) -> addToSet(v, dep)));
        });
        return obligations;
    }

    private Set<String> obligationsFor(String license, Project.Distribution distribution, Relation.Relationship relationship) {
        try {
            return registry.licenseType(license)
                    .requiresGiven(distribution, relationship).stream()
                    .map(Term::getDescription)
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            return Set.of("Unknown Obligations");
        }
    }

    private <T> Set<T> addToSet(@NullOr Set<T> set, T value) {
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(value);
        return set;
    }
}
