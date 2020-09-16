package com.philips.research.collector.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageTest {

    private static final String REFERENCE = "Reference";
    private static final String VERSION = "Version";

    private final Package pkg = new Package(REFERENCE, VERSION);

    @Test
    void createsInstance() {
        assertThat(pkg.getReference()).isEqualTo(REFERENCE);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
        assertThat(pkg.getTitle()).isEqualTo(REFERENCE);
    }

    @Test
    void addsChild() {
        final var child = new Package("Child", VERSION);

        pkg.addChild(child, Package.Relation.STATIC_LINK);
        pkg.addChild(child, Package.Relation.DYNAMIC_LINK);

        assertThat(pkg.getChildren()).hasSize(2);
        assertThat(pkg.getChildren().get(0).getPackage()).isEqualTo(child);
        assertThat(pkg.getChildren().get(0).getRelation()).isEqualTo(Package.Relation.STATIC_LINK);
        assertThat(pkg.getChildren().get(1).getRelation()).isEqualTo(Package.Relation.DYNAMIC_LINK);
    }
}
