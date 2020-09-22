package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LicenseChecker {
    private final LicenseRegistry registry;

    public LicenseChecker(LicenseRegistry registry) {
        this.registry = registry;
    }

    public List<LicenseViolation> verify(Project project) {
        return project.getPackages().stream()
                .flatMap(pkg -> verify(project, pkg))
                .collect(Collectors.toList());
    }

    private Stream<LicenseViolation> verify(Project project, Package pkg) {
        final var license = pkg.getLicense();
        if (license == null || license.isBlank()) {
            return Stream.of(new LicenseViolation(pkg, "has no license"));
        }
        if (license.toLowerCase().contains(" or ")) {
            return Stream.of(new LicenseViolation(pkg, String.format("has alternative licenses '%s'", license)));
        }

        return verifyPackage(pkg, project.getDistribution());
    }

    private Stream<LicenseViolation> verifyPackage(Package pkg, Project.Distribution distribution) {
        final var violations = Stream.<LicenseViolation>builder();
        for (var license : licenses(pkg)) {
            try {
                verifyChildren(pkg, license, distribution, violations);
            } catch (IllegalArgumentException e) {
                violations.add(new LicenseViolation(pkg, String.format("has unknown license '%s'", license)));
            }
        }
        return violations.build();
    }

    private void verifyChildren(Package pkg, String lic, Project.Distribution distribution, Stream.Builder<LicenseViolation> violations) {
        final var type = registry.licenseType(lic);
        final Set<Attribute> required = new HashSet<>();
        final Set<Attribute> denied = new HashSet<>();

        for (var link : pkg.getChildren()) {
            final var other = link.getPackage();
            final var relation = link.getRelation();

            for (var otherLicense : licenses(other)) {
                try {
                    final var otherType = registry.licenseType(otherLicense);
                    if (!type.conflicts(otherType, distribution, relation).isEmpty()) {
                        violations.add(new LicenseViolation(pkg,
                                String.format("license '%s' is not compatible with license '%s' of package %s",
                                        lic, otherLicense, other)));
                    }
                    required.addAll(otherType.requiredGiven(distribution, relation));
                    denied.addAll(otherType.deniedGiven(distribution, relation));
                } catch (IllegalArgumentException e) {
                    // Ignore because unknown licenses are caught at top level
                }
            }
        }

        required.retainAll(denied);
        if (!required.isEmpty()) {
            violations.add(new LicenseViolation(pkg, "contains a conflict between its direct subpackages"));
        }
    }

    private String[] licenses(Package other) {
        return other.getLicense()
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\s+(AND|and|OR|or)\\s+");
    }
}
