/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.Package.Acceptance;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Relation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseCheckerTest {
    private static final LicenseRegistry REGISTRY = new LicenseRegistry();
    private static final String LICENSE = "License";
    private static final String OTHER = "Other license";
    private static final String VIRAL = "Viral license";
    private static final String VIRAL_RELATION = "Viral given dynamic link";
    private static final String VIRAL_DISTRIBUTION = "Viral given SAAS distribution";
    private static final String INCOMPATIBLE = "Incompatible viral license";
    private static final URI REFERENCE = URI.create("Reference");
    private static final String RATIONALE = "Rationale";

    static {
        REGISTRY.license(LICENSE);
        REGISTRY.license(OTHER);
        final var viral = REGISTRY.license(VIRAL).copyleft();
        REGISTRY.license(VIRAL_RELATION).copyleft(viral, Relation.Relationship.STATIC_LINK);
        REGISTRY.license(VIRAL_DISTRIBUTION).copyleft(viral, Project.Distribution.SAAS);
        REGISTRY.license(INCOMPATIBLE).copyleft();
    }

    private final Dependency parent = new Dependency("Parent", "Parent").setLicense(LICENSE);
    private final Dependency child1 = new Dependency("Child1", "First child").setLicense(LICENSE);
    private final Dependency child2 = new Dependency("Child2", "Second child").setLicense(LICENSE);
    private final Project project = new Project(UUID.randomUUID())
            .addDependency(parent)
            .addDependency(child1)
            .addDependency(child2);
    private final LicenseChecker checker = new LicenseChecker(REGISTRY, project);

    @Test
    void verifiesEmptyProject() {
        parent.setIssueCount(13);

        assertThat(checker.violations()).isEmpty();
        assertThat(project.getIssueCount()).isZero();
        assertThat(parent.getIssueCount()).isZero();
    }

    @Test
    void approvesCompatibleLicenses() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        child1.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child2));

        assertThat(checker.violations()).isEmpty();
        assertThat(project.getIssueCount()).isZero();
        assertThat(parent.getIssueCount()).isZero();
    }

    @Test
    void verifiesSingleDependency() {
        parent.setLicense("Unknown");
        child1.setLicense("Unknown");

        assertThat(checker.violations(parent)).hasSize(1);
    }

    @Test
    void detectsMissingOrEffectivelyEmptyLicense() {
        parent.setLicense(" \n\t");

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("no license");
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
    }

    @Test
    void detectsUnknownLicense() {
        parent.setLicense("Unknown AND Unknown");

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("unknown license").doesNotContain(LICENSE);
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
    }

    @Test
    void acceptsCompatibleMultiLicense() {
        parent.setLicense(String.format("%s AND %s", LICENSE, VIRAL));

        assertThat(checker.violations()).isEmpty();
    }

    @Test
    void detectsIncompatibleLicense() {
        parent.setLicense(String.format("%s AND %s AND %s", LICENSE, VIRAL, INCOMPATIBLE));

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains(VIRAL).contains("incompatible");
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
    }

    @Test
    void detectsIncompatibleChoiceLicense() {
        parent.setLicense(String.format("%s OR %s", VIRAL, INCOMPATIBLE));

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("alternative");
    }

    @Test
    void detectsIncompatibleSubpackage() {
        child1.setLicense(VIRAL);
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package").contains(child1.toString());
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void detectsMultiLicenseIncompatibleSubpackage() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        parent.setLicense(String.format("(%s AND (%s))", LICENSE, OTHER));
        child1.setLicense(VIRAL);

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void detectsIncompatibleMultiLicenseSubpackage() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        parent.setLicense(LICENSE);
        child1.setLicense(String.format("(%s AND (%s))", LICENSE, VIRAL));

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void detectsIncompatibleMultiChoiceLicenseSubpackage() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        parent.setLicense(LICENSE);
        child1.setLicense(String.format("%s OR %s)", LICENSE, VIRAL));

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("explicit choice");
    }

    @Test
    void detectsUnknownSubpackageOnlyOnce() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        child1.setLicense("Unknown");

        assertThat(checker.violations()).hasSize(1);
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isZero();
        assertThat(child1.getIssueCount()).isEqualTo(1);
    }

    @Test
    void detectsIncompatibleSubpackageForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        child1.setLicense(VIRAL_DISTRIBUTION);

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void detectsIncompatibleSubpackageForRelation() {
        parent.addRelation(new Relation(Relation.Relationship.MODIFIED_CODE, child1));
        child1.setLicense(VIRAL_RELATION);

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void checksAllPackagesRecursively() {
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        child1.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child2));
        parent.setLicense("Unknown");
        child2.setLicense(VIRAL);

        final var violations = checker.violations();

        assertThat(violations).hasSize(2);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("unknown");
        assertThat(violations.get(1).toString()).contains(child1.toString()).contains("compatible");
        assertThat(project.getIssueCount()).isEqualTo(2);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isEqualTo(1);
    }

    @Test
    void skipsPackagesBehindIrrelevantRelationship() {
        parent.addRelation(new Relation(Relation.Relationship.IRRELEVANT, child1));
        child1.setLicense(VIRAL);

        final var violations = checker.violations();

        assertThat(violations).isEmpty();
    }

    @Test
    void detectsIncompatibleChildLicensesForDistribution() {
        project.setDistribution(Project.Distribution.PROPRIETARY);
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child1));
        parent.addRelation(new Relation(Relation.Relationship.INDEPENDENT, child2));
        child2.setLicense(VIRAL_DISTRIBUTION);

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("depends on incompatible");
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Test
    void detectsIncompatibleChildLicensesForRelation() {
        parent.addRelation(new Relation(Relation.Relationship.MODIFIED_CODE, child1));
        parent.addRelation(new Relation(Relation.Relationship.MODIFIED_CODE, child2));
        child2.setLicense(VIRAL_RELATION);

        final var violations = checker.violations();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("depends on incompatible");
        assertThat(project.getIssueCount()).isEqualTo(1);
        assertThat(parent.getIssueCount()).isEqualTo(1);
        assertThat(child1.getIssueCount()).isZero();
    }

    @Nested
    class PackageExemptions {
        private final Package pkg = new Package(REFERENCE);

        @BeforeEach
        void beforeEach() {
            parent.setPackage(pkg);
        }

        @Test
        void exemptsMissingLicenseViaPackage() {
            pkg.exemptLicense("");
            parent.setLicense("");

            assertThat(checker.violations()).isEmpty();
        }

        @Test
        void exemptsUnknownLicenseViaPackage() {
            pkg.exemptLicense("Unknown");
            parent.setLicense("Unknown");

            assertThat(checker.violations()).isEmpty();
        }
    }

    @Nested
    class ProjectExemptions {
        @BeforeEach
        void beforeEach() {
            parent.setPackage(new Package(REFERENCE));
            project.exempt(parent, RATIONALE);
        }

        @Test
        void exemptsMissingLicenseViaProject() {
            parent.setLicense("");

            assertThat(checker.violations()).isEmpty();
        }

        @Test
        void exemptsUnknownLicenseViaProject() {
            parent.setLicense("Unknown");

            assertThat(checker.violations()).isEmpty();
        }
    }

    @Nested
    class PackageDefinitionApproval {
        private final Package pkg = new Package(REFERENCE);

        @BeforeEach
        void setUp() {
            parent.setPackage(pkg);
        }

        @Test
        void raisesUseOfForbiddenPackage() {
            pkg.setAcceptance(Acceptance.FORBIDDEN);

            final var violations = checker.violations();

            assertThat(violations).hasSize(1);
            assertThat(violations.get(0).toString()).contains("is forbidden");
        }

        @Test
        void suppressesLicenseViolation() {
            pkg.setAcceptance(Acceptance.APPROVED);
            parent.setLicense("Unknown");

            final var violations = checker.violations();

            assertThat(violations).isEmpty();
        }

        @Test
        void requiresExplicitPerProjectExemption() {
            assertThat(checker.violations()).isEmpty();
            pkg.setAcceptance(Acceptance.PER_PROJECT);
            assertThat(checker.violations()).isNotEmpty();

            project.exempt(parent, "Testing project exemption");

            assertThat(checker.violations()).isEmpty();
        }

        @Test
        void raisesDependencyIsNotAPackage() {
            pkg.setAcceptance(Acceptance.NOT_A_PACKAGE);

            final var violations = checker.violations();

            assertThat(violations).hasSize(1);
            assertThat(violations.get(0).toString()).contains("not a package");
        }
    }
}
