/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.spdx;

import com.philips.research.bombar.core.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class SpdxParser {
    private static final Logger LOG = LoggerFactory.getLogger(SpdxParser.class);
    private static final Map<String, Relation.Relationship> RELATIONSHIP_MAPPING = new HashMap<>();

    static {
        RELATIONSHIP_MAPPING.put("DESCENDANT_OF", Relation.Relationship.MODIFIED_CODE);
        RELATIONSHIP_MAPPING.put("STATIC_LINK", Relation.Relationship.STATIC_LINK);
        RELATIONSHIP_MAPPING.put("DYNAMIC_LINK", Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put("DEPENDS_ON", Relation.Relationship.INDEPENDENT);
    }

    private final Project project;
    private final PersistentStore store;
    private final Map<String, Dependency> dictionary = new HashMap<>(); // Key is SPDX ID
    private final List<String> relationshipDeclarations = new ArrayList<>();
    private final Map<String, String> customLicenseNames = new HashMap<>(); // Key is custom license ID
    private @NullOr SpdxPackage currentPackage;
    private @NullOr String currentLicense;

    public SpdxParser(Project project, PersistentStore store) {
        this.project = project;
        this.store = store;
    }

    public void parse(InputStream stream) {
        project.clearDependencies();
        project.setLastUpdate(Instant.now());
        new TagValueParser(this::tagValue).parse(stream);
        finish();
    }

    private void tagValue(String tag, String value) {
        switch (tag) {
            case "Created":
                project.setLastUpdate(timestamp(value));
                break;
            case "DocumentName":
                if (project.getTitle().isBlank()) {
                    project.setTitle(value);
                }
                break;
            case "PackageName":
                mergeCurrent();
                currentPackage = new SpdxPackage(value);
                break;
            case "PackageVersion":
                //noinspection ConstantConditions
                ifPackageAndValue(value, () -> currentPackage.setVersion(value));
                break;
            case "PackageLicenseConcluded":
                //noinspection ConstantConditions
                ifPackageAndValue(value, () -> currentPackage.setLicense(value));
                break;
            case "SPDXID":
                //noinspection ConstantConditions
                ifPackageAndValue(value, () -> currentPackage.setSpdxId(value));
                break;
            case "ExternalRef":
                externalRef(value);
                break;
            case "Relationship":
                relationshipDeclarations.add(value);
                break;
            case "FileName": // Start of other section
                mergeCurrent();
                break;
            case "LicenseID":
                currentLicense = null;
                ifValue(value, () -> currentLicense = value);
                break;
            case "LicenseName":
                //noinspection ConstantConditions
                ifLicenseAndValue(value, () -> customLicenseNames.put(currentLicense, value));
                break;
            default: // Ignore
        }
    }

    private Instant timestamp(String iso) {
        try {
            return Instant.parse(iso);
        } catch (Exception e) {
            LOG.warn("Encountered malformed timestamp '{}'; using current time instead", iso);
            return Instant.now();
        }
    }

    private void ifPackageAndValue(String value, Runnable task) {
        if (currentPackage != null) {
            ifValue(value, task);
        }
    }

    private void ifLicenseAndValue(String value, Runnable task) {
        if (currentLicense != null) {
            ifValue(value, task);
        }
    }

    private void ifValue(String value, Runnable task) {
        if (!"NOASSERTION".equals(value)) {
            task.run();
        }
    }

    private void externalRef(String value) {
        final var elements = value.split("\\s+");
        if (elements.length != 3) {
            throw new SpdxException("Malformed external reference value: " + value);
        }
        if (currentPackage != null && "PACKAGE-MANAGER".equals(elements[0]) && "purl".equals(elements[1])) {
            currentPackage.setPurl(new Purl(URI.create(elements[2])));
        }
    }

    private void finish() {
        mergeCurrent();
        applyRelationShips();
        applyCustomLicenses();
    }

    private void mergeCurrent() {
        if (currentPackage != null) {
            final var dependency = currentPackage.build();
            project.addDependency(dependency);
            dictionary.put(dependency.getKey(), dependency);
            currentPackage = null;
        }
    }

    private void applyRelationShips() {
        relationshipDeclarations.forEach(r -> {
            final var parts = r.split("\\s+");
            final @NullOr Dependency from = dictionary.get(parts[0]);
            final var relation = parts[1];
            final @NullOr Dependency to = dictionary.get(parts[2]);

            if (from != null && to != null) {
                final var type = RELATIONSHIP_MAPPING.getOrDefault(relation.toUpperCase(), Relation.Relationship.UNRELATED);
                from.addRelation(store.createRelation(type, to));
                to.addUsage(from);
            }
        });
    }

    private void applyCustomLicenses() {
        dictionary.values().forEach(dependency -> {
            final var pattern = Pattern.compile("(LicenseRef-[^\\s$)]+)");
            final var expanded = pattern
                    .matcher(dependency.getLicense())
                    .replaceAll(id -> "\"" + customLicenseNames.getOrDefault(id.group(), id.group()) + "\"");
            dependency.setLicense(expanded);
        });
    }

    private class SpdxPackage {
        private final String name;

        private @NullOr String spdxId;
        private @NullOr URI reference;
        private @NullOr String version;
        private @NullOr String license;

        public SpdxPackage(String name) {
            this.name = name;
        }

        void setSpdxId(String spdxId) {
            this.spdxId = spdxId;
        }

        void setPurl(Purl purl) {
            reference = purl.getReference();
            version = purl.getVersion();
        }

        Optional<URI> getReference() {
            return Optional.ofNullable(reference);
        }

        Optional<String> getVersion() {
            return Optional.ofNullable(version);
        }

        void setVersion(String version) {
            if (this.version == null) {
                this.version = version;
            }
        }

        Optional<String> getLicense() {
            return Optional.ofNullable(license);
        }

        void setLicense(String license) {
            this.license = license;
        }

        Dependency build() {
            final var dependency = store.createDependency(project, spdxId, name);
            getReference()
                    .map(ref -> store.getPackageDefinition(ref)
                            .orElseGet(() -> store.createPackageDefinition(ref)))
                    .ifPresent(dependency::setPackage);
            getVersion().ifPresent(dependency::setVersion);
            getLicense().ifPresent(dependency::setLicense);

            return dependency;
        }
    }
}
