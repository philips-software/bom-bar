/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence.database;

import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
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
