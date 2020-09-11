package com.philips.research.collector.core.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String PACKAGE = "Package";
    private static final String PACKAGE2 = "SecondPackage";
    private static final String VERSION = "1.2.3";

    private final Project project = new Project(PROJECT_ID);

    @Test
    void addsPackage() {
        final var first = new Package(PACKAGE, VERSION);
        final var second = new Package(PACKAGE2, VERSION);

        project.addPackage(second).addPackage(first);

        assertThat(project.getPackages()).containsExactly(first, second);
    }

    @Test
    void findsPackage() {
        final var pkg = new Package(PACKAGE, VERSION);
        project.addPackage(pkg)
                .addPackage(new Package(PACKAGE, VERSION))
                .addPackage(new Package("other", VERSION));


        final var found = project.getPackage(PACKAGE, VERSION);

        assertThat(found).contains(pkg);
    }

    @Test
    void removesPackage() {
        final var first = new Package(PACKAGE, VERSION);
        final var second = new Package(PACKAGE2, VERSION);
        project.addPackage(first).addPackage(second);

        project.removePackage(second);

        assertThat(project.getPackages()).containsExactly(first);
    }
}
