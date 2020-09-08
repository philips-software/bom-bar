package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.PackageId;
import com.philips.research.collector.core.domain.Project;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    private static final PackageId PACKAGE_ID = new PackageId("Type", "NS", "Name");
    private static final PackageId PACKAGE_ID2 = new PackageId("Type", "NS", "Second");
    private static final String VERSION = "1.2.3";

    private final Project project = new Project("ProjectId");

    @Test
    void addsPackage() {
        final var first = new Package(PACKAGE_ID, VERSION);
        final var second = new Package(PACKAGE_ID2, VERSION);

        project.addPackage(second).addPackage(first);

        assertThat(project.getPackages()).containsExactly(first, second);
    }
}
