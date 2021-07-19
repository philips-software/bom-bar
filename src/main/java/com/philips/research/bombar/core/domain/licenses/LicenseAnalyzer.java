/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes the distribution of licenses.
 */
public class LicenseAnalyzer {
    private final Map<String, Integer> frequencies = new HashMap<>();

    /**
     * Adds all dependencies of a project.
     *
     * @param project add the project
     */
    public LicenseAnalyzer addProject(Project project) {
        project.getDependencies().stream()
                .flatMap(dep -> {
                    final var licenses = dep.getLicenses();
                    return licenses.isEmpty() ? Stream.of("(No license)") : licenses.stream();
                })
                .forEach(license -> frequencies.merge(license, 1, (value, x) -> value + 1));
        return this;
    }

    /**
     * @return distribution based on license counts
     */
    public Map<String, Integer> getDistribution() {
        return Collections.unmodifiableMap(frequencies);
    }

    /**
     * @return distribution based on license frequencies in range (0.0 - 1.0]
     */
    public Map<String, Double> getPercentageDistribution() {
        double total = frequencies.values().stream().mapToInt(i -> i).sum();

        return frequencies.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    /**
     * @return map of license obligations mapped to the present dependencies having the license
     */
    public Map<String, Set<Dependency>> findLicenseObligationsForDependencies(Project project, LicenseRegistry registry) {
        Map<String, Set<Dependency>> licenseObligations = new HashMap<>();
        project.getDependencies().forEach(dep ->
                Optional.of(dep.getLicenses()).ifPresent(licenses -> licenses.forEach(license -> {
                    LicenseType licenseType;
                    try {
                        licenseType = registry.licenseType(license);
                        licenseType.requiresGiven().forEach(require ->
                                licenseObligations.compute(require.getDescription(), (k, v) -> (v == null) ? Set.of(dep) : Stream.concat(v.stream(), Stream.of(dep))
                                        .collect(Collectors.toSet()))
                        );
                    } catch (IllegalArgumentException e) {
                        // do nothing, The License is not in our registry
                    }
                }))
        );
        return licenseObligations;
    }
}
