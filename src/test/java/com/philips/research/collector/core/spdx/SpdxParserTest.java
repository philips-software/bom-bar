/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SpdxParserTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String PACKAGE = "maven/namespace/name";
    private static final String OTHER = "maven/namespace/other";
    private static final String VERSION = "Version";
    private static final String APACHE_2_0 = "Apache-2.0";

    private final Project project = new Project(PROJECT_ID);
    private final SpdxParser parser = new SpdxParser(project);

    @Test
    void addsNewPackage() {
        final var spdx = spdxStream(
                "PackageName: Package name",
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + PACKAGE + "@" + VERSION);

        parser.parse(spdx);

        assertThat(project.getPackages()).hasSize(1);
        final var pkg = project.getPackages().get(0);
        assertThat(pkg.getReference()).isEqualTo(PACKAGE);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void updatesExistingPackage() {
        project.addPackage(new Package(PACKAGE, VERSION));
        final var spdx = spdxStream(
                "PackageName: Package name",
                "PackageLicenseConcluded: " + APACHE_2_0,
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + PACKAGE + "@" + VERSION);

        parser.parse(spdx);

        assertThat(project.getPackages()).hasSize(1);
        final var pkg = project.getPackages().get(0);
        assertThat(pkg.getReference()).isEqualTo(PACKAGE);
        assertThat(pkg.getLicense()).isEqualTo(APACHE_2_0);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void removesObsoletePackage() {
        project.addPackage(new Package(PACKAGE, VERSION));

        parser.parse(spdxStream(""));

        assertThat(project.getPackages()).isEmpty();
    }

    @Test
    void createsChildRelations() {
        parser.parse(spdxStream(
                "Relationship: parent DYNAMIC_LINK child",
                "Relationship: parent DEPENDS_ON child",
                "PackageName: " + PACKAGE,
                "SPDXID: parent",
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + PACKAGE + "@" + VERSION,
                "PackageName:" + OTHER,
                "SPDXID: child",
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + OTHER + "@" + VERSION));
        final var parent = project.getPackage(PACKAGE, VERSION).get();
        final var children = parent.getChildren();
        final var child = project.getPackage(OTHER, VERSION).get();
        assertThat(children.get(0).getPackage()).isEqualTo(child);
        assertThat(children.get(0).getRelation()).isEqualTo(Package.Relation.DYNAMIC_LINK);
        assertThat(children.get(1).getPackage()).isEqualTo(child);
        assertThat(children.get(1).getRelation()).isEqualTo(Package.Relation.INDEPENDENT);
    }

    private InputStream spdxStream(String... lines) {
        final var string = String.join("\n", lines);
        return new ByteArrayInputStream(string.getBytes());
    }
}
