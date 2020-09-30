/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Container to access licenses and their attributes by name.
 */
public class LicenseRegistry {
    private final Map<String, Attribute> attributes = new HashMap<>();
    private final Map<String, LicenseType> licenses = new HashMap<>();

    /**
     * @return collection of all defined attributes
     */
    public Collection<Attribute> getAttributes() {
        return attributes.values();
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
     * Defines an attribute by tag name.
     *
     * @return the created attribute
     * @throws IllegalArgumentException when the tag already exists
     */
    public Attribute attribute(String tag, String description) {
        validateUniqueness(attributes, tag);
        final var attr = new Attribute(tag, description);
        attributes.put(tag.toLowerCase(), attr);
        return attr;
    }

    /**
     * Defines a license by its identifier.
     *
     * @param license identifier of the license
     * @return builder to add attributes
     * @throws IllegalArgumentException when the license already exists
     */
    public LicenseBuilder license(String license) {
        return newLicenseBuilder(license, new LicenseType(license));
    }

    /**
     * Defines a child license by its identifier.
     *
     * @param license identifier of the license
     * @param parent  identifier of the parent license
     * @return builder to add attributes
     * @throws IllegalArgumentException when the license already exists
     */
    public LicenseBuilder license(String license, String parent) {
        final var parentType = getKnownItem(licenses, parent);
        final var type = new LicenseType(license, parentType);
        return newLicenseBuilder(license, type);
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
     * License builder to add (conditional) attributes by tag name to a license.
     */
    public class LicenseBuilder {
        private final LicenseType type;

        private LicenseBuilder(LicenseType type) {
            this.type = type;
        }

        /**
         * Adds a required attribute
         *
         * @param attribute tag of the attribute
         * @param guard     minimal condition(s) for the attribute
         * @throws IllegalArgumentException when the attribute is unknown
         */
        public LicenseBuilder require(String attribute, Enum<?>... guard) {
            type.require(getKnownItem(attributes, attribute), guard);
            return this;
        }

        /**
         * Adds a denied attribute
         *
         * @param attribute tag of the attribute
         * @param guard     minimal condition(s) for the attribute
         * @throws IllegalArgumentException when the attribute is unknown
         */
        public LicenseBuilder deny(String attribute, Enum<?>... guard) {
            type.deny(getKnownItem(attributes, attribute), guard);
            return this;
        }
    }
}
