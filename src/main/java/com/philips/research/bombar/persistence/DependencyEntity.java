/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = "dependencies")
@SuppressWarnings({"JpaObjectClassSignatureInspection", "JpaDataSourceORMInspection"})
class DependencyEntity extends Dependency {
    @ManyToOne(targetEntity = ProjectEntity.class, fetch = FetchType.LAZY)
    final Project project;

    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long id;

    @SuppressWarnings("unused")
    DependencyEntity() {
        //noinspection ConstantConditions
        this(null, null, "");
    }

    DependencyEntity(Project project, @NullOr String key, String title) {
        super(key, title);
        this.project = project;
    }
}
