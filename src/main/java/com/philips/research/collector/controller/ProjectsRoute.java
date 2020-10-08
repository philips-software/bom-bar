/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectsRoute {
    private final ProjectService service;

    public ProjectsRoute(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectJson> createProject(@RequestBody ProjectJson project, HttpServletRequest request) {
        final var result = service.createProject(project.title);
        final var location = URI.create(request.getRequestURI() + '/' + result.id);
        return ResponseEntity.created(location).body(new ProjectJson(result));
    }

    @GetMapping("{projectId}")
    public ProjectJson getProject(@PathVariable UUID projectId) {
        final var result = service.project(projectId);
        return new ProjectJson(result);
    }

    @PostMapping("{projectId}/upload")
    public void uploadSpdx(@PathVariable UUID projectId, @RequestParam("file") MultipartFile file) {
        try {
            service.importSpdx(projectId, file.getInputStream());
        } catch (IOException e) {
            throw new WebServerException("File upload failed", e);
        }
    }

    @GetMapping("{projectId}/packages")
    public ResultJson<PackageJson> readPackages(@PathVariable UUID projectId) {
        final var result = service.packages(projectId);
        //noinspection ConstantConditions
        return new ResultJson<>(PackageJson.toList(result));
    }
}
