/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.persistence;

import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.PackageRef;
import pl.tlinkowski.annotation.basic.NullOr;

import jakarta.persistence.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = "packages")
@SuppressWarnings({"JpaDataSourceORMInspection", "JpaObjectClassSignatureInspection"})
public class PackageEntity extends Package {
    private static final PackageRef EMPTY_REF = new PackageRef("");

    @Id
    @GeneratedValue
    @SuppressWarnings({"unused", "RedundantSuppression"})
    private @NullOr Long id;

    // Used for querying database on string match
    @SuppressWarnings({"unused", "NotNullFieldNotInitialized"})
    @Column(name = "reference", insertable = false, updatable = false)
    private String search;

    @SuppressWarnings("unused")
    PackageEntity() {
        super(EMPTY_REF);
    }

    public PackageEntity(PackageRef reference) {
        super(reference);
    }
}
