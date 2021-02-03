/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseAnalyzerTest {
    final LicenseAnalyzer analyzer = new LicenseAnalyzer();

    @Test
    void createsInstance() {
        assertThat(analyzer.getDistribution()).isEmpty();
        assertThat(analyzer.getPercentageDistribution()).isEmpty();
    }

    @Test
    void addsDependenciesOfProject() {
        final var dep1 = new Dependency("1", "1").setLicense("A");
        final var dep2 = new Dependency("2", "2").setLicense("A and B");
        final var dep3 = new Dependency("3", "3").setLicense("A or C");
        final var project = new Project(UUID.randomUUID())
                .addDependency(dep1)
                .addDependency(dep2)
                .addDependency(dep3);

        analyzer.addProject(project);

        assertThat(analyzer.getDistribution()).containsExactlyInAnyOrderEntriesOf(Map.of("A", 3, "B", 1, "C", 1));
    }

    @Test
    void indicatesMissingLicenses() {
        final var project = new Project(UUID.randomUUID())
                .addDependency(new Dependency("None", ""));

        analyzer.addProject(project);

        assertThat(analyzer.getDistribution()).containsEntry("(No license)", 1);
    }

    @Test
    void representsAsPercentage() {
        final var dep1 = new Dependency("1", "1").setLicense("A");
        final var dep2 = new Dependency("2", "2").setLicense("A and B");
        final var project = new Project(UUID.randomUUID())
                .addDependency(dep1)
                .addDependency(dep2);

        analyzer.addProject(project);

        assertThat(analyzer.getPercentageDistribution())
                .containsExactlyInAnyOrderEntriesOf(Map.of("A", 2.0 / 3, "B", 1.0 / 3));
    }
}
