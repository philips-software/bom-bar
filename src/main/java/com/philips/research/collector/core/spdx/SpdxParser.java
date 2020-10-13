/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.domain.*;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class SpdxParser {
    static private final Map<String, Relation.Type> RELATIONSHIP_MAPPING = new HashMap<>();
    private final Project project;
    private final ProjectStore store;
    private final Map<String, Dependency> dictionary = new HashMap<>();
    private final List<String> relationships = new ArrayList<>();
    private @NullOr SpdxPackage current;

    {
        RELATIONSHIP_MAPPING.put("DESCENDANT_OF", Relation.Type.MODIFIED_CODE);
        RELATIONSHIP_MAPPING.put("STATIC_LINK", Relation.Type.STATIC_LINK);
        RELATIONSHIP_MAPPING.put("DYNAMIC_LINK", Relation.Type.DYNAMIC_LINK);
        RELATIONSHIP_MAPPING.put("DEPENDS_ON", Relation.Type.INDEPENDENT);
    }

    public SpdxParser(Project project, ProjectStore store) {
        this.project = project;
        this.store = store;
    }

    public void parse(InputStream stream) {
        project.clearDependencies();
        new TagValueParser(this::tagValue).parse(stream);
        finish();
    }

    private void tagValue(String tag, String value) {
        switch (tag) {
            case "PackageName":
                mergeCurrent();
                current = new SpdxPackage(value);
                break;
            case "PackageVersion":
                //noinspection ConstantConditions
                ifValue(value, () -> current.setVersion(value));
                break;
            case "PackageLicenseConcluded":
                //noinspection ConstantConditions
                ifValue(value, () -> current.setLicense(value));
                break;
            case "SPDXID":
                //noinspection ConstantConditions
                ifValue(value, () -> current.setSpdxRef(value));
                break;
            case "ExternalRef":
                externalRef(value);
                break;
            case "Relationship":
                relationships.add(value);
                break;
            case "FileName": // Start of other section
                mergeCurrent();
                break;
            default: // Ignore
        }
    }

    private void ifValue(String value, Runnable task) {
        if (current != null && !"NOASSERTION".equals(value)) {
            task.run();
        }
    }

    private void externalRef(String value) {
        final var elements = value.split("\\s+");
        if (elements.length != 3) {
            throw new SpdxException("Malformed external reference value: " + value);
        }
        if (current != null && "PACKAGE-MANAGER".equals(elements[0]) && "purl".equals(elements[1])) {
            current.setPurl(new Purl(URI.create(elements[2])));
        }
    }

    private void finish() {
        mergeCurrent();
        applyRelationShips();
    }

    private void mergeCurrent() {
        if (current != null) {
            final var dependency = current.build();
            project.addDependency(dependency);
            dictionary.put(current.spdxRef, dependency);
            current = null;
        }
    }

    private void applyRelationShips() {
        relationships.forEach(r -> {
            final var parts = r.split("\\s+");
            final var from = dictionary.get(parts[0]);
            final var to = dictionary.get(parts[2]);
            final var relation = parts[1];

            relate(from, to, relation);
        });
    }

    private void relate(@NullOr Dependency from, @NullOr Dependency to, String relation) {
        final var type = RELATIONSHIP_MAPPING.get(relation.toUpperCase());
        if (from != null && to != null && type != null) {
            from.addRelation(store.createRelation(type, to));
        }
    }

    private class SpdxPackage {
        private final String name;

        private String spdxRef = "";
        private @NullOr String reference;
        private @NullOr String version;
        private @NullOr String license;

        public SpdxPackage(String name) {
            this.name = name;
        }

        void setSpdxRef(String reference) {
            this.spdxRef = reference;
        }

        void setPurl(Purl purl) {
            reference = purl.getReference();
            version = purl.getVersion();
        }

        Optional<String> getReference() {
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
            final @NullOr PackageDefinition pkg = getReference().
                    map(store::getOrCreatePackageDefinition)
                    .orElse(null);
            final var dependency = new Dependency(pkg, getVersion().orElse("?"));
            dependency.setTitle(name);
            getLicense().ifPresent(dependency::setLicense);

            return dependency;
        }
    }
}
