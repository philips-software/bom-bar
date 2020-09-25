package com.philips.research.collector.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
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

    @Test
    void removesChild() {
        final var child1 = new Package("Child 1", VERSION);
        final var child2 = new Package("Child 2", VERSION);
        pkg.addChild(child1, Package.Relation.STATIC_LINK);
        pkg.addChild(child2, Package.Relation.DYNAMIC_LINK);
        pkg.addChild(child1, Package.Relation.SOURCE_CODE);

        pkg.removeChild(child1);

        assertThat(pkg.getChildren()).hasSize(1);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Package.class)
                .withOnlyTheseFields("reference", "version")
                .withPrefabValues(Package.class, new Package("Red", VERSION), new Package("Blue", VERSION))
                .verify();
    }
}
