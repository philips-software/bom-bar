/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.ProjectService.ProjectDto;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProjectsRoute.class, JacksonConfiguration.class})
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ProjectsRouteTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String DEPENDENCY_ID = "Id";
    private static final String NAME = "Name";
    private static final URI REFERENCE = URI.create("package/reference");
    private static final String ENCODED_REFERENCE = "package%2Freference";
    private static final String RATIONALE = "Rationale";
    private static final String BASE_URL = "/projects";
    private static final String PROJECT_URL = BASE_URL + "/{projectId}";
    private static final String UPLOAD_SPDX_URL = PROJECT_URL + "/upload";
    private static final String DEPENDENCIES_URL = PROJECT_URL + "/dependencies";
    private static final String DEPENDENCY_URL = DEPENDENCIES_URL + "/{reference}";
    private static final String PACKAGE_SOURCE_URL = DEPENDENCY_URL + "/source";
    private static final String EXEMPTION_URL = DEPENDENCY_URL + "/exempt";
    private static final String LICENSES_URL = PROJECT_URL + "/licenses";

    @MockBean
    private ProjectService service;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(service);
    }

    @Test
    void getsAllProjects() throws Exception {
        var dto = new ProjectDto(PROJECT_ID);
        when(service.projects()).thenReturn(List.of(dto));

        mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(PROJECT_ID.toString()));
    }

    @Test
    void createsNewProject() throws Exception {
        final var json = new JSONObject().put("title", NAME).toString();
        final var dto = new ProjectDto(PROJECT_ID);
        dto.title = NAME;
        when(service.createProject(NAME)).thenReturn(dto);

        mvc.perform(post(BASE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl(BASE_URL + "/" + PROJECT_ID))
                .andExpect(jsonPath("$.id").value(PROJECT_ID.toString()))
                .andExpect(jsonPath("$.title").value(NAME));
    }

    @Test
    void readsProject() throws Exception {
        final var dto = new ProjectDto(PROJECT_ID);
        when(service.getProject(PROJECT_ID)).thenReturn(dto);

        mvc.perform(get(PROJECT_URL, PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID.toString()));
    }

    @Test
    void updatesProject() throws Exception {
        final var json = new JSONObject().put("title", NAME);
        final var dto = new ProjectDto(PROJECT_ID);
        dto.title = NAME;
        when(service.updateProject(any(ProjectDto.class))).thenReturn(dto);

        mvc.perform(put(PROJECT_URL, PROJECT_ID)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(NAME));

        verify(service).updateProject(any(ProjectDto.class));
    }

    @Test
    void uploadsSpdxFile() throws Exception {
        // Filename necessary due to Spring bug: https://github.com/spring-projects/spring-framework/issues/26261
        final var file = new MockMultipartFile("file", "Filename", MediaType.TEXT_PLAIN_VALUE, "Data".getBytes());

        mvc.perform(multipart(UPLOAD_SPDX_URL, PROJECT_ID).file(file))
                .andExpect(status().isOk());

        verify(service).importSpdx(eq(PROJECT_ID), any(InputStream.class));
    }

    @Test
    void readsDependencies() throws Exception {
        final var dto = new ProjectService.DependencyDto(DEPENDENCY_ID);
        when(service.getDependencies(PROJECT_ID)).thenReturn(List.of(dto));

        mvc.perform(get(DEPENDENCIES_URL, PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id").value(DEPENDENCY_ID));
    }

    @Test
    void readsDependencyById() throws Exception {
        final var dto = new ProjectService.DependencyDto(DEPENDENCY_ID);
        when(service.getDependency(PROJECT_ID, DEPENDENCY_ID)).thenReturn(dto);

        mvc.perform(get(DEPENDENCY_URL, PROJECT_ID, DEPENDENCY_ID))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id").value(DEPENDENCY_ID)));
    }

    @Test
    void exemptsDependency() throws Exception {
        mvc.perform(post(EXEMPTION_URL, PROJECT_ID, DEPENDENCY_ID)
                .content(new JSONObject().put("rationale", RATIONALE).toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).exempt(PROJECT_ID, DEPENDENCY_ID, RATIONALE);
    }

    @Test
    void removesDependencyExemption() throws Exception {
        mvc.perform(delete(EXEMPTION_URL, PROJECT_ID, DEPENDENCY_ID))
                .andExpect(status().isOk());

        verify(service).exempt(PROJECT_ID, DEPENDENCY_ID, null);
    }

    @Test
    void readsLicenseDistribution() throws Exception {
        when(service.licenseDistribution(PROJECT_ID)).thenReturn(Map.of("License", 7));

        mvc.perform(get(LICENSES_URL, PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.License").value(7));
    }
}
