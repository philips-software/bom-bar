package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseTypeTest {
    private static final String NAME = "Name";
    private static final Attribute ATTR_A = new Attribute("A", "Attribute A");
    private static final Attribute ATTR_B = new Attribute("B", "Attribute B");
    private static final Attribute ATTR_C = new Attribute("C", "Attribute C");

    @Test
    void createsInstance() {
        final var type = new LicenseType(NAME)
                .require(ATTR_A)
                .deny(ATTR_B);

        assertThat(type.getIdentifier()).isEqualTo(NAME);
        assertThat(type.requiredGiven()).containsExactly(ATTR_A);
        assertThat(type.deniedGiven()).containsExactly(ATTR_B);
    }

    @Test
    void createsInheritedInstance() {
        final var type = new LicenseType("Parent")
                .require(ATTR_A)
                .deny(ATTR_B);
        final var child = new LicenseType("Child", type);

        final var derived = new LicenseType(NAME, child);

        assertThat(derived.getIdentifier()).isEqualTo(NAME);
        assertThat(derived.requiredGiven()).containsExactly(ATTR_A);
        assertThat(derived.deniedGiven()).containsExactly(ATTR_B);
    }

    @Test
    void filtersRequiredAttributes() {
        final var type = new LicenseType(NAME)
                .require(ATTR_A)
                .require(ATTR_B, Condition.MID);

        assertThat(type.requiredGiven()).containsExactlyInAnyOrder(ATTR_A);
        assertThat(type.requiredGiven(Condition.LOW)).containsExactly(ATTR_A);
        assertThat(type.requiredGiven(Condition.HIGH)).containsExactlyInAnyOrder(ATTR_A, ATTR_B);
    }

    @Test
    void filtersDeniedAttributes() {
        final var type = new LicenseType(NAME)
                .deny(ATTR_A)
                .deny(ATTR_B, Condition.MID);

        assertThat(type.deniedGiven()).containsExactlyInAnyOrder(ATTR_A);
        assertThat(type.deniedGiven(Condition.LOW)).containsExactly(ATTR_A);
        assertThat(type.deniedGiven(Condition.HIGH)).containsExactlyInAnyOrder(ATTR_A, ATTR_B);
    }

    @Test
    void listsConditionalConflictsWithOtherLicense() {
        final var type = new LicenseType(NAME)
                .require(ATTR_A, Condition.LOW)
                .require(ATTR_B, Condition.LOW)
                .deny(ATTR_C, Condition.HIGH);
        final var other = new LicenseType(NAME)
                .require(ATTR_A, Condition.LOW)
                .deny(ATTR_B, Condition.LOW)
                .require(ATTR_C, Condition.HIGH);

        final var conflicts = type.conflicts(other, Condition.MID);

        assertThat(conflicts).containsExactly(ATTR_B);
    }

    private enum Condition {LOW, MID, HIGH}
}
