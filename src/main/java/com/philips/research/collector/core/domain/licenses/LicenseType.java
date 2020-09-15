package com.philips.research.collector.core.domain.licenses;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * License definition.
 */
class LicenseType {
    private final String identifier;
    private final LicenseType parent;
    private final Set<Conditional<Attribute>> require = new HashSet<>();
    private final Set<Conditional<Attribute>> deny = new HashSet<>();

    public LicenseType(String identifier) {
        this(identifier, null);
    }

    public LicenseType(String identifier, LicenseType parent) {
        this.identifier = identifier;
        this.parent = parent;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Adds conditional required attributes.
     *
     * @param guards minimal enum value(s) for the attribute to be applicable
     */
    public LicenseType require(Attribute attribute, Enum<?>... guards) {
        require.add(new Conditional<>(attribute, guards));
        return this;
    }

    /**
     * Adds conditional denied attributes.
     *
     * @param guards minimal enum value(s) for the attribute to be applicable
     */
    public LicenseType deny(Attribute attribute, Enum<?>... guards) {
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
    public Set<Attribute> conflicts(LicenseType other, Enum<?>... conditions) {
        final var requires = requires(conditions);
        requires.addAll(other.requires(conditions));

        final var denies = denies(conditions);
        denies.addAll(other.denies(conditions));

        denies.retainAll(requires);
        return denies;
    }

    /**
     * @param conditions the applicable attribute condition(s)
     * @return all required attributes under the given conditions
     */
    public Set<Attribute> requires(Enum<?>... conditions) {
        return merged(new HashSet<>(), (type) -> type.require, conditions);
    }

    /**
     * @param conditions the applicable attribute condition(s)
     * @return all denied attributes under the given conditions
     */
    public Set<Attribute> denies(Enum<?>... conditions) {
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
                .flatMap(attr -> conditions.length == 0
                        ? Stream.of(attr.get())
                        : Arrays.stream(conditions).flatMap(c -> attr.get(c).stream())
                )
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return identifier;
    }
}
