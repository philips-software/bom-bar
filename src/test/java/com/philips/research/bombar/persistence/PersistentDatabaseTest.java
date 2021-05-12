/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Relation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ComponentScan(basePackageClasses = {PersistentDatabase.class})
@DataJpaTest
class PersistentDatabaseTest {
    private static final URI REFERENCE = URI.create("namespace/name");
    private static final String TITLE = "Title";
    private static final String DEPENDENCY_ID = "DependencyId";

    @Autowired
    private PersistentDatabase database;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void storesPackageDefinitions() {
        final var pkg = database.createPackageDefinition(REFERENCE);
        flushEntityManager();

        final var stored = database.getPackageDefinition(REFERENCE);

        assertThat(stored).contains(pkg);
    }

    @Test
    void findsPackagesCaseInsensitiveByFragment() {
        final var pkg = database.createPackageDefinition(REFERENCE);
        flushEntityManager();

        final var found = database.findPackageDefinitions("sPaCe");

        assertThat(found).contains(pkg);
    }

    @Test
    void escapesWildcardsFromPackageSearchFragment() {
        final var pattern = "x%2F\\y[]_z";
        final var uri = "Ax%2Fy_zB";
        final var pkg = database.createPackageDefinition(URI.create(uri));

        assertThat(database.findPackageDefinitions(pattern)).isNotEmpty();
    }

    @Test
    void storesProjects() {
        final var project = database.createProject();
        flushEntityManager();

        final var stored = database.getProject(project.getId());

        assertThat(stored).contains(project);
    }

    @Test
    void findsProjects() {
        final var caseMatch = database.createProject();
        caseMatch.setTitle("Is an older MATCHing name");
        final var noMatch = database.createProject();
        noMatch.setTitle("Is quite another name");
        final var firstMatch = database.createProject();
        firstMatch.setTitle("Is the expected first matching project");
        database.createProject();
        flushEntityManager();

        final var matches = database.findProjects("match");

        assertThat(matches).hasSize(2);
        assertThat(matches.get(0)).isEqualTo(firstMatch);
        assertThat(matches.get(1)).isEqualTo(caseMatch);
    }

    @Test
    void storesDependencies() {
        final var project = database.createProject();
        final var dependency = database.createDependency(project, DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var stored = database.getProject(project.getId()).get();

        System.out.println(stored.getDependencies());
        assertThat(stored.getDependency(DEPENDENCY_ID)).contains(dependency);
    }

    @Test
    void findsDependencyByPackageDefinition() {
        final var project = database.createProject();
        final var pkg = database.createPackageDefinition(REFERENCE);
        final var dependency = database.createDependency(project, DEPENDENCY_ID, TITLE).setPackage(pkg);
        project.addDependency(dependency);
        flushEntityManager();

        final var dependencies = database.findDependencies(pkg);
        final var result = database.getProjectFor(dependencies.get(0));

        assertThat(result).isEqualTo(project);
    }

    @Test
    void storesRelations() {
        final var project = database.createProject();
        final var dependency = database.createDependency(project, DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);
        project.addRelationship(dependency, dependency, Relation.Relationship.DYNAMIC_LINK);
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var stored = database.getProject(project.getId()).get();

        //noinspection OptionalGetWithoutIsPresent
        final var dep = stored.getDependency(DEPENDENCY_ID).get();
        final var relation = new ArrayList<>(dep.getRelations()).get(0);
        assertThat(relation.getType()).isEqualTo(Relation.Relationship.DYNAMIC_LINK);
        assertThat(relation.getTarget()).isEqualTo(dep);
        assertThat(dep.getUsages()).contains(dep);
    }

    @Test
    void findsEnclosingProjectForDependency() {
        final var project = database.createProject();
        final var pkg = database.createPackageDefinition(REFERENCE);
        final var dependency = database.createDependency(project, DEPENDENCY_ID, TITLE);
        project.addDependency(dependency.setPackage(pkg));
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var def = database.getPackageDefinition(REFERENCE).get();
        final var dep = database.findDependencies(def).get(0);
        final var proj = database.getProjectFor(dep);

        assertThat(proj).isEqualTo(project);
    }

    @Test
    void forcesRemovalOfDependenciesForProject() {
        final var project = database.createProject();
        final var dependency = database.createDependency(project, DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);
        database.deleteDependencies(project);
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var proj = database.getProject(project.getId()).get();
        assertThat(proj.getDependencies()).isEmpty();
    }

    private void flushEntityManager() {
        entityManager.flush();
        entityManager.clear();
    }
}
