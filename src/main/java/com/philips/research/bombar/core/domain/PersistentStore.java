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

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersistentStore {

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
     * Creates a new package definition.
     *
     * @param reference PURL compatible package reference
     * @return the requested package definition
     */
    PackageDefinition createPackageDefinition(URI reference);

    /**
     * @return existing package definition
     */
    Optional<PackageDefinition> getPackageDefinition(URI reference);

    /**
     * @param fragment part of a reference
     * @return all packages with a reference containing the fragment
     */
    List<PackageDefinition> findPackageDefinitions(String fragment);

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
    Relation createRelation(Relation.Relationship type, Dependency target);
}
