/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence.database;

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
@ComponentScan(basePackageClasses = {NextDatabase.class})
@DataJpaTest
class NextDatabaseTest {
    private static final URI REFERENCE = URI.create("namespace/name");
    private static final String TITLE = "Title";
    private static final String DEPENDENCY_ID = "DependencyId";

    @Autowired
    private NextDatabase database;

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
    void findsPackagesByFragment() {
        final var pkg = database.createPackageDefinition(REFERENCE);
        flushEntityManager();

        final var found = database.findPackageDefinitions("%space%");

        assertThat(found).contains(pkg);
    }

    @Test
    void storesProjects() {
        final var project = database.createProject();
        flushEntityManager();

        final var stored = database.getProject(project.getId());

        assertThat(stored).contains(project);
    }

    @Test
    void storesDependencies() {
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var stored = database.getProject(project.getId()).get();

        System.out.println(stored.getDependencies());
        assertThat(stored.getDependency(DEPENDENCY_ID)).contains(dependency);
    }

    @Test
    void findsDependencyByPackageDefinition() {
        final var pkg = database.createPackageDefinition(REFERENCE);
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE).setPackage(pkg);
        project.addDependency(dependency);
        flushEntityManager();

        final var dependencies = database.findDependencies(pkg);
        final var result = database.getProjectFor(dependencies.get(0));

        assertThat(result).isEqualTo(project);
    }

    @Test
    void storesRelations() {
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        dependency.addRelation(new Relation(Relation.Relationship.DYNAMIC_LINK, dependency));
        dependency.addUsage(dependency);
        project.addDependency(dependency);
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
        final var pkg = database.createPackageDefinition(REFERENCE);
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        project.addDependency(dependency.setPackage(pkg));
        flushEntityManager();

        //noinspection OptionalGetWithoutIsPresent
        final var def = database.getPackageDefinition(REFERENCE).get();
        final var dep = database.findDependencies(def).get(0);
        final var proj = database.getProjectFor(dep);

        assertThat(proj).isEqualTo(project);
    }

    private void flushEntityManager() {
        entityManager.flush();
        entityManager.clear();
    }
}
