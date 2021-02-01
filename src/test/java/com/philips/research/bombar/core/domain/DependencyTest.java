/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencyTest {
    private static final String ID = "Id";
    private static final String TITLE = "Title";
    private static final URI REFERENCE = URI.create("Reference");
    private static final Package PACKAGE = new Package(REFERENCE);
    private static final String VERSION = "Version";
    private static final String LICENSE = "License";
    private static final String DESCRIPTION = "Description";
    private static final int COUNT = 42;

    private final Dependency dependency = new Dependency(ID, TITLE);

    @Test
    void createsInstance() {
        assertThat(dependency.getKey()).isEqualTo(ID);
        assertThat(dependency.getTitle()).isEqualTo(TITLE);
        assertThat(dependency.getPackage()).isEmpty();
        assertThat(dependency.getVersion()).isEmpty();
        assertThat(dependency.getPackageUrl()).isEmpty();
        assertThat(dependency.getLicense()).isEmpty();
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

        assertThat(dependency.getPackage()).contains(PACKAGE);
        assertThat(dependency.getPackageUrl()).contains(URI.create("pkg:" + PACKAGE.getReference()));
    }

    @Test
    void providesPackageReference() {
        dependency.setPackage(PACKAGE);

        assertThat(dependency.getPackageReference()).contains(REFERENCE);
    }

    @Test
    void providesVersionedPackageUrl() {
        dependency.setVersion(VERSION);
        dependency.setPackage(PACKAGE);

        assertThat(dependency.getPackage()).contains(PACKAGE);
        assertThat(dependency.getPackageUrl()).contains(URI.create("pkg:" + PACKAGE.getReference() + '@' + VERSION));
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
        dependency.setLicense("(A or ( B AnD A) OR (B )) and C");

        assertThat(dependency.getLicenses()).containsExactly("A", "B", "C");
    }

    @Test
    void updatesSourcePackageStatus() {
        dependency.setPackage(PACKAGE);

        dependency.setPackageSource(true);

        assertThat(dependency.isPackageSource()).isTrue();
    }

    @Test
    void throws_setSourceButNoPackage() {
        assertThatThrownBy(()->dependency.setPackageSource(true))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("no package definition");
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
