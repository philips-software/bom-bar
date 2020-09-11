package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.BusinessException;
import com.philips.research.collector.core.ImportService;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImportInteractorTest {
    private static final URL VALID_SPDX = ImportInteractorTest.class.getResource("/valid.spdx");
    private static final UUID UNKNOWN_UUID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();

    private final ProjectStore store = mock(ProjectStore.class);
    private final ImportService interactor = new ImportInteractor(store);

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
