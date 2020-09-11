package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
@RequestMapping("/projects")
public class ProjectsRoute {
    private final ProjectService service;

    public ProjectsRoute(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectJson> createProject(@RequestBody ProjectJson project, HttpServletRequest request) {
        final var result = service.createProject(project.name);
        final var location = URI.create(request.getRequestURI() + '/' + result.uuid);
        return ResponseEntity.created(location).body(new ProjectJson(result));
    }
}
