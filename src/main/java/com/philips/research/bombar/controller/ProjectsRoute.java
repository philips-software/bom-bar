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
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/projects")
public class ProjectsRoute {
    private final ProjectService service;

    public ProjectsRoute(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public ResultListJson<ProjectJson> getProjects() {
        final var result = service.projects();
        //noinspection ConstantConditions
        return new ResultListJson<>(ProjectJson.toList(result));
    }

    @PostMapping
    public ResponseEntity<ProjectJson> createProject(@RequestBody ProjectJson project, HttpServletRequest request) {
        final var result = service.createProject(project.title);
        final var location = URI.create(request.getRequestURI() + '/' + result.id);
        return ResponseEntity.created(location).body(new ProjectJson(result));
    }

    @GetMapping("{projectId}")
    public ProjectJson getProject(@PathVariable UUID projectId) {
        final var result = service.getProject(projectId);
        return new ProjectJson(result);
    }

    @PutMapping("{projectId}")
    public ProjectJson updateProject(@PathVariable UUID projectId, @RequestBody ProjectJson project) {
        final var result = service.updateProject(project.toDto(projectId));
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

    @GetMapping("{projectId}/dependencies")
    public ResultListJson<DependencyJson> readPackages(@PathVariable UUID projectId) {
        final var result = service.getDependencies(projectId);
        //noinspection ConstantConditions
        return new ResultListJson<>(DependencyJson.toList(result));
    }

    @GetMapping("{projectId}/dependencies/{dependencyId}")
    public DependencyJson readDependency(@PathVariable UUID projectId, @PathVariable String dependencyId) {
        final var result = service.getDependency(projectId, dependencyId);
        return new DependencyJson(result);
    }
}
