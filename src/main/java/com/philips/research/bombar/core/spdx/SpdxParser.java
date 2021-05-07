/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.spdx;

import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Purl;
import com.philips.research.bombar.core.domain.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class SpdxParser {
    private static final Logger LOG = LoggerFactory.getLogger(SpdxParser.class);
    private static final String SPDX_CONTAINS = "CONTAINS";
    private static final String SPDX_CONTAINED_BY = "CONTAINED_BY";
    private static final String SPDX_DEPENDS_ON = "DEPENDS_ON";
    private static final String SPDX_DEPENDENCY_OF = "DEPENDENCY_OF";
    private static final String SPDX_BUILD_DEPENDENCY_OF = "BUILD_DEPENDENCY_OF";
    private static final String SPDX_DEV_DEPENDENCY_OF = "DEV_DEPENDENCY_OF";
    private static final String SPDX_OPTIONAL_DEPENDENCY_OF = "OPTIONAL_DEPENDENCY_OF";
    private static final String SPDX_PROVIDED_DEPENDENCY_OF = "PROVIDED_DEPENDENCY_OF";
    private static final String SPDX_TEST_DEPENDENCY_OF = "TEST_DEPENDENCY_OF";
    private static final String SPDX_RUNTIME_DEPENDENCY_OF = "RUNTIME_DEPENDENCY_OF";
    private static final String SPDX_ANCESTOR_OF = "ANCESTOR_OF";
    private static final String SPDX_DESCENDANT_OF = "DESCENDANT_OF";
    private static final String SPDX_DISTRIBUTION_ARTIFACT = "DISTRIBUTION_ARTIFACT";
    private static final String SPDX_STATIC_LINK = "STATIC_LINK";
    private static final String SPDX_DYNAMIC_LINK = "DYNAMIC_LINK";
    private static final String SPDX_OPTIONAL_COMPONENT_OF = "OPTIONAL_COMPONENT_OF";
    private static final String SPDX_PACKAGE_OF = "PACKAGE_OF";
    private static final String SPDX_HAS_PREREQUISITE = "HAS_PREREQUISITE";
    private static final String SPDX_PREREQUISITE_FOR = "PREREQUISITE_FOR";
    private static final String SPDX_PATCH_FOR = "PATCH_FOR";
    private static final String SPDX_PATCH_APPLIED = "PATCH_APPLIED";
    private static final Map<String, Relation.Relationship> RELATIONSHIP_MAPPING = new HashMap<>();
    private static final Set<String> REVERSE_RELATIONSHIPS = new HashSet<>();

    static {
        RELATIONSHIP_MAPPING.put(SPDX_CONTAINS, Relation.Relationship.INDEPENDENT);
        RELATIONSHIP_MAPPING.put(SPDX_CONTAINED_BY, Relation.Relationship.INDEPENDENT);
        RELATIONSHIP_MAPPING.put(SPDX_DEPENDS_ON, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_DEPENDENCY_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_BUILD_DEPENDENCY_OF, Relation.Relationship.STATIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_DEV_DEPENDENCY_OF, Relation.Relationship.IRRELEVANT);
        RELATIONSHIP_MAPPING.put(SPDX_OPTIONAL_DEPENDENCY_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_PROVIDED_DEPENDENCY_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_TEST_DEPENDENCY_OF, Relation.Relationship.IRRELEVANT);
        RELATIONSHIP_MAPPING.put(SPDX_RUNTIME_DEPENDENCY_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_ANCESTOR_OF, Relation.Relationship.MODIFIED_CODE);
        RELATIONSHIP_MAPPING.put(SPDX_DESCENDANT_OF, Relation.Relationship.MODIFIED_CODE);
        RELATIONSHIP_MAPPING.put(SPDX_PATCH_FOR, Relation.Relationship.INDEPENDENT);
        RELATIONSHIP_MAPPING.put(SPDX_PATCH_APPLIED, Relation.Relationship.INDEPENDENT);
        RELATIONSHIP_MAPPING.put(SPDX_DISTRIBUTION_ARTIFACT, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_STATIC_LINK, Relation.Relationship.STATIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_DYNAMIC_LINK, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_OPTIONAL_COMPONENT_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_PACKAGE_OF, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_HAS_PREREQUISITE, Relation.Relationship.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put(SPDX_PREREQUISITE_FOR, Relation.Relationship.DYNAMIC_LINK);

        REVERSE_RELATIONSHIPS.add(SPDX_CONTAINED_BY);
        REVERSE_RELATIONSHIPS.add(SPDX_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_BUILD_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_DEV_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_OPTIONAL_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_PROVIDED_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_TEST_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_RUNTIME_DEPENDENCY_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_ANCESTOR_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_PATCH_APPLIED);
        REVERSE_RELATIONSHIPS.add(SPDX_OPTIONAL_COMPONENT_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_PACKAGE_OF);
        REVERSE_RELATIONSHIPS.add(SPDX_PREREQUISITE_FOR);
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
                ifInPackageDefinition(() -> currentPackage.setVersion(value));
                break;
            case "PackageHomePage":
                //noinspection ConstantConditions
                ifInPackageDefinition(() -> currentPackage.setHomePage(value));
                break;
            case "PackageSupplier":
                //noinspection ConstantConditions
                ifInPackageDefinition(() -> currentPackage.setSupplier(value));
                break;
            case "PackageSummary":
                //noinspection ConstantConditions
                ifInPackageDefinition(() -> currentPackage.setSummary(value));
                break;
            case "PackageLicenseConcluded":
                //noinspection ConstantConditions
                ifInPackageDefinition(() -> currentPackage.setLicense(value));
                break;
            case "SPDXID":
                //noinspection ConstantConditions
                ifInPackageDefinition(() -> currentPackage.setSpdxId(value));
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
                currentLicense = value;
                break;
            case "LicenseName":
                //noinspection ConstantConditions
                ifLicense(() -> customLicenseNames.put(currentLicense, value));
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

    private void ifInPackageDefinition(Runnable task) {
        if (currentPackage != null) {
            task.run();
        }
    }

    private void ifLicense(Runnable task) {
        if (currentLicense != null) {
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
        project.postProcess();
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
            final var relation = parts[1];
            final var reversed = REVERSE_RELATIONSHIPS.contains(relation);
            final @NullOr Dependency from = dictionary.get(parts[reversed ? 2 : 0]);
            final @NullOr Dependency to = dictionary.get(parts[reversed ? 0 : 2]);

            if (from != null && to != null) {
                final var relationship = RELATIONSHIP_MAPPING.getOrDefault(relation.toUpperCase(), Relation.Relationship.IRRELEVANT);
                project.addRelationship(from, to, relationship);
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
        private @NullOr Purl purl;
        private @NullOr String version;
        private @NullOr String license;
        private @NullOr URL homePage;
        private @NullOr String supplier;
        private @NullOr String summary;

        public SpdxPackage(String name) {
            this.name = name;
        }

        void setSpdxId(String spdxId) {
            this.spdxId = spdxId;
        }

        Optional<Purl> getPurl() {
            return Optional.ofNullable(purl);
        }

        void setPurl(Purl purl) {
            this.purl = purl;
            version = purl.getVersion();
        }

        Optional<String> getVersion() {
            return Optional.ofNullable(version);
        }

        void setVersion(String version) {
            if (this.version == null) {
                this.version = version;
            }
        }

        void setHomePage(String url) {
            try {
                homePage = new URL(url);
            } catch (MalformedURLException e) {
                LOG.warn("Malformed homepage URL: {}", url);
            }
        }

        void setSupplier(String supplier) {
            this.supplier = supplier;
        }

        void setSummary(String summary) {
            this.summary = summary;
        }

        Optional<String> getLicense() {
            return Optional.ofNullable(license);
        }

        void setLicense(String license) {
            this.license = license;
        }

        Dependency build() {
            final var dependency = store.createDependency(project, spdxId, name);
            getPurl().stream().peek(dependency::setPurl)
                    .map(purl -> store.getPackageDefinition(purl.getReference())
                            .orElseGet(() -> store.createPackageDefinition(purl.getReference())))
                    .forEach(dependency::setPackage);
            dependency.getPackage().ifPresent(pkg -> {
                if (pkg.getReference().toString().equals(pkg.getName())) {
                    pkg.setName(name);
                }
                if (pkg.getHomepage().isEmpty()) {
                    pkg.setHomepage(homePage);
                }
                if (pkg.getVendor().isEmpty()) {
                    pkg.setVendor(supplier);
                }
                if (pkg.getDescription().isEmpty()) {
                    pkg.setDescription(summary);
                }
            });
            getVersion().ifPresent(dependency::setVersion);
            getLicense().ifPresent(dependency::setLicense);

            return dependency;
        }
    }
}
