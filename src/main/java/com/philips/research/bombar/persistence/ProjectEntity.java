/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Access(AccessType.FIELD)
@Table(name = "projects")
@SuppressWarnings({"JpaDataSourceORMInspection", "JpaObjectClassSignatureInspection"})
class ProjectEntity extends Project {
    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long id;

    @SuppressWarnings("unused")
    ProjectEntity() {
        //noinspection ConstantConditions
        super(null);
    }

    ProjectEntity(UUID uuid) {
        super(uuid);
    }
}
