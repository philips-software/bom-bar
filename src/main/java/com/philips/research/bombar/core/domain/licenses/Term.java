/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import java.util.Collection;
import java.util.Objects;

/**
 * Named license term with a description.
 */
final class Term {
    private final Object key;
    private final String description;

    Term(Object key, String description) {
        this.key = key;
        this.description = description;
    }

    static Term from(LicenseType license) {
        return new Term(license, "Copyleft license '" + license.getIdentifier() + "'");
    }

    Object getKey() {
        return key;
    }

    String getDescription() {
        return description;
    }

    /**
     * @return true if term matches the key of any provided term,
     * or when the key is a license: if the license is an ancestor of any term.
     */
    boolean isMatching(Collection<Term> terms) {
        final var key = getKey();
        return terms.stream().anyMatch(term -> (key instanceof LicenseType)
                ? matchesAncestor(terms, (LicenseType) key)
                : (Objects.equals(getKey(), term.getKey())));
    }

    private boolean matchesAncestor(Collection<Term> terms, LicenseType license) {
        return terms.stream()
                .map(Term::getKey)
                .filter(k -> k instanceof LicenseType)
                .map(k -> (LicenseType) k)
                .anyMatch(l -> l.hasAncestor(license));
    }

    @Override
    public String toString() {
        return String.format("%s: %s", key, description);
    }
}
