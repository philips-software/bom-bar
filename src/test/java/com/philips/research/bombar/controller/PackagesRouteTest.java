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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class PackagesRouteTest {
    private static final String REFERENCE = "Reference";
    private static final String LICENSE = "Some License";
    private static final String RATIONALE = "Rationale";
    private static final String URL_PACKAGE = "/packages";
    private static final String URL_LICENSE = URL_PACKAGE + "/{reference}/license/{license}";
    private static final String URL_EXEMPT = URL_LICENSE + "/exempt";

    @MockBean
    private PackageService service;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(service);
    }

    @Test
    void addsLicenseException() throws Exception {
        final var body = new JSONObject().put("rationale", RATIONALE);

        mvc.perform(post(URL_EXEMPT, REFERENCE, LICENSE)
                .content(body.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).exemptLicense(REFERENCE, LICENSE, RATIONALE);
    }

    @Test
    void addsLicenseExceptionWithoutRationale() throws Exception {
        mvc.perform(post(URL_EXEMPT, REFERENCE, LICENSE))
                .andExpect(status().isOk());

        verify(service).exemptLicense(REFERENCE, LICENSE, "");
    }

    @Test
    void revokesLicenseExemption() throws Exception {
        mvc.perform(post(URL_EXEMPT + "?revoke=yes", REFERENCE, LICENSE))
                .andExpect(status().isOk());

        verify(service).revokeLicenseExemption(REFERENCE, LICENSE);
    }
}
