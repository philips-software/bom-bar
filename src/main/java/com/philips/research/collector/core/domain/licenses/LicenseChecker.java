/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

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

    public List<LicenseViolation> verify() {
        violations.clear();
        project.getPackages().forEach(this::termsFromCache);
        return violations;
    }

    private Aggregate termsFromCache(Package pkg) {
        var terms = cache.get(pkg);
        if (terms == null) {
            terms = termsFor(pkg);
            cache.put(pkg, terms);
        }
        return terms;
    }

    private Aggregate termsFor(Package pkg) {
        final var terms = new Aggregate();

        final var licenses = pkg.getLicense();
        if (licenses.isBlank()) {
            violations.add(new LicenseViolation(pkg, "has no license"));
        } else if (licenses.toLowerCase().contains(" or ")) {
            violations.add(new LicenseViolation(pkg, String.format("has alternative licenses '%s'", licenses)));
        }

        checkPackage(pkg, terms);

        return terms;
    }

    private void checkPackage(Package pkg, Aggregate terms) {
        for (var license : split(pkg.getLicense())) {
            try {
                final var type = registry.licenseType(license);
                for (Package.Link link : pkg.getChildren()) {
                    checkChild(pkg, type, link, terms);
                }
            } catch (IllegalArgumentException e) {
                violations.add(new LicenseViolation(pkg, String.format("has unknown license '%s'", license)));
            }
        }

        if (terms.hasViolation()) {
            violations.add(new LicenseViolation(pkg, "has a conflict in its subpackages"));
        }
    }

    private void checkChild(Package pkg, LicenseType type, Package.Link link, Aggregate terms) {
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
                    terms.add(childType, project.getDistribution(), relation);
                }
            } catch (IllegalArgumentException e) {
                // Ignore because unknown licenses are caught at top level
            }
        }

        if (!isViolating) {
            terms.add(termsFromCache(child));
        }
    }

    private String[] split(String license) {
        return Arrays.stream(license
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\s+(AND|and|OR|or)\\s+"))
                .filter(l -> !l.isBlank())
                .distinct()
                .toArray(String[]::new);
    }

    private static class Aggregate {
        private final Set<Term> required = new HashSet<>();
        private final Set<Term> forbidden = new HashSet<>();

        void add(LicenseType type, Enum<?>... conditions) {
            required.addAll(type.requiredGiven(conditions));
            forbidden.addAll(type.forbiddenGiven(conditions));
        }

        void add(Aggregate aggregate) {
            required.addAll(aggregate.required);
            forbidden.addAll(aggregate.forbidden);
        }

        boolean hasViolation() {
            final var intersection = new HashSet<>(forbidden);
            intersection.retainAll(required);
            return !intersection.isEmpty();
        }
    }
}
