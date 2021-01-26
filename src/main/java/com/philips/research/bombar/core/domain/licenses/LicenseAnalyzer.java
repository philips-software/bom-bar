/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LicenseAnalyzer {
    private Map<String, Integer> frequencies = new HashMap<>();

    public LicenseAnalyzer addProject(Project project) {
        project.getDependencies().stream()
                .flatMap(dep -> dep.getLicenses().stream())
                .forEach(license -> frequencies.merge(license, 1, (key, value) -> value + 1));
        return this;
    }

    public Map<String, Integer> getDistribution() {
        return Collections.unmodifiableMap(frequencies);
    }

    public Map<String, Double> getPercentageDistribution() {
        double total = frequencies.values().stream().mapToInt(i -> i).sum();

        return frequencies.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }
}
