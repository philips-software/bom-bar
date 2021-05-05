/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String ID = "Id";
    private static final String TITLE = "Title";
    private static final URI REFERENCE = URI.create("Reference");
    private static final Package PACKAGE = new Package(REFERENCE);
    private static final String RATIONALE = "Rationale";

    private final Project project = new Project(PROJECT_ID);

    @Test
    void createsInstance() {
        assertThat(project.getId()).isEqualTo(PROJECT_ID);
        assertThat(project.getTitle()).isEmpty();
        assertThat(project.getLastUpdate()).isEmpty();
        assertThat(project.getDistribution()).isEqualTo(Project.Distribution.PROPRIETARY);
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
                .addDependency(new Dependency("Five", TITLE).setIssueCount(5))
                .addDependency(new Dependency("Seven", TITLE).setIssueCount(7));

        assertThat(project.getIssueCount()).isEqualTo(5 + 7);
    }

    @Test
    void addsDependency() {
        final var first = new Dependency("First", TITLE);
        final var second = new Dependency("Second", TITLE);

        project.addDependency(first).addDependency(second);

        assertThat(project.getDependencies()).containsExactlyInAnyOrder(first, second);
    }

    @Test
    void throws_duplicateDependency() {
        final var dependency = new Dependency(ID, TITLE);
        project.addDependency(dependency);

        assertThatThrownBy(() -> project.addDependency(dependency))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("duplicate");
    }

    @Test
    void findsDependency() {
        final var dependency = new Dependency(ID, TITLE);
        project.addDependency(new Dependency("First", TITLE))
                .addDependency(dependency)
                .addDependency(new Dependency("Third", TITLE));

        final var found = project.getDependency(ID);

        assertThat(found).contains(dependency);
    }

    @Test
    void listsRootDependencies() {
        final var root = new Dependency("Root", TITLE);
        final var child = new Dependency("Child", TITLE);
        final var grandchild = new Dependency("grandchild", TITLE);
        root.addRelation(new Relation(Relation.Relationship.STATIC_LINK, child));
        child.addRelation(new Relation(Relation.Relationship.INDEPENDENT, grandchild));
        project.addDependency(root);
        project.addDependency(child);
        project.addDependency(grandchild);

        assertThat(project.getRootDependencies()).containsExactly(root);
    }

    @Test
    void removesDependencies() {
        project.addDependency(new Dependency(ID, TITLE));

        project.clearDependencies();

        assertThat(project.getDependencies()).isEmpty();
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Project.class)
                .withOnlyTheseFields("uuid")
                .withNonnullFields("uuid")
                .withPrefabValues(Dependency.class, new Dependency("red", TITLE), new Dependency("blue", TITLE))
                .verify();
    }

    @Nested
    class PackageExemptions {
        private final Dependency dependency = new Dependency(ID, TITLE).setPackage(PACKAGE);

        @Test
        void exemptsExistingDependencies() {
            project.addDependency(dependency);
            project.exempt(dependency, RATIONALE);

            assertThat(dependency.getExemption()).contains(RATIONALE);
        }

        @Test
        void unexemptsExistingDependencies() {
            project.exempt(dependency, RATIONALE);

            project.unexempt(dependency);

            assertThat(dependency.getExemption()).isEmpty();
        }

        @Test
        void exemptsNewAddedDependencies() {
            project.exempt(dependency, RATIONALE);

            project.addDependency(dependency);

            assertThat(dependency.getExemption()).contains(RATIONALE);
        }
    }
}
