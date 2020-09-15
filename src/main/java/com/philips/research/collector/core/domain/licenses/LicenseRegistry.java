package com.philips.research.collector.core.domain.licenses;

import java.util.*;
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
        throwIfDuplicate(attributes, tag);
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
        final var parentType = getOrThrowIfUnknown(licenses, parent);
        final var type = new LicenseType(license, parentType);
        return newLicenseBuilder(license, type);
    }

    private LicenseBuilder newLicenseBuilder(String license, LicenseType type) {
        throwIfDuplicate(licenses, license);
        licenses.put(license.toLowerCase(), type);
        return new LicenseBuilder(type);
    }

    private <T> void throwIfDuplicate(Map<String, T> map, String key) {
        T value = map.get(key.toLowerCase());
        if (value != null) {
            throw new IllegalArgumentException("Trying to register a duplicate: '" + value + "'");
        }
    }

    private <T> T getOrThrowIfUnknown(Map<String, T> map, String key) {
        T value = map.get(key.toLowerCase());
        if (value == null) {
            throw new IllegalArgumentException("Unknown reference: '" + key + "'");
        }
        return value;
    }

    /**
     * Starts an evaluation of a collection of licenses.
     *
     * @param license    identifier of the first license
     * @param conditions applicable attribute conditions
     * @return builder to evaluate a collection of licenses
     * @throws IllegalArgumentException when the license is not defined
     */
    public Evaluation evaluate(String license, Enum<?>... conditions) {
        final var type = getOrThrowIfUnknown(licenses, license);
        return new Evaluation(type, conditions);
    }

    /**
     * Conditionally compares two licenses.
     *
     * @param license    identifier of a license
     * @param other      identifier of a license
     * @param conditions applicable attribute conditions
     * @return all attributes that are in violation
     */
    public Set<Attribute> compare(String license, String other, Enum<?>... conditions) {
        final var left = getOrThrowIfUnknown(licenses, license);
        final var right = getOrThrowIfUnknown(licenses, other);
        return left.conflicts(right, conditions);
    }

    public static class Violation {
        private final String context;
        private final String license;
        private final Set<Attribute> attributes;

        public Violation(String context, String license, Set<Attribute> attributes) {
            this.context = context;
            this.license = license;
            this.attributes = attributes;
        }

        public String getPackage() {
            return context;
        }

        public String getLicense() {
            return license;
        }

        public Set<Attribute> getAttributes() {
            return attributes;
        }

        @Override
        public String toString() {
            return String.format("pkg:%s (%s): %s", context, license, attributes);
        }
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
            type.require(getOrThrowIfUnknown(attributes, attribute), guard);
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
            type.deny(getOrThrowIfUnknown(attributes, attribute), guard);
            return this;
        }
    }

    /**
     * License evaluation builder.
     *
     * @see LicenseRegistry#evaluate(String, Enum[])
     */
    public class Evaluation {
        private final LicenseType type;
        private final List<Enum<?>> conditions;
        private final List<Violation> violations = new ArrayList<>();
        private final Set<Attribute> requires = new HashSet<>();
        private final Set<Attribute> denies = new HashSet<>();

        private Evaluation(LicenseType type, Enum<?>[] conditions) {
            this.type = type;
            this.conditions = Arrays.asList(conditions);
        }

        /**
         * Adds another license to the evaluation.
         *
         * @param license    identifier of the license
         * @param conditions conditions to apply to the attributes of the license
         * @throws IllegalArgumentException when the license is unknown
         */
        Evaluation and(String context, String license, Enum<?>... conditions) {
            final var type = getOrThrowIfUnknown(licenses, license);
            final var allConditions = allConditions(conditions);

            requires.addAll(type.requires(allConditions));
            denies.addAll(type.denies(allConditions));

            final var conflicts = this.type.conflicts(type, allConditions);
            if (!conflicts.isEmpty()) {
                violations.add(new Violation(context, license, conflicts));
            }

            return this;
        }

        private Enum<?>[] allConditions(Enum<?>[] conditions) {
            final var temp = new ArrayList<>(Arrays.asList(conditions));
            temp.addAll(this.conditions);
            return temp.toArray(new Enum<?>[0]);
        }

        /**
         * @return the aggregated violations
         */
        List<Violation> getViolations() {
            return violations;
        }

        /**
         * @return the attributes required by combined licenses
         */
        Set<Attribute> requires() {
            return requires;
        }

        /**
         * @return the violations raised by the combined licenses
         */
        Set<Attribute> incompatibilities() {
            // TODO This is destructive for the denis set; is that a problem?
            denies.retainAll(requires);
            return denies;
        }
    }
}
