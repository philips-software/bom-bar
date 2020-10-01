/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalTest {
    private static final Term TERM = new Term("Tag", "Description");

    @Test
    void createsUnguardedInstance() {
        final var term = new Conditional<>(TERM);

        assertThat(term.get()).isEqualTo(TERM);
        assertThat(term.get(Condition.LOW)).contains(TERM);
    }

    @Test
    void passesConditional_noConditionProvided() {
        final var term = new Conditional<>(TERM, Condition.LOW);

        assertThat(term.get()).isEqualTo(TERM);
    }

    @Test
    void ignores_differentCondition() {
        final var term = new Conditional<>(TERM, Condition.LOW);

        assertThat(term.get(Other.LEFT)).isEmpty();
        assertThat(term.get(Other.RIGHT)).isEmpty();
    }

    @Test
    void passes_conditionAtLeastMinimalCondition() {
        final var term = new Conditional<>(TERM, Condition.MID);

        assertThat(term.get(Condition.LOW)).isEmpty();
        assertThat(term.get(Condition.MID)).contains(TERM);
        assertThat(term.get(Condition.HIGH)).contains(TERM);
    }

    @Test
    void passes_allConditionsAreMet() {
        final var term = new Conditional<>(TERM, Condition.HIGH, Other.LEFT);

        assertThat(term.get(Condition.HIGH)).isEmpty();
        assertThat(term.get(Other.LEFT)).isEmpty();
        assertThat(term.get(Condition.LOW, Other.LEFT)).isEmpty();
        assertThat(term.get(Condition.HIGH, Other.RIGHT)).contains(TERM);
    }

    enum Condition {LOW, MID, HIGH}

    enum Other {LEFT, RIGHT}
}
