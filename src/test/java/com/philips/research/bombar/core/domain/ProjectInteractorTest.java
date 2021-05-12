/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.BusinessException;
import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.ProjectService.ProjectDto;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProjectInteractorTest {
    private static final String TITLE = "Title";
    private static final String DEPENDENCY_ID = "dependencyId";
    private static final UUID PROJECT_ID = UUID.randomUUID();
    @SuppressWarnings("ConstantConditions")
    private static final URL VALID_SPDX = ProjectInteractorTest.class.getResource("/valid.spdx");
    private static final UUID UNKNOWN_UUID = UUID.randomUUID();
    private static final URI PACKAGE_REFERENCE = URI.create("package/reference");
    private static final Package PACKAGE = new Package(PACKAGE_REFERENCE);
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
    void findsProjectsByName() {
        final var project = new Project(PROJECT_ID).setTitle(TITLE);
        when(store.findProjects(TITLE)).thenReturn(List.of(project, new Project(UUID.randomUUID())));

        final var projects = interactor.findProjects(TITLE, 1);

        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).title).isEqualTo(TITLE);
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
    void listsUseOfAPackage() {
        final var project = new Project(PROJECT_ID).addDependency(new Dependency("Other", TITLE));
        final var dependency1 = new Dependency("Dep1", TITLE);
        final var dependency2 = new Dependency("Dep2", TITLE);
        when(store.getPackageDefinition(PACKAGE_REFERENCE)).thenReturn(Optional.of(PACKAGE));
        when(store.findDependencies(PACKAGE)).thenReturn(List.of(dependency1, dependency2));
        when(store.getProjectFor(any(Dependency.class))).thenReturn(project);

        final var projects = interactor.findPackageUse(PACKAGE_REFERENCE);

        assertThat(projects).hasSize(1);
        final var proj = projects.get(0);
        assertThat(proj.id).isEqualTo(PROJECT_ID);
        assertThat(proj.packages).hasSize(2);
        //noinspection ConstantConditions
        assertThat(proj.packages.get(0).id).isEqualTo(dependency1.getKey());
    }

    @Nested
    class ExistingProject {
        final Dependency dependency = new Dependency(DEPENDENCY_ID, VERSION);
        final Project project = new Project(PROJECT_ID).addDependency(dependency);

        @BeforeEach
        void setUp() {
            when(store.getProject(PROJECT_ID)).thenReturn(Optional.of(project));
        }

        @Test
        void readsProject() {
            final var dto = interactor.getProject(PROJECT_ID);

            assertThat(dto.id).isEqualTo(PROJECT_ID);
        }

        @Test
        void readProjectDependencies() {
            final var dtos = interactor.getDependencies(PROJECT_ID);

            assertThat(dtos).hasSize(1);
        }

        @Test
        void readsProjectDependencyById() {
            project.addDependency(new Dependency("Other", "Other title"));

            final var dto = interactor.getDependency(PROJECT_ID, DEPENDENCY_ID);

            assertThat(dto.id).isEqualTo(DEPENDENCY_ID);
            assertThat(dto.violations).isNotNull();
        }

        @Test
        void updatesProject() {
            project.setTitle("Other")
                    .setDistribution(Project.Distribution.OPEN_SOURCE)
                    .setPhase(Project.Phase.RELEASED);
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
            project.setTitle(TITLE).setDistribution(DISTRIBUTION).setPhase(PHASE);
            final var dto = new ProjectDto(PROJECT_ID);

            interactor.updateProject(dto);

            assertThat(project.getTitle()).isEqualTo(TITLE);
            assertThat(project.getDistribution()).isEqualTo(DISTRIBUTION);
            assertThat(project.getPhase()).isEqualTo(PHASE);
        }

        @Test
        void throws_updateUnknownDistributionValue() {
            final var dto = new ProjectDto(PROJECT_ID);
            dto.distribution = "unknown";

            assertThatThrownBy(() -> interactor.updateProject(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("distribution");
        }

        @Test
        void throws_updateUnknownPhaseValue() {
            final var dto = new ProjectDto(PROJECT_ID);
            dto.phase = "unknown";

            assertThatThrownBy(() -> interactor.updateProject(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("phase");
        }

        @Test
        void exemptsProjectPackage() {
            dependency.setPackage(new Package(PACKAGE_REFERENCE));

            interactor.exempt(PROJECT_ID, DEPENDENCY_ID, RATIONALE);

            assertThat(dependency.getExemption()).isNotEmpty();
        }

        @Test
        void unexemptsProjectPackage() {
            dependency.setPackage(new Package(PACKAGE_REFERENCE));
            project.exempt(dependency, RATIONALE);

            interactor.exempt(PROJECT_ID, DEPENDENCY_ID, null);

            assertThat(dependency.getExemption()).isEmpty();
        }

        @Test
        void readsLicenseDistributionForProject() {
            dependency.setLicense("A and B");

            final var distribution = interactor.licenseDistribution(PROJECT_ID);

            assertThat(distribution).containsEntry("A", 1);
            assertThat(distribution).containsEntry("B", 1);
        }

        @Nested
        class SpdxImport {
            @Test
            void throws_importForUnknownProject() {
                when(store.getProject(UNKNOWN_UUID)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> interactor.importSpdx(UNKNOWN_UUID, mock(InputStream.class)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(UNKNOWN_UUID.toString());
            }

            @Test
            void importsProject() throws Exception {
                project.clearDependencies();
                when(store.createDependency(eq(project), any(), any())).thenAnswer(
                        (a) -> new Dependency(a.getArgument(1), a.getArgument(2)));

                try (InputStream stream = VALID_SPDX.openStream()) {
                    interactor.importSpdx(PROJECT_ID, stream);
                }

                verify(store).deleteDependencies(project);
                assertThat(project.getDependencies()).isNotEmpty();
            }
        }
    }

}
