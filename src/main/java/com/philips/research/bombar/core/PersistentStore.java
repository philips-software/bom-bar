/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersistentStore {

    /**
     * @return all projects
     */
    @Deprecated
    List<Project> getProjects();

    /**
     * Finds projects matching the given name.
     *
     * @param fragment (case-insensitive) fragment of the name
     * @return any matching projects
     */
    List<Project> findProjects(String fragment);

    /**
     * @return new project
     */
    Project createProject();

    /**
     * @return the project with the provided id
     */
    Optional<Project> getProject(UUID projectId);

    /**
     * Creates a new package definition.
     *
     * @param reference PURL compatible package reference
     * @return the requested package definition
     */
    Package createPackageDefinition(URI reference);

    /**
     * @return existing package definition
     */
    Optional<Package> getPackageDefinition(URI reference);

    /**
     * @param fragment part of a reference
     * @return all packages with a reference containing the fragment
     */
    List<Package> findPackageDefinitions(String fragment);

    /**
     * Creates a new persisted dependency.
     *
     * @param project context for the dependency
     * @param id      identification within the project
     * @param title   human readable identification
     * @return a persisted dependency
     */
    Dependency createDependency(Project project, @NullOr String id, String title);

    /**
     * @return the project containing the dependency
     */
    Project getProjectFor(Dependency dependency);

    /**
     * Lists all dependencies that map to a version of a package.
     *
     * @param pkg the package definition
     * @return all matching dependencies
     */
    List<Dependency> findDependencies(Package pkg);

    /**
     * Delete all dependencies for a project.
     *
     * @param project owner of the dependencies
     */
    void deleteDependencies(Project project);
}
