package com.philips.research.collector.core.domain;

import java.util.Comparator;
import java.util.Objects;

public final class PackageId implements Comparable<PackageId> {
    private final String type;
    private final String namespace;
    private final String name;

    public PackageId(String type, String namespace, String name) {
        this.type = type;
        this.namespace = namespace;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageId packageId = (PackageId) o;
        return Objects.equals(type, packageId.type) &&
                Objects.equals(namespace, packageId.namespace) &&
                Objects.equals(name, packageId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, namespace, name);
    }

    @Override
    public int compareTo(PackageId other) {
        return Comparator.comparing(PackageId::getType)
                .thenComparing(PackageId::getNamespace)
                .thenComparing(PackageId::getName)
                .compare(this, other);
    }

    @Override
    public String toString() {
        return type + "/" + namespace + "/" + name;
    }
}
