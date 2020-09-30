/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

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
    void createsInstance() {
        assertThat(project.getId()).isEqualTo(PROJECT_ID);
        assertThat(project.getTitle()).isEqualTo(PROJECT_ID.toString());
        assertThat(project.getDistribution()).isEqualTo(Project.Distribution.OPEN_SOURCE);
        assertThat(project.getPhase()).isEqualTo(Project.Phase.DEVELOPMENT);
        assertThat(project.getPackages()).isEmpty();
    }

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
    void listsRootPackages() {
        final var root = new Package("Root", VERSION);
        final var child = new Package("Child", VERSION);
        final var grandchild = new Package("Grandchild", VERSION);
        root.addChild(child, Package.Relation.STATIC_LINK);
        child.addChild(grandchild, Package.Relation.INDEPENDENT);
        project.addPackage(root);
        project.addPackage(child);
        project.addPackage(grandchild);

        assertThat(project.getRootPackages()).containsExactly(root);
    }

    @Test
    void removesPackage() {
        final var first = new Package(PACKAGE, VERSION);
        final var second = new Package(PACKAGE2, VERSION);
        project.addPackage(first).addPackage(second);
        first.addChild(second, Package.Relation.STATIC_LINK);

        project.removePackage(second);

        assertThat(project.getPackages()).containsExactly(first);
        assertThat(first.getChildren()).isEmpty();
    }
}
