package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ProjectsRouteTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String NAME = "Name";
    private static final String BASE_URL = "/projects";

    @MockBean
    private ProjectService service;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(service);
    }

    @Test
    void createsNewProject() throws Exception {
        final var json = new JSONObject().put("title", NAME).toString();
        final var dto = new ProjectService.ProjectDto();
        dto.id = PROJECT_ID;
        dto.title = NAME;
        when(service.createProject(NAME)).thenReturn(dto);

        mvc.perform(post(BASE_URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl(BASE_URL + "/" + PROJECT_ID))
                .andExpect(jsonPath("$.id").value(PROJECT_ID.toString()))
                .andExpect(jsonPath("$.title").value(NAME));
    }
}
