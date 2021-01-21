/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ControllerExceptionHandlerTest {
    private static final String TEST_URL = "/test/{param}";
    @Autowired
    MockMvc mockMvc;
    @MockBean
    TestResource resource;

    @Test
    void handlesParameterTypeMismatch() throws Exception {
        mockMvc.perform(get("/test/{param}", "Not a number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").exists());
    }

    @Test
    void handlesNotFound() throws Exception {
        doThrow(new NotFoundException("type", "id")).when(resource).test(anyInt());

        mockMvc.perform(get(TEST_URL, 13))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.reason").exists());
    }

    @RestController
    static class TestResource {
        @GetMapping(TEST_URL)
        void test(@PathVariable int param) {
        }
    }
}


