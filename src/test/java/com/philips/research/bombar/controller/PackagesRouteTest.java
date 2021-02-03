/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.PackageService.PackageDto;
import com.philips.research.bombar.core.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PackagesRoute.class, JacksonConfiguration.class})
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class PackagesRouteTest {
    private static final URI REFERENCE = URI.create("Reference/with:issues");
    private static final String REFERENCE_ENCODED = encode(REFERENCE.toString());
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String FRAGMENT = "Fragment";
    private static final String LICENSE = "Some License";
    private static final String URL_PACKAGES = "/packages";
    private static final String URL_PACKAGE = URL_PACKAGES + "/{reference}";
    private static final String URL_APPROVAL = URL_PACKAGE + "/approve/{approval}";
    private static final String URL_EXEMPT = URL_PACKAGE + "/exempt/{license}";
    private static final PackageService.Approval APPROVAL = PackageService.Approval.NEEDS_APPROVAL;

    private final PackageDto pkg = new PackageDto();

    @MockBean
    private PackageService service;
    @MockBean
    private ProjectService projectService;
    @Autowired
    private MockMvc mvc;

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(service);
        pkg.reference = REFERENCE;
        pkg.approval = PackageService.Approval.CONTEXT;
    }

    @Test
    void findsPackages() throws Exception {
        when(service.findPackages(FRAGMENT)).thenReturn(List.of(pkg));

        mvc.perform(get(URL_PACKAGES + "?q={fragment}", FRAGMENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].reference").value(REFERENCE.toString()));
    }

    @Test
    void readsPackage() throws Exception {
        final var project = new ProjectService.ProjectDto(PROJECT_ID);
        when(projectService.findPackageUse(REFERENCE)).thenReturn(List.of(project));
        when(service.getPackage(REFERENCE)).thenReturn(pkg);

        mvc.perform(get(URL_PACKAGE, REFERENCE_ENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value(REFERENCE.toString()))
                .andExpect(jsonPath("$.projects[0].id").value(PROJECT_ID.toString()));
    }

    @Test
    void updatesPackageApproval() throws Exception {
        mvc.perform(post(URL_APPROVAL, REFERENCE_ENCODED, APPROVAL.name().toLowerCase()))
                .andExpect(status().isOk());

        verify(service).setApproval(REFERENCE, APPROVAL);
    }

    @Test
    void throws_updateApprovalWithUnknownValue() throws Exception {
        mvc.perform(post(URL_APPROVAL, REFERENCE_ENCODED, "nothing"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addsLicenseException() throws Exception {
        mvc.perform(post(URL_EXEMPT, REFERENCE_ENCODED, LICENSE))
                .andExpect(status().isOk());

        verify(service).exemptLicense(REFERENCE, LICENSE);
    }

    @Test
    void revokesLicenseExemption() throws Exception {
        mvc.perform(delete(URL_EXEMPT, REFERENCE_ENCODED, LICENSE))
                .andExpect(status().isOk());

        verify(service).unExemptLicense(REFERENCE, LICENSE);
    }
}
