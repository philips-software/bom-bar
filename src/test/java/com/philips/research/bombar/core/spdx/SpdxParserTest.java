/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.spdx;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpdxParserTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String TITLE = "Name";
    private static final PackageRef REFERENCE = new PackageRef("maven/namespace/name");
    private static final String VERSION = "Version";
    private static final PackageURL PURL = purlOf("pkg:" + REFERENCE.canonicalize() + '@' + VERSION);
    private static final String LICENSE = "License";

    private final Project project = new Project(PROJECT_ID);
    private final PersistentStore store = mock(PersistentStore.class);

    private final SpdxParser parser = new SpdxParser(project, store);
    private final Package pkg = new Package(REFERENCE);

    static PackageURL purlOf(String purl) {
        try {
            return new PackageURL(purl);
        } catch (MalformedPackageURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @BeforeEach
    void beforeEach() {
        //noinspection unchecked
        when(store.getPackageDefinition(REFERENCE)).thenReturn(Optional.empty(), Optional.of(pkg));
        when(store.createPackageDefinition(REFERENCE)).thenReturn(pkg);
        when(store.createDependency(eq(project), any(), any())).thenAnswer(
                (a) -> new Dependency(a.getArgument(1), a.getArgument(2)));
    }

    @Test
    void setsUpdateTimestamp() {
        final var iso = "2010-01-29T18:30:22Z";
        final var spdx = spdxStream("Created: " + iso);

        parser.parse(spdx);

        assertThat(project.getLastUpdate()).contains(Instant.parse(iso));
    }

    @Test
    void setsInitialProjectTitle() {
        final var spdx = spdxStream(
                "DocumentName: " + TITLE
        );

        parser.parse(spdx);

        assertThat(project.getTitle()).isEqualTo(TITLE);
    }

    @Test
    void ignoresProjectTitle_alreadySet() {
        project.setTitle(TITLE);
        final var spdx = spdxStream(
                "DocumentName: Something else"
        );

        parser.parse(spdx);

        assertThat(project.getTitle()).isEqualTo(TITLE);
    }

    @Test
    void addsPackageAsDependency() {
        final var spdx = spdxStream(
                "PackageName: " + TITLE,
                "SPDXID: package",
                "PackageLicenseConcluded: " + LICENSE,
                "ExternalRef: PACKAGE-MANAGER purl " + PURL,
                "PackageVersion: Nope");

        parser.parse(spdx);

        assertThat(project.getDependencies()).hasSize(1);
        //noinspection OptionalGetWithoutIsPresent
        final var dependency = project.getDependency("package").get();
        assertThat(dependency.getPackage()).contains(pkg);
        assertThat(dependency.getVersion()).isEqualTo(VERSION);
        assertThat(dependency.getTitle()).isEqualTo(TITLE);
        assertThat(dependency.getLicense()).isEqualTo(LICENSE);
        assertThat(dependency.getPurl()).contains(PURL);
    }

    @Test
    void addsAnonymousDependency() {
        final var spdx = spdxStream(
                "PackageName: " + TITLE,
                "PackageVersion: " + VERSION,
                "PackageLicenseConcluded: " + LICENSE);

        parser.parse(spdx);

        assertThat(project.getDependencies()).hasSize(1);
        final var dependency = project.getDependencies().iterator().next();
        assertThat(dependency.getKey()).isNotBlank();
        assertThat(dependency.getPackage()).isEmpty();
        assertThat(dependency.getTitle()).isEqualTo(TITLE);
        assertThat(dependency.getVersion()).isEqualTo(VERSION);
        assertThat(dependency.getLicense()).isEqualTo(LICENSE);
        assertThat(dependency.getPurl()).isEmpty();
    }

    @Test
    void replacesPackages() {
        project.addDependency(new Dependency("Old", "Old stuff"));
        final var spdx = spdxStream(
                "PackageName: " + TITLE,
                "SPDXID: package");

        parser.parse(spdx);

        assertThat(project.getDependencies()).hasSize(1);
        assertThat(project.getDependency("package")).isNotEmpty();
    }

    @Test
    void fallsBackFromConcludedToDeclaredLicense() {
        parser.parse(spdxStream("PackageName: License fallback",
                "SPDXID: 1",
                "PackageLicenseDeclared: MIT"));

        final var dependency = project.getDependency("1").orElseThrow();
        assertThat(dependency.getLicense()).isEqualTo("MIT");
    }

    @Test
    void expandsNonSpdxLicenses() {
        parser.parse(spdxStream(
                "PackageName: Custom license",
                "SPDXID: 1",
                "PackageLicenseConcluded: Apache-2.0 OR (MIT AND LicenseRef-Custom) OR LicenseRef-Custom",
                "PackageName: Broken",
                "SPDXID: 2",
                "PackageLicenseConcluded: LicenseRef-Broken",
                "LicenseID: LicenseRef-Custom",
                "LicenseName: Name"));

        final var dependency = project.getDependency("1").orElseThrow();
        final var broken = project.getDependency("2").orElseThrow();
        assertThat(dependency.getLicense()).isEqualTo("Apache-2.0 OR (MIT AND \"Name\") OR \"Name\"");
        assertThat(broken.getLicense()).isEqualTo("\"LicenseRef-Broken\"");
    }

    @Test
    void copiesMissingPackageInformation() throws Exception {
        parser.parse(spdxStream(
                "PackageName: Name",
                "SPDXID: 1",
                "ExternalRef: PACKAGE-MANAGER purl " + PURL,
                "PackageHomePage: https://example.com",
                "PackageSupplier: Vendor",
                "PackageSummary: <text>Summary</text>"));

        final var pkg = project.getDependency("1").flatMap(Dependency::getPackage).orElseThrow();
        assertThat(pkg.getName()).isEqualTo("Name");
        assertThat(pkg.getHomepage()).contains(new URL("https://example.com"));
        assertThat(pkg.getVendor()).contains("Vendor");
        assertThat(pkg.getDescription()).contains("Summary");
    }

    private InputStream spdxStream(String... lines) {
        final var string = String.join("\n", lines);
        return new ByteArrayInputStream(string.getBytes());
    }

    @Nested
    class RelationshipConversion {
        @Test
        void createsChildRelations() {
            parser.parse(spdxStream(
                    "Relationship: parent DEPENDS_ON child",
                    "Relationship: parent STATIC_LINK child",
                    "PackageName: Parent package",
                    "SPDXID: parent", // Start of parent
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@1.0",
                    "PackageName: Child package",
                    "SPDXID: child", // Start of child
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@2.0"));

            final var parent = project.getDependency("parent").orElseThrow();
            final var child = project.getDependency("child").orElseThrow();
            assertThat(child.getRelations()).isEmpty();
            assertThat(parent.getRelations()).hasSize(2);
            var relation = parent.getRelations().stream()
                    .filter(r -> r.getType().equals(Relation.Relationship.DYNAMIC_LINK))
                    .findFirst().orElseThrow();
            assertThat(relation.getTarget()).isEqualTo(child);
            assertThat(child.getUsages()).contains(parent);
            assertThat(parent.getUsages()).isEmpty();
            assertThat(parent.isRoot()).isTrue();
            assertThat(child.isRoot()).isFalse();
        }

        @Test
        void createsReversedChildRelationship() {
            parser.parse(spdxStream(
                    "Relationship: child DEPENDENCY_OF parent",
                    "PackageName: Parent package",
                    "SPDXID: parent", // Start of parent
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@1.0",
                    "PackageName: Child package",
                    "SPDXID: child", // Start of child
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@2.0"));

            final var parent = project.getDependency("parent").orElseThrow();
            final var child = project.getDependency("child").orElseThrow();
            assertThat(child.getRelations()).isEmpty();
            assertThat(parent.getRelations()).hasSize(1);
            var relation = parent.getRelations().stream().findFirst().orElseThrow();
            assertThat(relation.getTarget()).isEqualTo(child);
            assertThat(parent.isRoot()).isTrue();
            assertThat(child.isRoot()).isFalse();
        }

        @Test
        void ignoredDuplicateRelationship() {
            parser.parse(spdxStream(
                    "Relationship: parent DEPENDS_ON parent",
                    "Relationship: parent DEPENDS_ON parent",
                    "PackageName: Parent package",
                    "SPDXID: parent", // Start of parent
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@1.0",
                    "PackageName: Child package",
                    "SPDXID: child", // Start of child
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@2.0"));

            final var parent = project.getDependency("parent").orElseThrow();
            assertThat(parent.getRelations()).hasSize(1);
        }

        @Test
        void defaultsToIrrelevantRelationship() {
            parser.parse(spdxStream(
                    "Relationship: parent UNKNOWN_RELATION child",
                    "PackageName: Parent package",
                    "SPDXID: parent", // Start of parent
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@1.0",
                    "PackageName: Child package",
                    "SPDXID: child", // Start of child
                    "ExternalRef: PACKAGE-MANAGER purl pkg:" + REFERENCE + "@2.0"));

            final var parent = project.getDependency("parent").orElseThrow();
            final var relation = parent.getRelations().stream().findFirst().orElseThrow();
            assertThat(relation.getType()).isEqualTo(Relation.Relationship.IRRELEVANT);
        }
    }
}
