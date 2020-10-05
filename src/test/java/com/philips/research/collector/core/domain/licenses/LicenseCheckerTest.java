/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Dependency;
import com.philips.research.collector.core.domain.PackageDefinition;
import com.philips.research.collector.core.domain.Project;
import com.philips.research.collector.core.domain.Relation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseCheckerTest {
    private static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String LICENSE = "License";
    private static final String REQUIRED = "Required";
    private static final String FORBIDDEN = "Forbidden";
    private static final String REQUIRED_GIVEN = "Required given";
    private static final String FORBIDDEN_GIVEN = "Forbidden given";
    private static final PackageDefinition PACKAGE = new PackageDefinition("Package");

    private static final String TERM = "Term";

    static {
        REGISTRY.term(TERM, "Description");
        REGISTRY.license(LICENSE);
        REGISTRY.license(REQUIRED).require(TERM);
        REGISTRY.license(FORBIDDEN).forbid(TERM);
        REGISTRY.license(REQUIRED_GIVEN)
                .require(TERM, Project.Distribution.SAAS)
                .require(TERM, Relation.Type.STATIC_LINK)
                .require(TERM, Dependency.Exemption.FAILED);
        REGISTRY.license(FORBIDDEN_GIVEN)
                .forbid(TERM, Project.Distribution.SAAS)
                .forbid(TERM, Relation.Type.STATIC_LINK)
                .forbid(TERM, Dependency.Exemption.FAILED);
    }

    private final Dependency parent = new Dependency(PACKAGE, "Parent").setLicense(LICENSE);
    private final Dependency child1 = new Dependency(PACKAGE, "Child1").setLicense(LICENSE);
    private final Dependency child2 = new Dependency(PACKAGE, "Child2").setLicense(LICENSE);
    private final Project project = new Project(UUID.randomUUID())
            .addDependency(parent)
            .addDependency(child1)
            .addDependency(child2);
    private final LicenseChecker checker = new LicenseChecker(REGISTRY, project);

    @Test
    void verifiesEmptyProject() {
        assertThat(checker.verify()).isEmpty();
    }

    @Test
    void approvesCompatibleLicenses() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        child1.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));

        assertThat(checker.verify()).isEmpty();
    }

    @Test
    void detectsMissingOrEffectivelyEmptyLicense() {
        parent.setLicense(" \n\t");

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("no license");
    }

    @Test
    void detectsDualLicense() {
        parent.setLicense(LICENSE + " OR " + LICENSE);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("alternative licenses");
    }

    @Test
    void detectsUnknownLicense() {
        parent.setLicense("Unknown AND Unknown");

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("unknown license").doesNotContain(LICENSE);
    }

    @Test
    void detectsIncompatibleSubpackage() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(REQUIRED);
        child1.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsMultiLicenseIncompatibleSubpackage() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(String.format("(%s AND (%s))", LICENSE, REQUIRED));
        child1.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleMultiLicenseSubpackage() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(REQUIRED);
        child1.setLicense(String.format("(%s AND (%s))", LICENSE, FORBIDDEN));

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsUnknownSubpackageOnlyOnce() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        child1.setLicense("Unknown");

        assertThat(checker.verify()).hasSize(1);
    }

    @Test
    void detectsIncompatibleSubpackageForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleSubpackageForRelation() {
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child1));
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    @Disabled
    void detectsIncompatibleSubpackageForExemption() {
        //TODO How to exempt a package?
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleChildLicenses() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        child1.setLicense(REQUIRED);
        child2.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void checksAllPackagesRecursively() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        child1.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        parent.setLicense("Unknown");
        child1.setLicense(REQUIRED);
        child2.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(2);
        assertThat(violations.get(0).toString()).contains(child1.toString()).contains("compatible");
        assertThat(violations.get(1).toString()).contains(parent.toString()).contains("unknown");
    }

    @Test
    void ignoresAggregateIncompatibilitiesWhenIncompatibleChildFound() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        parent.setLicense(REQUIRED);
        child1.setLicense(REQUIRED);
        child2.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package");
    }

    @Test
    void detectsIncompatibleChildLicensesForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void detectsIncompatibleChildLicensesForRelation() {
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child1));
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child2));
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    @Disabled
    void detectsIncompatibleChildLicensesForExemption() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        //TODO How to set exemption?
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }
}
