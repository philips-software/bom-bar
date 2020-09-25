package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;

import java.util.*;

public class LicenseChecker {
    private final LicenseRegistry registry;
    private final Project project;
    private final Map<Package, Aggregate> cache = new HashMap<>();
    private final List<LicenseViolation> violations = new ArrayList<>();

    public LicenseChecker(LicenseRegistry registry, Project project) {
        this.registry = registry;
        this.project = project;
    }

    private static String[] split(String license) {
        return license
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\s+(AND|and|OR|or)\\s+");
    }

    public List<LicenseViolation> verify() {
        violations.clear();
        final var roots = new ArrayList<>(project.getPackages());
        project.getPackages().stream()
                .flatMap(pkg -> pkg.getChildren().stream())
                .forEach(link -> roots.remove(link.getPackage()));
        roots.forEach(this::attributesFor);
        return violations;
    }

    private Aggregate attributesFor(Package pkg) {
        final var attributes = new Aggregate();

        final var licenses = pkg.getLicense();
        if (licenses.isBlank()) {
            violations.add(new LicenseViolation(pkg, "has no license"));
        } else if (licenses.toLowerCase().contains(" or ")) {
            violations.add(new LicenseViolation(pkg, String.format("has alternative licenses '%s'", licenses)));
        } else {
            checkPackage(pkg, attributes);
        }

        return attributes;
    }

    private void checkPackage(Package pkg, Aggregate attributes) {
        for (var license : split(pkg.getLicense())) {
            try {
                final var type = registry.licenseType(license);
                for (Package.Link link : pkg.getChildren()) {
                    checkChild(pkg, type, link, attributes);
                }
            } catch (IllegalArgumentException e) {
                violations.add(new LicenseViolation(pkg, String.format("has unknown license '%s'", license)));
            }
        }

        if (attributes.hasViolation()) {
            violations.add(new LicenseViolation(pkg, "has a conflict in its subpackages"));
        }
    }

    private void checkChild(Package pkg, LicenseType type, Package.Link link, Aggregate attributes) {
        final var relation = link.getRelation();
        final var child = link.getPackage();
        var isViolating = false;

        for (var childLicense : split(child.getLicense())) {
            try {
                final var childType = registry.licenseType(childLicense);
                if (!type.conflicts(childType, project.getDistribution(), relation).isEmpty()) {
                    violations.add(new LicenseViolation(pkg,
                            String.format("license '%s' is not compatible with license '%s' of package %s",
                                    type, childType, child)));
                    isViolating = true;
                } else {
                    attributes.add(childType, project.getDistribution(), relation);
                }
            } catch (IllegalArgumentException e) {
                // Ignore because unknown licenses are caught at top level
            }
        }

        if (!isViolating) {
            var attrs = cache.get(child);
            if (attrs == null) {
                attrs = attributesFor(child);
                cache.put(pkg, attrs);
            }
            attributes.add(attrs);
        }
    }

    private static class Aggregate {
        private final Set<Attribute> required = new HashSet<>();
        private final Set<Attribute> denied = new HashSet<>();

        void add(LicenseType type, Enum<?>... conditions) {
            required.addAll(type.requiredGiven(conditions));
            denied.addAll(type.deniedGiven(conditions));
        }

        void add(Aggregate aggregate) {
            required.addAll(aggregate.required);
            denied.addAll(aggregate.denied);
        }

        boolean hasViolation() {
            final var intersection = new HashSet<>(denied);
            intersection.retainAll(required);
            return !intersection.isEmpty();
        }
    }
}
