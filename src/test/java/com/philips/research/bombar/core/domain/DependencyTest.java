/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyTest {
    private static final String ID = "Id";
    private static final String TITLE = "Title";
    private static final PackageRef REFERENCE = new PackageRef("Reference");
    private static final PackageURL PURL = purlOf("pkg:type/ns/name@version");
    private static final Package PACKAGE = new Package(REFERENCE);
    private static final String VERSION = "Version";
    private static final String LICENSE = "License";
    private static final String DESCRIPTION = "Description";
    private static final int COUNT = 42;

    private final Dependency dependency = new Dependency(ID, TITLE);

    static PackageURL purlOf(String purl) {
        try {
            return new PackageURL(purl);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Test
    void createsInstance() {
        assertThat(dependency.getKey()).isEqualTo(ID);
        assertThat(dependency.getTitle()).isEqualTo(TITLE);
        assertThat(dependency.getPackage()).isEmpty();
        assertThat(dependency.getVersion()).isEmpty();
        assertThat(dependency.getPurl()).isEmpty();
        assertThat(dependency.getLicense()).isEmpty();
        assertThat(dependency.isRoot()).isFalse();
        assertThat(dependency.isDevelopment()).isFalse();
        assertThat(dependency.isDelivered()).isFalse();
        assertThat(dependency.getRelations()).isEmpty();
        assertThat(dependency.getUsages()).isEmpty();
        assertThat(dependency.getExemption()).isEmpty();
    }

    @Test
    void generatesIdentityIfNoneProvided() {
        final var anonymous = new Dependency(null, TITLE);

        assertThat(anonymous.getKey()).isNotEmpty();
    }

    @Test
    void updatesPackage() {
        dependency.setPackage(PACKAGE);
        dependency.setPurl(PURL);

        assertThat(dependency.getPackage()).contains(PACKAGE);
        assertThat(dependency.getPurl()).contains(PURL);
    }

    @Test
    void providesPackageReference() {
        dependency.setPackage(PACKAGE);

        assertThat(dependency.getPackageReference()).contains(REFERENCE);
    }

    @Test
    void updatesVersion() {
        dependency.setVersion(VERSION);

        assertThat(dependency.getVersion()).contains(VERSION);
    }

    @Test
    void updatesLicense() {
        dependency.setLicense(LICENSE);

        assertThat(dependency.getLicense()).contains(LICENSE);
    }

    @Test
    void extractsLicenseComponents() {
        dependency.setLicense("(A OR ( B AND A) OR (C and D ))");

        assertThat(dependency.getLicenses()).containsExactly("A", "B", "C and D");
    }

    @Test
    void updatesRootStatus() {
        dependency.setRoot();

        assertThat(dependency.isRoot()).isTrue();
        assertThat(dependency.isDelivered()).isTrue();
    }

    @Test
    void updatesDevelopmentStatus() {
        dependency.setDevelopment();

        assertThat(dependency.isDevelopment()).isTrue();
    }

    @Test
    void updatesDeliveryStatus() {
        dependency.setDelivered();

        assertThat(dependency.isDelivered()).isTrue();
    }

    @Test
    void tracksNumberOfIssues() {
        dependency.setIssueCount(COUNT);

        assertThat(dependency.getIssueCount()).isEqualTo(COUNT);
    }

    @Test
    void addsRelations() {
        final var target = new Dependency(ID, "Child");
        final var relation = new Relation(Relation.Relationship.STATIC_LINK, target);
        dependency.addRelation(relation);

        final var dependencies = dependency.getRelations();

        assertThat(dependencies).containsExactly(relation);
    }


    @Test
    void addsUsages() {
        final var target = new Dependency(ID, "Parent");
        dependency.addUsage(target);

        final var dependencies = dependency.getUsages();

        assertThat(dependencies).containsExactly(target);
    }

    @Test
    void noStrongUsageForOrphan() {
        assertThat(dependency.getStrongestUsage()).isEmpty();
    }

    @Test
    void findsStrongestRelationshipFromOwnUsage() {
        final var parent = new Dependency(ID, "Parent");
        final var child = new Dependency(ID, "Child");
        final var sibling = new Dependency(ID, "Sibling");
        final var childRelation = new Relation(Relation.Relationship.STATIC_LINK, child);
        final var siblingRelation = new Relation(Relation.Relationship.DYNAMIC_LINK, sibling);
        parent.addRelation(childRelation);
        parent.addRelation(siblingRelation);
        child.addUsage(parent);
        sibling.addUsage(parent);

        assertThat(child.getStrongestUsage()).contains(childRelation.getType());
    }

    @Test
    void detectsStrongestRelationshipFromMultipleUsages() {
        final var parent = new Dependency(ID, "Parent");
        final var strongerRelation = new Relation(Relation.Relationship.STATIC_LINK, dependency);
        final var weakerRelation = new Relation(Relation.Relationship.DYNAMIC_LINK, dependency);

        parent.addRelation(strongerRelation);
        parent.addRelation(weakerRelation);
        dependency.addUsage(parent);

        assertThat(dependency.getStrongestUsage()).contains(strongerRelation.getType());
    }

    @Test
    void tracksExemption() {
        dependency.setExemption(DESCRIPTION);

        assertThat(dependency.getExemption()).contains(DESCRIPTION);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Dependency.class)
                .withOnlyTheseFields("key")
                .withNonnullFields("key")
                .withPrefabValues(Dependency.class, new Dependency("A", TITLE), new Dependency("B", TITLE))
                .verify();
    }
}
