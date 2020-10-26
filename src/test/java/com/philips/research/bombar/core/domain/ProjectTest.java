/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
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
        assertThat(project.getLastUpdate()).isEmpty();
        assertThat(project.getDistribution()).isEqualTo(Project.Distribution.OPEN_SOURCE);
        assertThat(project.getIssueCount()).isZero();
        assertThat(project.getPhase()).isEqualTo(Project.Phase.DEVELOPMENT);
        assertThat(project.getDependencies()).isEmpty();
    }

    @Test
    void marksLastUpdate() {
        final var now = Instant.now();

        project.setLastUpdate(now);

        assertThat(project.getLastUpdate()).contains(now);
    }

    @Test
    void tracksNumberOfIssues() {
        project
                .addDependency(new Dependency(PACKAGE, VERSION).setIssueCount(5))
                .addDependency(new Dependency(PACKAGE2, VERSION).setIssueCount(7));

        assertThat(project.getIssueCount()).isEqualTo(5 + 7);
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
