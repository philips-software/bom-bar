/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyTest {
    private static final PackageDefinition PACKAGE = new PackageDefinition("Reference");
    private static final String VERSION = "Version";
    private static final String LICENSE = "License";

    private final Dependency dependency = new Dependency(PACKAGE, VERSION);

    @Test
    void createsInstance() {
        assertThat(dependency.getPackage()).contains(PACKAGE);
        assertThat(dependency.getVersion()).isEqualTo(VERSION);
        assertThat(dependency.getLicense()).isEmpty();
        assertThat(dependency.getRelations()).isEmpty();
    }

    @Test
    void createsAnonymousInstance() {
        final var anonymous = new Dependency(null, VERSION);

        assertThat(anonymous.getPackage()).isEmpty();
    }

    @Test
    void updatesLicense() {
        dependency.setLicense(LICENSE);

        assertThat(dependency.getLicense()).contains(LICENSE);
    }

    @Test
    void addsRelations() {
        final var target = new Dependency(PACKAGE, "Child");
        final var relation = new Relation(Relation.Type.STATIC_LINK, target);

        dependency.addRelation(relation);

        final var dependencies = dependency.getRelations();

        assertThat(dependencies).containsExactly(relation);
    }

    @Test
    void indicatesPackageVersionMatch() {
        assertThat(dependency.isEqualTo(PACKAGE, VERSION)).isTrue();
        assertThat(new Dependency(null, VERSION).isEqualTo(PACKAGE, VERSION)).isFalse();
    }

    @Test
    void implementsComparable() {
        final var aaa = new PackageDefinition("A");
        final var bbb = new PackageDefinition("B");
        final var depNull = new Dependency(null, VERSION);
        final var depAaa = new Dependency(aaa, "1");
        final var depBbb = new Dependency(bbb, "1");
        final var depAaa2 = new Dependency(aaa, "2");

        assertThat(depNull).isEqualByComparingTo(depNull);
        assertThat(depNull).isLessThan(depAaa);
        assertThat(depAaa).isGreaterThan(depNull);
        assertThat(depBbb).isGreaterThan(depAaa);
        assertThat(depAaa).isLessThan(depBbb);
        assertThat(depAaa).isLessThan(depAaa2);
        assertThat(depAaa).isLessThan(depAaa2);
        assertThat(depAaa).isLessThan(depAaa2);
    }
}
