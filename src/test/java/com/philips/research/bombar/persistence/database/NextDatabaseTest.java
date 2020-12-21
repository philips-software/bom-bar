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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;

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
    private ProjectRepository projectRepository;

    @Test
    void storesPackageDefinitions() {
        final var pkg = database.createPackageDefinition(REFERENCE);

        final var stored = database.getPackageDefinition(REFERENCE);

        assertThat(stored).contains(pkg);
    }

    @Test
    void findsPackagesByFragment() {
        final var pkg = database.createPackageDefinition(REFERENCE);

        final var found = database.findPackageDefinitions("%space%");

        assertThat(found).contains(pkg);
    }

    @Test
    void storesProjects() {
        final var project = database.createProject();

        final var stored = database.getProject(project.getId());

        assertThat(stored).contains(project);
    }

    @Test
    void storesDependencies() {
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);

        //noinspection OptionalGetWithoutIsPresent
        final var stored = database.getProject(project.getId()).get();

        System.out.println(stored.getDependencies());
        assertThat(stored.getDependency(DEPENDENCY_ID)).contains(dependency);
    }

    @Test
    void storesRelations() {
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        dependency.addRelation(new Relation(Relation.Relationship.DYNAMIC_LINK, dependency));
        project.addDependency(dependency);

        final var stored = database.getProject(project.getId()).get();

        assertThat(stored.getDependency(DEPENDENCY_ID).get().getRelations()).hasSize(1);
    }

    @Test
    @Disabled("Is the back link really not created?")
    void findsEnclosingProjectForDependency() {
        final var project = database.createProject();
        final var dependency = database.createDependency(DEPENDENCY_ID, TITLE);
        project.addDependency(dependency);
        projectRepository.save((ProjectEntity) project);

        final var found = database.getProjectFor(dependency);

        assertThat(found).isEqualTo(project);
    }
}
