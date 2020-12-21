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
class DependencyEntity extends Dependency {
    @ManyToOne(targetEntity = ProjectEntity.class)
    Project project;
    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long id;

    @SuppressWarnings("unused")
    private DependencyEntity() {
        //noinspection ConstantConditions
        this(null, "");
    }

    DependencyEntity(@NullOr String key, String title) {
        super(key, title);
    }
}
