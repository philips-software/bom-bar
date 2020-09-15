package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalTest {
    private static final Attribute ATTRIBUTE = new Attribute("Tag", "Description");

    @Test
    void createsUnguardedInstance() {
        final var attr = new Conditional<>(ATTRIBUTE);

        assertThat(attr.get()).isEqualTo(ATTRIBUTE);
        assertThat(attr.get(Condition.LOW)).contains(ATTRIBUTE);
    }

    @Test
    void passesConditional_noConditionProvided() {
        final var attr = new Conditional<>(ATTRIBUTE, Condition.LOW);

        assertThat(attr.get()).isEqualTo(ATTRIBUTE);
    }

    @Test
    void ignores_differentCondition() {
        final var attr = new Conditional<>(ATTRIBUTE, Condition.LOW);

        assertThat(attr.get(Other.LEFT)).isEmpty();
        assertThat(attr.get(Other.RIGHT)).isEmpty();
    }

    @Test
    void passes_conditionAtLeastMinimalCondition() {
        final var attr = new Conditional<>(ATTRIBUTE, Condition.MID);

        assertThat(attr.get(Condition.LOW)).isEmpty();
        assertThat(attr.get(Condition.MID)).contains(ATTRIBUTE);
        assertThat(attr.get(Condition.HIGH)).contains(ATTRIBUTE);
    }

    @Test
    void passes_allConditionsAreMet() {
        final var attr = new Conditional<>(ATTRIBUTE, Condition.HIGH, Other.LEFT);

        assertThat(attr.get(Condition.HIGH)).isEmpty();
        assertThat(attr.get(Other.LEFT)).isEmpty();
        assertThat(attr.get(Condition.LOW, Other.LEFT)).isEmpty();
        assertThat(attr.get(Condition.HIGH, Other.RIGHT)).contains(ATTRIBUTE);
    }

    enum Condition {LOW, MID, HIGH}

    enum Other {LEFT, RIGHT}
}
