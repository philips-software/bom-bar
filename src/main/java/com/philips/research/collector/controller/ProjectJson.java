package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;

import java.util.UUID;

class ProjectJson {
    public UUID id;
    public String title;

    @SuppressWarnings("unused")
    public ProjectJson() {
    }

    public ProjectJson(ProjectService.ProjectDto dto) {
        this.id = dto.id;
        this.title = dto.title;
    }
}
