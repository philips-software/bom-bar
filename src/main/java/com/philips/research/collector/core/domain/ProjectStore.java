/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Optional;
import java.util.UUID;

public interface ProjectStore {
    Project createProject();

    Optional<Project> readProject(UUID uuid);

    PackageDefinition getOrCreatePackageDefinition(String reference);

    Dependency createDependency(@NullOr PackageDefinition pkg, String version);

    Relation createRelation(Relation.Type type, Dependency target);
}
