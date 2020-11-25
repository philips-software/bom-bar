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
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PackageJsonTest {
    private static final String NAME = "Name";
    private static final URI REFERENCE = URI.create("Reference");
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
        dto.approval = PackageService.Approval.CONTEXT;
        dto.licenseExemptions = Map.of(LICENSE, RATIONALE);

        final var json = new PackageJson(dto);

        assertThat(json.id).isEqualTo(REFERENCE.toString());
        assertThat(json.reference).isEqualTo(REFERENCE);
        assertThat(json.name).isEqualTo(NAME);
        assertThat(json.vendor).isEqualTo(VENDOR);
        assertThat(json.homepage).isEqualTo(HOMEPAGE.toURL());
        assertThat(json.approval).isEqualTo(PackageService.Approval.CONTEXT.toString().toLowerCase());
        assertThat(json.exemptions).isEqualTo(Map.of(LICENSE, RATIONALE));
    }

    @Test
    void createsInstancesFromListOfDto() {
        final var dto = new PackageDto();
        dto.reference = REFERENCE;
        dto.approval = PackageService.Approval.CONTEXT;

        final var list = PackageJson.toList(List.of(dto));

        assertThat(list).hasSize(1);
        assertThat(list.get(0).reference).isEqualTo(REFERENCE);
    }
}
