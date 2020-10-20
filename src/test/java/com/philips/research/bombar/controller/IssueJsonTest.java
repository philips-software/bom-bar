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

import com.philips.research.bombar.core.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueJsonTest {

    private static final String REFERENCE = "Reference";
    private static final String TITLE = "Title";
    private static final String VIOLATION = "Violation";

    @Test
    void createsInstance() {
        final var dto = new ProjectService.ViolationDto();
        dto.reference = REFERENCE;
        dto.dependency = TITLE;
        dto.violation = VIOLATION;

        final var json = new IssueJson(dto);

        assertThat(json.id).isEqualTo(REFERENCE);
        assertThat(json.title).isEqualTo(TITLE);
        assertThat(json.description).isEqualTo(VIOLATION);
    }

    @Test
    void convertsNullList() {
        assertThat(IssueJson.toList(null)).isNull();
    }

    @Test
    void convertsList() {
        final var dto = new ProjectService.ViolationDto();
        dto.reference = REFERENCE;

        //noinspection ConstantConditions
        final var json = IssueJson.toList(List.of(dto));

        assert json != null;
        assertThat(json).hasSize(1);
        assertThat(json.get(0).id).isEqualTo(REFERENCE);
    }
}
