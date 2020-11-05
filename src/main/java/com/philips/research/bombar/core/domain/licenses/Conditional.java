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

import java.util.Arrays;
import java.util.Optional;

/**
 * Value that is guarded by the ordinal values of an enum.
 * The value is exposed if the enum is at least its guard value.
 *
 * @param <T> Type of the value held by the condition
 */
class Conditional<T> {
    private final T value;
    private final Enum<?>[] guards;

    /**
     * Creates a guarded value.
     *
     * @param guard minimal enum for the value to be exposed
     */
    Conditional(T value, Enum<?>... guard) {
        long count = Arrays.stream(guard).map(Enum::getClass).distinct().count();
        if (guard.length != count) {
            throw new IllegalArgumentException("Conditions include multiple guards of the same type");
        }

        this.value = value;
        this.guards = guard;
    }

    public T getValue() {
        return value;
    }

    /**
     * @return the value, unless any condition fails a guard
     */
    Optional<T> get(Enum<?>... conditions) {
        return Arrays.stream(conditions).anyMatch(this::isFailing)
                ? Optional.empty()
                : Optional.of(value);
    }

    boolean isFailing(Enum<?> condition) {
        final var clazz = condition.getClass();
        return Arrays.stream(guards)
                .filter(clazz::isInstance)
                .anyMatch(g -> condition.ordinal() < g.ordinal());
    }
}
