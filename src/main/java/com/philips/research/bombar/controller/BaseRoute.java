/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.ProjectService;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class BaseRoute {
    protected final ProjectService projectService;

    public BaseRoute(ProjectService projectService) {
        this.projectService = projectService;
    }

    URI toReference(String id) {
        return URI.create(URLDecoder.decode(id, StandardCharsets.UTF_8));
    }
}
