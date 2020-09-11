package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;

import java.util.UUID;

class ProjectJson {
    public UUID uuid;
    public String name;

    @SuppressWarnings("unused")
    public ProjectJson() {
    }

    public ProjectJson(ProjectService.ProjectDto dto) {
        this.uuid = dto.uuid;
        this.name = dto.name;
    }
}
