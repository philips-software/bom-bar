package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ObligationAnalyzer {
    private final LicenseRegistry registry;
    private final Project project;

    public ObligationAnalyzer(LicenseRegistry registry, Project project) {
        this.registry = registry;
        this.project = project;
    }

    /**
     * @return map of license obligations mapped to the present dependencies having the license
     */
    public Map<String, Set<Dependency>> findObligations() {
        Map<String, Set<Dependency>> obligations = new HashMap<>();
        project.getDependencies().forEach(dep ->
                dep.getLicenses().forEach(license -> {
                    try {
                        final var licenseType = registry.licenseType(license);
                        final var relation = dep.getStrongUsage();
                        licenseType.requiresGiven(project.getDistribution(),relation.get()).forEach(require ->
                                obligations.compute(require.getDescription(), (k, v) -> addToSet(v, dep))
                        );
                    } catch (IllegalArgumentException e) {
                        obligations.compute("Unknown Obligations", (k, v) -> addToSet(v, dep));
                    }
                })
        );
        return obligations;
    }

    private Set<Dependency> addToSet(@NullOr Set<Dependency> dependencies, Dependency dependency) {
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        dependencies.add(dependency);
        return dependencies;
    }
}
