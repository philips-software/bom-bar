/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core;

import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ProjectService {
    /**
     * Searches for projects by name. (Use "" to list the most recent projects.)
     *
     * @param fragment case-insensitive part of the project name
     * @param limit    maximum number of results
     * @return list of matching projects, sorted by SBOM upload date
     */
    List<ProjectDto> findProjects(String fragment, int limit);

    /**
     * Creates a new project.
     *
     * @param title Assigned name of the project
     * @return project definition
     */
    ProjectDto createProject(@NullOr String title);

    /**
     * @return the indicated project
     */
    ProjectDto findProject(UUID projectId);

    /**
     * Updates project settings.
     *
     * @param dto updated fields for project
     * @return result of the update
     */
    ProjectDto updateProject(ProjectDto dto);

    /**
     * Updates a project from an SPDX tag-value file.
     *
     * @param stream file content
     */
    void importSpdx(UUID projectId, InputStream stream);

    /**
     * @return all package of the indicated project
     */
    List<DependencyDto> findDependencies(UUID projectId);

    /**
     * @return the indicated package
     */
    DependencyDto findDependency(UUID projectId, String dependencyId);

    /**
     * Suppress violations for dependency.
     *
     * @param dependencyId reference of dependency
     * @param rationale    Explanation, or <code>null</code> to remove exemption
     */
    void exempt(UUID projectId, String dependencyId, @NullOr String rationale);

    /**
     * Find all project uses of a package.
     *
     * @param packageReference the package
     * @return the projects including dependencies referencing the package.
     */
    List<ProjectDto> findPackageUse(URI packageReference);

    /**
     * Returns distribution of licenses across packages of a project.
     *
     * @param projectId target project
     * @return map from license name to frequency of occurrence
     */
    Map<String, Integer> licenseDistribution(UUID projectId);

    /**
     * Returns lists of all dependencies per obligation for a project
     *
     * @param projectId target project
     * @return map from obligation to the project dependencies
     */
    Map<String, Set<DependencyDto>> findObligations(UUID projectId);

    class ProjectDto {
        public final UUID id;
        public @NullOr String title;
        public @NullOr Instant updated;
        public @NullOr String distribution;
        public @NullOr String phase;
        public int issues;
        public @NullOr List<DependencyDto> packages;

        public ProjectDto(UUID id) {
            this.id = id;
        }
    }

    class DependencyDto {
        public final String id;
        public @NullOr URI purl;
        public String title = "";
        public String version = "";
        public @NullOr String license;
        public @NullOr String relation;
        public PackageService.@NullOr PackageDto pkg;
        public int issues;
        public boolean isRoot;
        public boolean isDevelopment;
        public boolean isDelivered;
        public @NullOr List<String> violations;
        public @NullOr List<DependencyDto> dependencies;
        public @NullOr List<DependencyDto> usages;
        public @NullOr String exemption;

        public DependencyDto(String id) {
            this.id = id;
        }
    }
}

