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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Container to access licenses and their term by name.
 */
public class LicenseRegistry {
    private final Map<String, Term> terms = new HashMap<>();
    private final Map<String, LicenseType> licenses = new HashMap<>();

    /**
     * @return collection of all defined terms
     */
    public Collection<Term> getTerms() {
        return terms.values();
    }

    /**
     * @return alphabetic list of registered licenses
     */
    public Collection<String> getLicenses() {
        return licenses.values().stream()
                .map(LicenseType::getIdentifier)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * Defines an term by tag name.
     *
     * @return the created term
     * @throws IllegalArgumentException when the tag already exists
     */
    public Term term(String tag, String description) {
        validateUniqueness(terms, tag);
        final var attr = new Term(tag, description);
        terms.put(tag.toLowerCase(), attr);
        return attr;
    }

    /**
     * Defines a license by its identifier.
     *
     * @param license identifier of the license
     * @return builder to add terms
     * @throws IllegalArgumentException when the license already exists
     */
    public LicenseBuilder license(String license) {
        return newLicenseBuilder(license, new LicenseType(license));
    }

    /**
     * Defines a child license by its identifier.
     *
     * @param license identifier of the license
     * @param parent  the parent license
     * @return builder to add terms
     * @throws IllegalArgumentException when the license already exists
     */
    public LicenseBuilder license(String license, LicenseBuilder parent) {
        final var type = new LicenseType(license, parent.type);
        return newLicenseBuilder(license, type);
    }

    /**
     * Defines a new license as exception on a base license.
     * The new license typically overrides terms of the base license
     *
     * @param exception exception identifier
     * @param parent    Base license  to extend
     * @return builder to additional terms
     * @throws IllegalArgumentException when the license already exists
     */
    public LicenseBuilder with(String exception, LicenseBuilder parent) {
        return license(parent.type.getIdentifier() + " WITH " + exception, parent);
    }

    private LicenseBuilder newLicenseBuilder(String license, LicenseType type) {
        validateUniqueness(licenses, license);
        licenses.put(license.toLowerCase(), type);
        return new LicenseBuilder(type);
    }

    private <T> void validateUniqueness(Map<String, T> map, String key) {
        T value = map.get(key.toLowerCase());
        if (value != null) {
            throw new IllegalArgumentException("Trying to register a duplicate: '" + value + "'");
        }
    }

    /**
     * @param license name of the license
     * @return license metadata
     * @throws IllegalArgumentException when license is unknown in the registry
     */
    LicenseType licenseType(String license) {
        return getKnownItem(licenses, license);
    }

    private <T> T getKnownItem(Map<String, T> map, String key) {
        T value = map.get(key.toLowerCase());
        if (value == null) {
            throw new IllegalArgumentException("Unknown reference: '" + key + "'");
        }
        return value;
    }

    /**
     * License builder to add (conditional) terms by tag name to a license.
     */
    public class LicenseBuilder {
        private final LicenseType type;

        private LicenseBuilder(LicenseType type) {
            this.type = type;
        }

        /**
         * Adds a required term.
         *
         * @param term  tag of the term
         * @param guard minimal condition(s) for the term
         * @throws IllegalArgumentException when the term is unknown
         */
        public LicenseBuilder require(String term, Enum<?>... guard) {
            type.require(getKnownItem(terms, term), guard);
            return this;
        }

        /**
         * Makes license (weak) copyleft.
         *
         * @param guard minimal conditions for weak copyleft
         */
        public LicenseBuilder copyleft(Enum<?>... guard) {
            return copyleft(this, guard);
        }

        /**
         * Makes license (weak) copyleft for another license.
         *
         * @param guard minimal conditions for weak copyleft
         */
        public LicenseBuilder copyleft(LicenseBuilder license, Enum<?>... guard) {
            type.demand(Term.from(license.type), guard);
            accept(license);
            return this;
        }

        /**
         * Add a conditional demand to this license.
         *
         * @param term  tag of the term
         * @param guard minimal condition for the demand
         */
        public LicenseBuilder demand(String term, Enum<?>... guard) {
            type.demand(getKnownItem(terms, term), guard);
            return this;
        }

        /**
         * Makes the license accept a copyleft license demand.
         *
         * @param licenses builders for the accepted licenses.
         */
        public LicenseBuilder accept(LicenseBuilder... licenses) {
            for (var license : licenses) {
                type.accept(Term.from(license.type));
            }
            return this;
        }

        /**
         * Makes the license accept a term demand.
         *
         * @param term tag of the term
         */
        public LicenseBuilder accept(String term) {
            type.accept(getKnownItem(terms, term));
            return this;
        }

        /**
         * Makes license compatible with a target license.
         */
        public LicenseBuilder compatibleWith(LicenseBuilder... targets) {
            for (var target : targets) {
                target.accept(this);
            }
            return this;
        }
    }
}
