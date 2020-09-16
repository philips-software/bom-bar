package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

class PackageJson {
    public String id;
    public String title;
    public String license;
    public String relation;
    public List<PackageJson> children;

    @SuppressWarnings("unused")
    PackageJson() {
    }

    PackageJson(ProjectService.PackageDto dto) {
        this.id = dto.reference;
        this.title = dto.title;
        this.relation = dto.relation;
        this.license = dto.license;
        this.children = toList(dto.children);
    }

    static List<PackageJson> toList(List<ProjectService.PackageDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        return dtoList.stream().map(PackageJson::new).collect(Collectors.toList());
    }
}
