package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.PackageId;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageIdTest {
    private static final PackageId ID = new PackageId("Type", "NS", "Name");
    private static final PackageId CHILD_ID = new PackageId("Type", "NS", "Child");
    private static final String VERSION = "1.2.3";

    private final Package pkg = new Package(ID, VERSION);

    @Test
    void movesChildPackage() {
        final var child = new Package(CHILD_ID, VERSION);
        child.addChild(child);

        pkg.addChild(child);

        assertThat(pkg.getChildren()).containsExactly(child);
        assertThat(child.getChildren()).isEmpty();
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(PackageId.class).verify();
    }
}
