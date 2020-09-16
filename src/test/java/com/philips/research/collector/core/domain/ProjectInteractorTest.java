package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectInteractorTest {
    private static final String TITLE = "Title";
    private static final UUID PROJECT_ID = UUID.randomUUID();

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
}
