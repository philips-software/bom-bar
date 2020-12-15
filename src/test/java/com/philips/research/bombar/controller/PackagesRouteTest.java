/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.PackageService.PackageDto;
import com.philips.research.bombar.core.ProjectService;
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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class PackagesRouteTest {
    private static final URI REFERENCE = URI.create("Reference/with:issues");
    private static final String REFERENCE_ENCODED = encode(REFERENCE.toString());
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String FRAGMENT = "Fragment";
    private static final String LICENSE = "Some License";
    private static final String RATIONALE = "Rationale";
    private static final String URL_PACKAGES = "/packages";
    private static final String URL_PACKAGE = URL_PACKAGES + "/{reference}";
    private static final String URL_APPROVAL = URL_PACKAGE + "/approve/{approval}";
    private static final String URL_LICENSE = URL_PACKAGE + "/license/{license}";
    private static final String URL_EXEMPT = URL_LICENSE + "/exempt";
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

        mvc.perform(get(URL_PACKAGES + "?id={fragment}", FRAGMENT))
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
        final var body = new JSONObject().put("rationale", RATIONALE);

        mvc.perform(post(URL_EXEMPT, REFERENCE_ENCODED, LICENSE)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).exemptLicense(REFERENCE, LICENSE, RATIONALE);
    }

    @Test
    void addsLicenseExceptionWithoutRationale() throws Exception {
        mvc.perform(post(URL_EXEMPT, REFERENCE_ENCODED, LICENSE))
                .andExpect(status().isOk());

        verify(service).exemptLicense(REFERENCE, LICENSE, "");
    }

    @Test
    void revokesLicenseExemption() throws Exception {
        mvc.perform(post(URL_EXEMPT + "?revoke=yes", REFERENCE_ENCODED, LICENSE))
                .andExpect(status().isOk());

        verify(service).revokeLicenseExemption(REFERENCE, LICENSE);
    }
}
