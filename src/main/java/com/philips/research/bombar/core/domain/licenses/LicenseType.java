/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Collection;
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
    private final Set<Enum<Licenses.Requisite>[]> obligations = new HashSet<>();

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
     * @return true if the given license is an ancestor of this licens.
     */
    boolean hasAncestor(LicenseType license) {
        return this.equals(license) || (parent != null && parent.hasAncestor(license));
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

    LicenseType obligates(Enum<Licenses.Requisite>... requisites) {
        obligations.add(requisites);
        return this;
    }

    /**
     * Determines the conditional incompatible terms of a another license.
     *
     * @param other      the license to check
     * @param conditions the condition(s) for the terms
     * @return all conflicting terms
     */
    Set<Term> unmetDemands(LicenseType other, Enum<?>... conditions) {
        final var demands = other.demandsGiven(conditions);
        demands.removeIf(term -> term.isMatching(accepts()));
        return demands;
    }

    /**
     * @param conditions the applicable term condition(s)
     * @return all required terms under the given conditions
     */
    Set<Term> requiresGiven(Enum<?>... conditions) {
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
        if (parent != null) {
            result.addAll(parent.merged(result, set, conditions));
        }
        removeConditionTerms(result, set.apply(this));
        result.addAll(terms(set.apply(this), conditions));
        return result;
    }

    private void removeConditionTerms(Set<Term> from, Collection<Conditional<Term>> conditions) {
        final var terms = conditions.stream().map(Conditional::getValue).collect(Collectors.toList());
        from.removeIf(t -> t.isMatching(terms));
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
