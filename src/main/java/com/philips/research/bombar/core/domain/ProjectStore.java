/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectStore {

    /**
     * @return all projects
     */
    List<Project> getProjects();

    /**
     * @return new project
     */
    Project createProject();

    /**
     * @return the project with the provided id
     */
    Optional<Project> readProject(UUID projectId);

    /**
     * Creates a new package or returns the existing package definition.
     *
     * @param reference PURL compatible package reference
     * @return the requested package definition
     */
    PackageDefinition getOrCreatePackageDefinition(String reference);

    /**
     * Creates a new persisted dependency.
     *
     * @param id    identification within the project
     * @param title human readable identification
     * @return a persisted dependency
     */
    Dependency createDependency(String id, String title);

    /**
     * Creates a new persisted dependency relation.
     *
     * @param type   type of the relation
     * @param target target dependency of the relation
     * @return a persisted relation
     */
    Relation createRelation(Relation.Type type, Dependency target);
}
