/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
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

    private static final String TERM = "Term";

    static {
        REGISTRY.term(TERM, "Description");
        REGISTRY.license(LICENSE);
        REGISTRY.license(REQUIRED).require(TERM);
        REGISTRY.license(FORBIDDEN).forbid(TERM);
        REGISTRY.license(REQUIRED_GIVEN)
                .require(TERM, Project.Distribution.SAAS)
                .require(TERM, Package.Relation.STATIC_LINK)
                .require(TERM, Package.Exemption.FAILED);
        REGISTRY.license(FORBIDDEN_GIVEN)
                .forbid(TERM, Project.Distribution.SAAS)
                .forbid(TERM, Package.Relation.STATIC_LINK)
                .forbid(TERM, Package.Exemption.FAILED);
    }

    private final Package parent = new Package("Parent", "v1").setLicense(LICENSE);
    private final Package child1 = new Package("Child 1", "v2").setLicense(LICENSE);
    private final Package child2 = new Package("Child 2", "v3").setLicense(LICENSE);
    private final Project project = new Project(UUID.randomUUID())
            .addPackage(parent)
            .addPackage(child1)
            .addPackage(child2);
    private final LicenseChecker checker = new LicenseChecker(REGISTRY, project);


    @Test
    void verifiesEmptyProject() {
        assertThat(checker.verify()).isEmpty();
    }

    @Test
    void approvesCompatibleLicenses() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        child1.addChild(child2, Package.Relation.INDEPENDENT);

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
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED);
        child1.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsMultiLicenseIncompatibleSubpackage() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(String.format("(%s AND (%s))", LICENSE, REQUIRED));
        child1.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleMultiLicenseSubpackage() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED);
        child1.setLicense(String.format("(%s AND (%s))", LICENSE, FORBIDDEN));

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsUnknownSubpackageOnlyOnce() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        child1.setLicense("Unknown");

        assertThat(checker.verify()).hasSize(1);
    }

    @Test
    void detectsIncompatibleSubpackageForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleSubpackageForRelation() {
        parent.addChild(child1, Package.Relation.MODIFIED_CODE);
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
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleChildLicenses() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
        child1.setLicense(REQUIRED);
        child2.setLicense(FORBIDDEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void checksAllPackagesRecursively() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        child1.addChild(child2, Package.Relation.INDEPENDENT);
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
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
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
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void detectsIncompatibleChildLicensesForRelation() {
        parent.addChild(child1, Package.Relation.MODIFIED_CODE);
        parent.addChild(child2, Package.Relation.MODIFIED_CODE);
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    @Disabled
    void detectsIncompatibleChildLicensesForExemption() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
        //TODO How to set exemption?
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(FORBIDDEN_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }
}
