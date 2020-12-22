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

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.*;

@Entity
@Table(name = "dependencies")
@SuppressWarnings({"JpaObjectClassSignatureInspection", "JpaDataSourceORMInspection"})
class DependencyEntity extends Dependency {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @ManyToOne(targetEntity = ProjectEntity.class, fetch = FetchType.LAZY)
    Project project;

    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long id;

    @SuppressWarnings("unused")
    DependencyEntity() {
        this(null, "");
    }

    DependencyEntity(@NullOr String key, String title) {
        super(key, title);
    }
}
