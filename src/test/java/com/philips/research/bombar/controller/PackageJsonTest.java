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

import com.philips.research.bombar.core.PackageService.PackageDto;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PackageJsonTest {

    private static final String NAME = "Name";
    private static final String REFERENCE = "Reference";
    private static final String VENDOR = "Vendor";
    private static final URI HOMEPAGE = URI.create("https://example.com");
    private static final String LICENSE = "License";
    private static final String RATIONALE = "Rationale";

    @Test
    void createsInstanceFromDto() throws Exception {
        final var dto = new PackageDto();
        dto.name = NAME;
        dto.reference = REFERENCE;
        dto.vendor = VENDOR;
        dto.homepage = HOMEPAGE.toURL();
        dto.licenseExemptions = Map.of(LICENSE, RATIONALE);

        final var json = new PackageJson(dto);

        assertThat(json.id).isEqualTo(REFERENCE);
        assertThat(json.name).isEqualTo(NAME);
        assertThat(json.vendor).isEqualTo(VENDOR);
        assertThat(json.homepage).isEqualTo(HOMEPAGE.toURL());
        assertThat(json.exemptions).isEqualTo(Map.of(LICENSE, RATIONALE));
    }
}
