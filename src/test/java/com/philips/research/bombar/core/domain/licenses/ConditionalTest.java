/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConditionalTest {
    private static final String VALUE = "Value";

    @Test
    void createsUnguardedInstance() {
        final var conditional = new Conditional<>(VALUE);

        assertThat(conditional.getValue()).isEqualTo(VALUE);
        assertThat(conditional.get()).contains(VALUE);
        assertThat(conditional.get(Condition.LOW)).contains(VALUE);
    }

    @Test
    void throws_multipleGuardsOfSameType() {
        assertThatThrownBy(() -> new Conditional<>(VALUE, Condition.LOW, Condition.HIGH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same type");
    }

    @Test
    void passes_noMatchingConditionTypeProvided() {
        final var Conditional = new Conditional<>(VALUE, Condition.LOW);

        assertThat(Conditional.get()).contains(VALUE);
        assertThat(Conditional.get(Other.LEFT)).contains(VALUE);
    }

    @Test
    void passes_conditionAtLeastMinimalCondition() {
        final var conditional = new Conditional<>(VALUE, Condition.MID);

        assertThat(conditional.get(Condition.LOW)).isEmpty();
        assertThat(conditional.get(Condition.MID)).contains(VALUE);
        assertThat(conditional.get(Condition.HIGH)).contains(VALUE);
    }

    @Test
    void passes_allConditionsAreMet() {
        final var conditional = new Conditional<>(VALUE, Condition.HIGH, Other.RIGHT);

        assertThat(conditional.get(Condition.LOW, Other.LEFT)).isEmpty();
        assertThat(conditional.get(Condition.LOW, Other.RIGHT)).isEmpty();
        assertThat(conditional.get(Condition.HIGH, Other.LEFT)).isEmpty();
        assertThat(conditional.get(Condition.HIGH, Other.RIGHT)).isNotEmpty();
    }

    enum Condition {LOW, MID, HIGH}

    enum Other {LEFT, RIGHT}
}
