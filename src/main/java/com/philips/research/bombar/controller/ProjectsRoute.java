/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
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
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/projects")
public class ProjectsRoute extends BaseRoute {
    public ProjectsRoute(ProjectService service) {
        super(service);
    }

    @GetMapping
    public ResultListJson<ProjectJson> getProjects() {
        final var result = projectService.projects();
        //noinspection ConstantConditions
        return new ResultListJson<>(ProjectJson.toList(result));
    }

    @PostMapping
    public ResponseEntity<ProjectJson> createProject(@RequestBody ProjectJson project, HttpServletRequest request) {
        final var result = projectService.createProject(project.title);
        final var location = URI.create(request.getRequestURI() + '/' + result.id);
        return ResponseEntity.created(location).body(new ProjectJson(result));
    }

    @GetMapping("{projectId}")
    public ProjectJson getProject(@PathVariable UUID projectId) {
        final var result = projectService.getProject(projectId);
        return new ProjectJson(result);
    }

    @PutMapping("{projectId}")
    public ProjectJson updateProject(@PathVariable UUID projectId, @RequestBody ProjectJson project) {
        final var result = projectService.updateProject(project.toDto(projectId));
        return new ProjectJson(result);
    }

    @PostMapping("{projectId}/upload")
    public void uploadSpdx(@PathVariable UUID projectId, @RequestParam("file") MultipartFile file) {
        try {
            projectService.importSpdx(projectId, file.getInputStream());
        } catch (IOException e) {
            throw new WebServerException("File upload failed", e);
        }
    }

    @GetMapping("{projectId}/dependencies")
    public ResultListJson<DependencyJson> readPackages(@PathVariable UUID projectId) {
        final var result = projectService.getDependencies(projectId);
        //noinspection ConstantConditions
        return new ResultListJson<>(DependencyJson.toList(result));
    }

    @GetMapping("{projectId}/dependencies/{dependencyId}")
    public DependencyJson readDependency(@PathVariable UUID projectId, @PathVariable String dependencyId) {
        final var result = projectService.getDependency(projectId, dependencyId);
        return new DependencyJson(result);
    }

    @PostMapping("{projectId}/dependencies/{dependencyId}/source")
    public void setPackageSource(@PathVariable UUID projectId, @PathVariable String dependencyId) {
        projectService.setSourcePackage(projectId, dependencyId, true);
    }

    @DeleteMapping("{projectId}/dependencies/{dependencyId}/source")
    public void resetPackageSource(@PathVariable UUID projectId, @PathVariable String dependencyId) {
        projectService.setSourcePackage(projectId, dependencyId, false);
    }

    @PostMapping("{projectId}/exempt/{id}")
    public void exempt(@PathVariable UUID projectId, @PathVariable String id, @RequestBody ExemptionJson body) {
        projectService.exempt(projectId, toReference(id), body.rationale);
    }

    @DeleteMapping("{projectId}/exempt/{id}")
    public void exempt(@PathVariable UUID projectId, @PathVariable String id) {
        projectService.exempt(projectId, toReference(id), null);
    }

    @GetMapping("{projectId}/licenses")
    public Map<String, Integer> readLicenseDistribution(@PathVariable UUID projectId) {
        return projectService.licenseDistribution(projectId);
    }
}
