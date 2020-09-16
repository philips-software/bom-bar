package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.BusinessException;
import com.philips.research.collector.core.ProjectService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectInteractorTest {
    private static final String TITLE = "Title";
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final URL VALID_SPDX = ProjectInteractorTest.class.getResource("/valid.spdx");
    private static final UUID UNKNOWN_UUID = UUID.randomUUID();

    private final ProjectStore store = mock(ProjectStore.class);
    private final ProjectService interactor = new ProjectInteractor(store);

    @Test
    void createsProject() {
        var project = new Project(PROJECT_ID);
        when(store.createProject()).thenReturn(project);

        final var dto = interactor.createProject(TITLE);

        assertThat(dto.id).isEqualTo(PROJECT_ID);
        assertThat(dto.title).isEqualTo(TITLE);
    }

    @Nested
    class SpdxImport {
        @Test
        void throws_importForUnknownProject() {
            when(store.readProject(UNKNOWN_UUID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> interactor.importSpdx(UNKNOWN_UUID, null))
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

            assertThat(project.getPackages()).isNotEmpty();
        }
    }
}
