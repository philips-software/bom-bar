package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpdxParserTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String NAME = "maven/namespace/name";
    private static final String VERSION = "Version";
    private static final String APACHE_2_0 = "Apache-2.0";

    private final Project project = new Project(PROJECT_ID);
    private final SpdxParser parser = new SpdxParser(project);

    @Test
    void throws_packageWithoutReference() {
        final var spdx = spdxStream("PackageName: Package name");

        assertThatThrownBy(() -> parser.parse(spdx))
                .isInstanceOf(SpdxException.class)
                .hasMessageContaining("Package URL");
    }

    @Test
    void addsNewPackage() {
        final var spdx = spdxStream(
                "PackageName: Package name",
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + NAME + "@" + VERSION);

        parser.parse(spdx);

        assertThat(project.getPackages()).hasSize(1);
        final var pkg = project.getPackages().get(0);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void updatesExistingPackage() {
        project.addPackage(new Package(NAME, VERSION));
        final var spdx = spdxStream(
                "PackageName: Package name",
                "PackageLicenseConcluded: " + APACHE_2_0,
                "ExternalRef: PACKAGE-MANAGER purl pkg:" + NAME + "@" + VERSION);

        parser.parse(spdx);

        assertThat(project.getPackages()).hasSize(1);
        final var pkg = project.getPackages().get(0);
        assertThat(pkg.getName()).isEqualTo(NAME);
        assertThat(pkg.getLicense()).isEqualTo(APACHE_2_0);
        assertThat(pkg.getVersion()).isEqualTo(VERSION);
    }

    @Test
    void removesObsoletePackage() {
        project.addPackage(new Package(NAME, VERSION));

        parser.parse(spdxStream(""));

        assertThat(project.getPackages()).isEmpty();
    }

    private InputStream spdxStream(String... lines) {
        final var string = String.join("\n", lines);
        return new ByteArrayInputStream(string.getBytes());
    }
}
