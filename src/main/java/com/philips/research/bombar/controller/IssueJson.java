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
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class IssueJson {
    @NullOr String id;
    String title;
    String description;

    public IssueJson(ProjectService.ViolationDto dto) {
        if (dto.reference != null) {
            this.id = URLEncoder.encode(dto.reference, StandardCharsets.UTF_8);
        }
        this.title = dto.dependency;
        this.description = dto.violation;
    }

    static @NullOr List<IssueJson> toList(@NullOr List<ProjectService.ViolationDto> violations) {
        if (violations == null) return null;
        return violations.stream().map(IssueJson::new).collect(Collectors.toList());
    }
}
