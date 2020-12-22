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

import com.philips.research.bombar.core.domain.PackageDefinition;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.persistence.*;
import java.net.URI;

@Entity
@Table(name = "packages")
@SuppressWarnings("JpaDataSourceORMInspection")
public class PackageDefinitionEntity extends PackageDefinition {
    private static final URI NO_URI = URI.create("");

    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long _Id;

    // Used for querying database on string match
    @SuppressWarnings({"unused", "NotNullFieldNotInitialized"})
    @Column(name = "reference", insertable = false, updatable = false)
    private String search;

    @SuppressWarnings("unused")
    PackageDefinitionEntity() {
        super(NO_URI);
    }

    public PackageDefinitionEntity(URI reference) {
        super(reference);
    }
}
