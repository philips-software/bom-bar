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
    private final Set<Conditional<Term>> demands = new HashSet<>();
    private final Set<Term> accepts = new HashSet<>();

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
     * Adds conditional demand terms.
     *
     * @param guards (combination of) minimal enum value(s) for the term to be demanded
     */
    LicenseType demand(Term term, Enum<?>... guards) {
        demands.add(new Conditional<>(term, guards));
        return this;
    }

    /**
     * Adds accept terms.
     */
    LicenseType accept(Term term) {
        accepts.add(term);
        return this;
    }

    /**
     * Determines the conditional incompatible terms of a another license.
     *
     * @param other      the license to check
     * @param conditions the condition(s) for the terms
     * @return all conflicting terms
     */
    Set<Term> incompatibilities(LicenseType other, Enum<?>... conditions) {
        final var demands = other.demandsGiven(conditions);
        demands.removeAll(accepts());
        return demands;
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
     * @return all demands under the given conditions
     */
    Set<Term> demandsGiven(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.demands, conditions);
    }

    private Set<Term> merged(Set<Term> result, Function<LicenseType, Set<Conditional<Term>>> set, Enum<?>[] conditions) {
        result.addAll(terms(set.apply(this), conditions));
        if (parent != null) {
            result.addAll(parent.merged(result, set, conditions));
        }
        return result;
    }

    /**
     * @return all accepted terms
     */
    Set<Term> accepts() {
        final var result = new HashSet<>(accepts);
        if (parent != null) {
            result.addAll(parent.accepts());
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
