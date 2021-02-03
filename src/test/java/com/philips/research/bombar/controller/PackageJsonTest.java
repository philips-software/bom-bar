/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.PackageService.PackageDto;
import com.philips.research.bombar.core.ProjectService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PackageJsonTest {
    private static final String NAME = "Name";
    private static final URI REFERENCE = URI.create("Reference");
    private static final String VENDOR = "Vendor";
    private static final URI HOMEPAGE = URI.create("https://example.com");
    private static final String DESCRIPTION = "Description";
    private static final String LICENSE = "License";
    private static final UUID PROJECT_ID = UUID.randomUUID();

    @Test
    void createsInstanceFromDto() throws Exception {
        final var dto = new PackageDto();
        dto.name = NAME;
        dto.reference = REFERENCE;
        dto.vendor = VENDOR;
        dto.homepage = HOMEPAGE.toURL();
        dto.description = DESCRIPTION;
        dto.approval = PackageService.Approval.CONTEXT;
        dto.licenseExemptions = List.of(LICENSE);

        final var json = new PackageJson(dto);

        assertThat(json.id).isEqualTo(REFERENCE.toString());
        assertThat(json.reference).isEqualTo(REFERENCE);
        assertThat(json.name).isEqualTo(NAME);
        assertThat(json.vendor).isEqualTo(VENDOR);
        assertThat(json.homepage).isEqualTo(HOMEPAGE.toURL());
        assertThat(json.description).isEqualTo(DESCRIPTION);
        assertThat(json.approval).isEqualTo(PackageService.Approval.CONTEXT.toString().toLowerCase());
        assertThat(json.exemptions).contains(LICENSE);
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

    @Test
    void addsProjects() {
        final var packageDto = new PackageDto();
        packageDto.reference = REFERENCE;
        packageDto.approval = PackageService.Approval.APPROVED;
        final var json = new PackageJson(packageDto);
        final var projectDto = new ProjectService.ProjectDto(PROJECT_ID);

        json.setProjects(List.of(projectDto));

        assertThat(json.projects).hasSize(1);
        //noinspection ConstantConditions
        assertThat(json.projects.get(0).id).isEqualTo(PROJECT_ID);
    }
}
