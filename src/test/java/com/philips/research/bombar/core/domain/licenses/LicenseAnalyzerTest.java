/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseAnalyzerTest {
    private static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String LICENSE_A = "LicenseA";
    private static final String LICENSE_B = "LicenseB";
    private static final String LICENSE_C = "LicenseC";
    private static final String LICENSE_D = "LicenseD";
    private static final String LICENSE_E = "LicenseE";
    private static final String REQ_A = "Req_a";
    private static final String REQ_A_DESC = "Requires_A";
    private static final String REQ_B = "Req_b";
    private static final String REQ_B_DESC = "Requires_B";

    static {
        REGISTRY.term(REQ_A, REQ_A_DESC);
        REGISTRY.term(REQ_B, REQ_B_DESC);
        REGISTRY.license(LICENSE_A).requires(REQ_A);
        REGISTRY.license(LICENSE_B).requires(REQ_B);
        REGISTRY.license(LICENSE_C).requires(REQ_A);
        REGISTRY.license(LICENSE_D).requires(REQ_B);
        REGISTRY.license(LICENSE_E);
    }

    final LicenseAnalyzer analyzer = new LicenseAnalyzer();

    @Test
    void createsInstance() {
        assertThat(analyzer.getDistribution()).isEmpty();
        assertThat(analyzer.getPercentageDistribution()).isEmpty();
    }

    @Test
    void addsDependenciesOfProject() {
        final var dep1 = new Dependency("1", "1").setLicense("A");
        final var dep2 = new Dependency("2", "2").setLicense("A AND B");
        final var dep3 = new Dependency("3", "3").setLicense("A OR C");
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
        final var dep2 = new Dependency("2", "2").setLicense("A AND B");
        final var project = new Project(UUID.randomUUID())
                .addDependency(dep1)
                .addDependency(dep2);

        analyzer.addProject(project);

        assertThat(analyzer.getPercentageDistribution())
                .containsExactlyInAnyOrderEntriesOf(Map.of("A", 2.0 / 3, "B", 1.0 / 3));
    }

    @Test
    void licenseObligationOfProjectByDependencies() {
        final var dep1 = new Dependency("1", "1").setLicense(LICENSE_A);
        final var dep2 = new Dependency("2", "2").setLicense(LICENSE_B + "AND" + LICENSE_C);
        final var dep3 = new Dependency("3", "3").setLicense(LICENSE_D + "OR" + LICENSE_A);
        final var dep4 = new Dependency("4", "4").setLicense(LICENSE_E);
        final var dep5 = new Dependency("5", "5").setLicense("Other");


        final var project = new Project(UUID.randomUUID())
                .addDependency(dep1).addDependency(dep2).addDependency(dep3).addDependency(dep4);
        Map<String, Set<Dependency>> obligationsMap = analyzer.findLicenseObligationsForDependencies(project, REGISTRY);
        assertThat(obligationsMap.get(REQ_A_DESC)).contains(dep1, dep2, dep3);
        assertThat(obligationsMap.get(REQ_B_DESC)).contains(dep2, dep3);
        assertThat(obligationsMap.values().stream().flatMap(Set::stream).collect(Collectors.toList())).doesNotContain(dep4, dep5);
    }
}
