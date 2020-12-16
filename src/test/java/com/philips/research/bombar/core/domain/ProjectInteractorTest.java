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

import com.philips.research.bombar.core.BusinessException;
import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.ProjectService.ProjectDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectInteractorTest {
    private static final String TITLE = "Title";
    private static final String ID = "Id";
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final URL VALID_SPDX = ProjectInteractorTest.class.getResource("/valid.spdx");
    private static final UUID UNKNOWN_UUID = UUID.randomUUID();
    private static final URI PACKAGE_REFERENCE = URI.create("package/reference");
    private static final String VERSION = "Version";
    private static final Project.Distribution DISTRIBUTION = Project.Distribution.SAAS;
    private static final Project.Phase PHASE = Project.Phase.DEVELOPMENT;
    private static final String RATIONALE = "Rationale";

    private final PersistentStore store = mock(PersistentStore.class);
    private final ProjectService interactor = new ProjectInteractor(store);

    @Test
    void listsProjects() {
        when(store.getProjects()).thenReturn(List.of(new Project(PROJECT_ID)));

        final var projects = interactor.projects();

        assertThat(projects.get(0).id).isEqualTo(PROJECT_ID);
    }

    @Test
    void createsAnonymousProject() {
        var project = new Project(PROJECT_ID);
        when(store.createProject()).thenReturn(project);

        final var dto = interactor.createProject(null);

        assertThat(dto.id).isEqualTo(PROJECT_ID);
        assertThat(dto.title).isEmpty();
    }

    @Test
    void createsNamedProject() {
        var project = new Project(PROJECT_ID);
        when(store.createProject()).thenReturn(project);

        final var dto = interactor.createProject(TITLE);

        assertThat(dto.id).isEqualTo(PROJECT_ID);
        assertThat(dto.title).isEqualTo(TITLE);
    }

    @Test
    void readsProject() {
        var project = new Project(PROJECT_ID);
        project.addDependency(new Dependency(ID, VERSION));
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));

        final var dto = interactor.getProject(PROJECT_ID);

        assertThat(dto.id).isEqualTo(PROJECT_ID);
    }

    @Test
    void readProjectDependencies() {
        final var project = new Project(PROJECT_ID);
        project.addDependency(new Dependency(ID, TITLE));
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));

        final var dtos = interactor.getDependencies(PROJECT_ID);

        assertThat(dtos).hasSize(1);
    }

    @Test
    void readsProjectDependencyById() {
        final var project = new Project(PROJECT_ID);
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));
        project.addDependency(new Dependency("Other", "Other title"));
        project.addDependency(new Dependency(ID, TITLE));

        final var dto = interactor.getDependency(PROJECT_ID, ID);

        assertThat(dto.id).isEqualTo(ID);
        assertThat(dto.violations).isNotNull();
    }

    @Test
    void updatesProject() {
        final var project = new Project(PROJECT_ID)
                .setTitle("Other")
                .setDistribution(Project.Distribution.OPEN_SOURCE)
                .setPhase(Project.Phase.RELEASED);
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));
        final var dto = new ProjectDto(PROJECT_ID);
        dto.title = TITLE;
        dto.distribution = DISTRIBUTION.name().toLowerCase();
        dto.phase = PHASE.name().toLowerCase();

        final var result = interactor.updateProject(dto);

        assertThat(project.getTitle()).isEqualTo(TITLE);
        assertThat(project.getDistribution()).isEqualTo(DISTRIBUTION);
        assertThat(project.getPhase()).isEqualTo(PHASE);
        assertThat(result.title).isEqualTo(TITLE);
        assertThat(result.distribution).isEqualTo(DISTRIBUTION.name());
        assertThat(result.phase).isEqualTo(PHASE.name());
    }

    @Test
    void ignoresUnchangedProjectProperties() {
        final var project = new Project(PROJECT_ID)
                .setTitle(TITLE).setDistribution(DISTRIBUTION).setPhase(PHASE);
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));
        final var dto = new ProjectDto(PROJECT_ID);

        interactor.updateProject(dto);

        assertThat(project.getTitle()).isEqualTo(TITLE);
        assertThat(project.getDistribution()).isEqualTo(DISTRIBUTION);
        assertThat(project.getPhase()).isEqualTo(PHASE);
    }

    @Test
    void throws_updateUnknownDistributionValue() {
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(new Project(PROJECT_ID)));
        final var dto = new ProjectDto(PROJECT_ID);
        dto.distribution = "unknown";

        assertThatThrownBy(() -> interactor.updateProject(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("distribution");
    }

    @Test
    void throws_updateUnknownPhaseValue() {
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(new Project(PROJECT_ID)));
        final var dto = new ProjectDto(PROJECT_ID);
        dto.phase = "unknown";

        assertThatThrownBy(() -> interactor.updateProject(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("phase");
    }

    @Test
    void exemptsProjectPackage() {
        final var dependency = new Dependency(ID, TITLE).setPackage(new PackageDefinition(PACKAGE_REFERENCE));
        final var project = new Project(PROJECT_ID).addDependency(dependency);
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));

        interactor.exempt(PROJECT_ID, PACKAGE_REFERENCE, RATIONALE);

        assertThat(dependency.getExemption()).isNotEmpty();
    }

    @Test
    void unexemptsProjectPackage() {
        final var dependency = new Dependency(ID, TITLE).setPackage(new PackageDefinition(PACKAGE_REFERENCE));
        final var project = new Project(PROJECT_ID).addDependency(dependency).exempt(PACKAGE_REFERENCE, RATIONALE);
        when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));

        interactor.exempt(PROJECT_ID, PACKAGE_REFERENCE, null);

        assertThat(dependency.getExemption()).isEmpty();
    }

    @Test
    void listsUseOfAPackage() {
        final var project = new Project(PROJECT_ID).addDependency(new Dependency("Other", TITLE));
        final var dependency1 = new Dependency("Dep1", TITLE);
        final var dependency2 = new Dependency("Dep2", TITLE);
        when(store.findDependencies(PACKAGE_REFERENCE)).thenReturn(List.of(dependency1, dependency2));
        when(store.getProjectFor(any(Dependency.class))).thenReturn(project);

        final var projects = interactor.findPackageUse(PACKAGE_REFERENCE);

        assertThat(projects).hasSize(1);
        final var proj = projects.get(0);
        assertThat(proj.id).isEqualTo(PROJECT_ID);
        assertThat(proj.packages).hasSize(2);
        //noinspection ConstantConditions
        assertThat(proj.packages.get(0).id).isEqualTo(dependency1.getId());
    }

    @Nested
    class SpdxImport {
        @Test
        void throws_importForUnknownProject() {
            when(store.readProject(UNKNOWN_UUID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> interactor.importSpdx(UNKNOWN_UUID, mock(InputStream.class)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UNKNOWN_UUID.toString());
        }

        @Test
        void importsProject() throws Exception {
            final var project = new Project(PROJECT_ID);
            when(store.readProject(PROJECT_ID)).thenReturn(Optional.of(project));

            try (InputStream stream = VALID_SPDX.openStream()) {
                interactor.importSpdx(PROJECT_ID, stream);
            }

            assertThat(project.getDependencies()).isNotEmpty();
        }
    }
}
