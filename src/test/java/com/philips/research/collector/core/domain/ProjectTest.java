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
    private static final PackageDefinition PACKAGE = new PackageDefinition("Package");
    private static final PackageDefinition PACKAGE2 = new PackageDefinition("SecondPackage");
    private static final String VERSION = "1.2.3";

    private final Project project = new Project(PROJECT_ID);

    @Test
    void createsInstance() {
        assertThat(project.getId()).isEqualTo(PROJECT_ID);
        assertThat(project.getTitle()).isEqualTo(PROJECT_ID.toString());
        assertThat(project.getDistribution()).isEqualTo(Project.Distribution.OPEN_SOURCE);
        assertThat(project.getPhase()).isEqualTo(Project.Phase.DEVELOPMENT);
        assertThat(project.getDependencies()).isEmpty();
    }

    @Test
    void addsPackage() {
        final var first = new Dependency(PACKAGE, VERSION);
        final var second = new Dependency(PACKAGE2, VERSION);

        project.addDependency(second).addDependency(first);

        assertThat(project.getDependencies()).containsExactly(first, second);
    }

    @Test
    void findsPackage() {
        final var dependency = new Dependency(PACKAGE, VERSION);
        project.addDependency(dependency)
                .addDependency(new Dependency(PACKAGE, VERSION))
                .addDependency(new Dependency(PACKAGE2, VERSION));

        final var found = project.getDependency(PACKAGE, VERSION);

        assertThat(found).contains(dependency);
    }

    @Test
    void listsRootPackages() {
        final var root = new Dependency(PACKAGE, "root");
        final var child = new Dependency(PACKAGE, "child");
        final var grandchild = new Dependency(PACKAGE, "grandchild");
        root.addRelation(new Relation(Relation.Type.STATIC_LINK, child));
        child.addRelation(new Relation(Relation.Type.INDEPENDENT, grandchild));
        project.addDependency(root);
        project.addDependency(child);
        project.addDependency(grandchild);

        assertThat(project.getRootDependencies()).containsExactly(root);
    }

    @Test
    void removesPackages() {
        project.addDependency(new Dependency(PACKAGE, VERSION));

        project.clearDependencies();

        assertThat(project.getDependencies()).isEmpty();
    }
}
