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
