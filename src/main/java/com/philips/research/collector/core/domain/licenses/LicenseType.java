package com.philips.research.collector.core.domain.licenses;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * License definition.
 */
class LicenseType {
    private final String identifier;
    private final LicenseType parent;
    private final Set<Conditional<Attribute>> require = new HashSet<>();
    private final Set<Conditional<Attribute>> deny = new HashSet<>();

    LicenseType(String identifier) {
        this(identifier, null);
    }

    LicenseType(String identifier, LicenseType parent) {
        this.identifier = identifier;
        this.parent = parent;
    }

    String getIdentifier() {
        return identifier;
    }

    /**
     * Adds conditional required attributes.
     *
     * @param guards (combination of) minimal enum value(s) for the attribute to be required
     */
    LicenseType require(Attribute attribute, Enum<?>... guards) {
        require.add(new Conditional<>(attribute, guards));
        return this;
    }

    /**
     * Adds conditional denied attributes.
     *
     * @param guards (combination of) minimal enum value(s) for the attribute to be denied
     */
    LicenseType deny(Attribute attribute, Enum<?>... guards) {
        deny.add(new Conditional<>(attribute, guards));
        return this;
    }

    /**
     * Conditionally compares the attributes of this license with another license.
     *
     * @param other      the license to compare with
     * @param conditions the condition(s) for the attributes of both licenses
     * @return all conflicting attributes
     */
    Set<Attribute> conflicts(LicenseType other, Enum<?>... conditions) {
        final var requires = requiredGiven(conditions);
        requires.addAll(other.requiredGiven(conditions));

        final var denies = deniedGiven(conditions);
        denies.addAll(other.deniedGiven(conditions));

        denies.retainAll(requires);
        return denies;
    }

    /**
     * @param conditions the applicable attribute condition(s)
     * @return all required attributes under the given conditions
     */
    Set<Attribute> requiredGiven(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.require, conditions);
    }

    /**
     * @param conditions the applicable attribute condition(s)
     * @return all denied attributes under the given conditions
     */
    Set<Attribute> deniedGiven(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.deny, conditions);
    }

    private Set<Attribute> merged(Set<Attribute> result, Function<LicenseType, Set<Conditional<Attribute>>> set, Enum<?>[] conditions) {
        result.addAll(attributes(set.apply(this), conditions));
        if (parent != null) {
            result.addAll(parent.merged(result, set, conditions));
        }
        return result;
    }

    private Set<Attribute> attributes(Set<Conditional<Attribute>> attributes, Enum<?>... conditions) {
        return attributes.stream()
                .flatMap(attr -> attr.get(conditions).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return identifier;
    }
}
