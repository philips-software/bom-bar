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
    private static final String DENIED = "Denied";
    private static final String REQUIRED_GIVEN = "Required given";
    private static final String DENIED_GIVEN = "Denied given";

    private static final String ATTRIBUTE = "Attribute";

    static {
        REGISTRY.attribute(ATTRIBUTE, "Description");
        REGISTRY.license(LICENSE);
        REGISTRY.license(REQUIRED).require(ATTRIBUTE);
        REGISTRY.license(DENIED).deny(ATTRIBUTE);
        REGISTRY.license(REQUIRED_GIVEN)
                .require(ATTRIBUTE, Project.Distribution.SAAS)
                .require(ATTRIBUTE, Package.Relation.STATIC_LINK)
                .require(ATTRIBUTE, Package.Exemption.FAILED);
        REGISTRY.license(DENIED_GIVEN)
                .deny(ATTRIBUTE, Project.Distribution.SAAS)
                .deny(ATTRIBUTE, Package.Relation.STATIC_LINK)
                .deny(ATTRIBUTE, Package.Exemption.FAILED);
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
        parent.setLicense("Left OR Right");

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("alternative licenses");
    }

    @Test
    void detectsUnknownLicense() {
        parent.setLicense("Unknown AND " + LICENSE);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("unknown license").doesNotContain(LICENSE);
    }

    @Test
    void detectsIncompatibleSubpackage() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED);
        child1.setLicense(DENIED);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsMultiLicenseIncompatibleSubpackage() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(String.format("(%s AND (%s))", LICENSE, REQUIRED));
        child1.setLicense(DENIED);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).doesNotContain(LICENSE).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleMultiLicenseSubpackage() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED);
        child1.setLicense(String.format("(%s AND (%s))", LICENSE, DENIED));

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
        child1.setLicense(DENIED_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleSubpackageForRelation() {
        parent.addChild(child1, Package.Relation.SOURCE_CODE);
        parent.setLicense(REQUIRED_GIVEN);
        child1.setLicense(DENIED_GIVEN);

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
        child1.setLicense(DENIED_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains("package").contains(child1.toString());
    }

    @Test
    void detectsIncompatibleChildLicenses() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
        child1.setLicense(REQUIRED);
        child2.setLicense(DENIED);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void ignoresAggregateIncompatibilitiesWhenIncompatibleChildFound() {
        parent.addChild(child1, Package.Relation.INDEPENDENT);
        parent.addChild(child2, Package.Relation.INDEPENDENT);
        parent.setLicense(REQUIRED);
        child1.setLicense(REQUIRED);
        child2.setLicense(DENIED);

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
        child2.setLicense(DENIED_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }

    @Test
    void detectsIncompatibleChildLicensesForRelation() {
        parent.addChild(child1, Package.Relation.SOURCE_CODE);
        parent.addChild(child2, Package.Relation.SOURCE_CODE);
        child1.setLicense(REQUIRED_GIVEN);
        child2.setLicense(DENIED_GIVEN);

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
        child2.setLicense(DENIED_GIVEN);

        final var violations = checker.verify();

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).toString()).contains(parent.toString()).contains("subpackages");
    }
}
