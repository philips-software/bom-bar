/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.PackageDefinition;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Relation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseCheckerTest {
    private static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String LICENSE = "License";
    private static final String OTHER = "Other license";
    private static final String VIRAL = "Viral license";
    private static final String VIRAL_RElATION = "Viral given dynamic link";
    private static final String VIRAL_DISTRIBUTION = "Viral given SAAS distribution";
    private static final String INCOMPATIBLE = "Incompatible viral license";
    private static final PackageDefinition PACKAGE = new PackageDefinition("Package");

    static {
        REGISTRY.license(LICENSE);
        REGISTRY.license(OTHER);
        final var viral = REGISTRY.license(VIRAL).copyleft();
        REGISTRY.license(VIRAL_RElATION).copyleft(viral, Relation.Type.STATIC_LINK);
        REGISTRY.license(VIRAL_DISTRIBUTION).copyleft(viral, Project.Distribution.SAAS);
        REGISTRY.license(INCOMPATIBLE).copyleft();
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
    void detectsIncompatibleLicense() {
        parent.setLicense(String.format("%s AND %s AND %s", LICENSE, VIRAL, INCOMPATIBLE));

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains(VIRAL);
    }

    @Test
    void detectsIncompatibleSubpackage() {
        child1.setLicense(VIRAL);
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsMultiLicenseIncompatibleSubpackage() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(String.format("(%s AND (%s))", LICENSE, OTHER));
        child1.setLicense(VIRAL);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleMultiLicenseSubpackage() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.setLicense(LICENSE);
        child1.setLicense(String.format("(%s AND (%s))", LICENSE, VIRAL));

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
        child1.setLicense(VIRAL_DISTRIBUTION);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleSubpackageForRelation() {
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child1));
        child1.setLicense(VIRAL_RElATION);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void checksAllPackagesRecursively() {
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        child1.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        parent.setLicense("Unknown");
        child2.setLicense(VIRAL);

        final var violations = checker.verify();

        assertThat(violations).hasSize(2);
        assertThat(violations.get(0).toString()).contains(child1.toString()).contains("compatible");
        assertThat(violations.get(1).toString()).contains(parent.toString()).contains("unknown");
    }

    @Test
    void detectsIncompatibleChildLicensesForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Type.INDEPENDENT, child2));
        child2.setLicense(VIRAL_DISTRIBUTION);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("depends on incompatible");
    }

    @Test
    void detectsIncompatibleChildLicensesForRelation() {
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child1));
        parent.addRelation(new Relation(Relation.Type.MODIFIED_CODE, child2));
        child2.setLicense(VIRAL_RElATION);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("depends on incompatible");
    }
}
