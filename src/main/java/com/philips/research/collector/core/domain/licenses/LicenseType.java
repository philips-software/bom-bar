/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * License definition.
 */
class LicenseType {
    private final String identifier;
    private final @NullOr LicenseType parent;
    private final Set<Conditional<Term>> requires = new HashSet<>();
    private final Set<Conditional<Term>> forbids = new HashSet<>();

    LicenseType(String identifier) {
        this(identifier, null);
    }

    LicenseType(String identifier, @NullOr LicenseType parent) {
        this.identifier = identifier;
        this.parent = parent;
    }

    String getIdentifier() {
        return identifier;
    }

    /**
     * Adds conditional required terms.
     *
     * @param guards (combination of) minimal enum value(s) for the term to be required
     */
    LicenseType require(Term term, Enum<?>... guards) {
        requires.add(new Conditional<>(term, guards));
        return this;
    }

    /**
     * Adds conditional forbidden terms.
     *
     * @param guards (combination of) minimal enum value(s) for the term to be forbidden
     */
    LicenseType forbid(Term term, Enum<?>... guards) {
        forbids.add(new Conditional<>(term, guards));
        return this;
    }

    /**
     * Conditionally compares the terms of this license with another license.
     *
     * @param other      the license to compare with
     * @param conditions the condition(s) for the terms of both licenses
     * @return all conflicting terms
     */
    Set<Term> conflicts(LicenseType other, Enum<?>... conditions) {
        final var requires = requiredGiven(conditions);
        requires.addAll(other.requiredGiven(conditions));

        final var forbids = forbiddenGiven(conditions);
        forbids.addAll(other.forbiddenGiven(conditions));

        forbids.retainAll(requires);
        return forbids;
    }

    /**
     * @param conditions the applicable term condition(s)
     * @return all required terms under the given conditions
     */
    Set<Term> requiredGiven(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.requires, conditions);
    }

    /**
     * @param conditions the applicable term condition(s)
     * @return all forbidden term under the given conditions
     */
    Set<Term> forbiddenGiven(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.forbids, conditions);
    }

    private Set<Term> merged(Set<Term> result, Function<LicenseType, Set<Conditional<Term>>> set, Enum<?>[] conditions) {
        result.addAll(terms(set.apply(this), conditions));
        if (parent != null) {
            result.addAll(parent.merged(result, set, conditions));
        }
        return result;
    }

    private Set<Term> terms(Set<Conditional<Term>> terms, Enum<?>... conditions) {
        return terms.stream()
                .flatMap(attr -> attr.get(conditions).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return identifier;
    }
}
